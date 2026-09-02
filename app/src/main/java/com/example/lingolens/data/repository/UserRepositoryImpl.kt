package com.example.lingolens.data.repository

import android.content.Context
import com.example.lingolens.domain.model.AuthUser
import com.example.lingolens.domain.model.UserProfile
import com.example.lingolens.domain.repository.UserRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

@Singleton
class UserRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : UserRepository {

    private val firestore: FirebaseFirestore? by lazy {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
            FirebaseFirestore.getInstance()
        } catch (_: Exception) {
            null
        }
    }

    override fun observeUserProfile(uid: String): Flow<UserProfile?> = callbackFlow {
        val db = firestore
        if (db == null || uid.isBlank()) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listener = db.collection("users").document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) {
                    trySend(null)
                    return@addSnapshotListener
                }
                val profile = UserProfile(
                    uid = snapshot.getString("uid").orEmpty().ifBlank { uid },
                    username = snapshot.getString("username").orEmpty().ifBlank { "Learner" },
                    email = snapshot.getString("email").orEmpty(),
                    avatarUrl = snapshot.getString("avatarUrl").orEmpty(),
                    xp = snapshot.getLong("xp")?.toInt() ?: 100,
                    level = snapshot.getLong("level")?.toInt() ?: 1,
                    streakDays = snapshot.getLong("streakDays")?.toInt() ?: 1,
                    totalWords = snapshot.getLong("totalWords")?.toInt() ?: 0,
                    createdAt = snapshot.getLong("createdAt") ?: System.currentTimeMillis(),
                    lastLoginAt = snapshot.getLong("lastLoginAt") ?: System.currentTimeMillis(),
                )
                trySend(profile)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun getUserProfile(uid: String): UserProfile? {
        val db = firestore ?: return null
        if (uid.isBlank()) return null
        return try {
            val snapshot = db.collection("users").document(uid).get().await()
            if (!snapshot.exists()) return null
            UserProfile(
                uid = snapshot.getString("uid").orEmpty().ifBlank { uid },
                username = snapshot.getString("username").orEmpty().ifBlank { "Learner" },
                email = snapshot.getString("email").orEmpty(),
                avatarUrl = snapshot.getString("avatarUrl").orEmpty(),
                xp = snapshot.getLong("xp")?.toInt() ?: 100,
                level = snapshot.getLong("level")?.toInt() ?: 1,
                streakDays = snapshot.getLong("streakDays")?.toInt() ?: 1,
                totalWords = snapshot.getLong("totalWords")?.toInt() ?: 0,
                createdAt = snapshot.getLong("createdAt") ?: System.currentTimeMillis(),
                lastLoginAt = snapshot.getLong("lastLoginAt") ?: System.currentTimeMillis(),
            )
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun syncUserProfileOnLogin(user: AuthUser) {
        val db = firestore ?: return
        if (user.uid.isBlank()) return
        runCatching {
            val docRef = db.collection("users").document(user.uid)
            val snapshot = docRef.get().await()
            val now = System.currentTimeMillis()

            if (!snapshot.exists()) {
                val newProfile = hashMapOf(
                    "uid" to user.uid,
                    "username" to user.displayName.ifBlank { "Learner" },
                    "email" to user.email,
                    "avatarUrl" to user.photoUrl,
                    "xp" to 100,
                    "level" to 1,
                    "streakDays" to 1,
                    "totalWords" to 0,
                    "createdAt" to now,
                    "lastLoginAt" to now,
                )
                docRef.set(newProfile, SetOptions.merge()).await()
            } else {
                val updates = hashMapOf<String, Any>(
                    "lastLoginAt" to now,
                )
                if (user.displayName.isNotBlank()) {
                    updates["username"] = user.displayName
                }
                if (user.email.isNotBlank()) {
                    updates["email"] = user.email
                }
                docRef.set(updates, SetOptions.merge()).await()
            }
        }
    }

    override suspend fun addXp(uid: String, xpAmount: Int) {
        val db = firestore ?: return
        if (uid.isBlank() || xpAmount <= 0) return
        runCatching {
            val docRef = db.collection("users").document(uid)
            val snapshot = docRef.get().await()
            val currentXp = if (snapshot.exists()) (snapshot.getLong("xp")?.toInt() ?: 100) else 100
            val newXp = currentXp + xpAmount
            val newLevel = (newXp / 200) + 1
            val data = hashMapOf<String, Any>(
                "uid" to uid,
                "xp" to newXp,
                "level" to newLevel,
                "lastLoginAt" to System.currentTimeMillis(),
            )
            docRef.set(data, SetOptions.merge()).await()
        }
    }
}
