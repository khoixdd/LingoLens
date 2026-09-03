package com.example.lingolens.data.repository

import android.content.Context
import com.example.lingolens.domain.model.AuthUser
import com.example.lingolens.domain.model.UserProfile
import com.example.lingolens.domain.repository.UserRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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

    private val auth: FirebaseAuth? by lazy {
        try {
            FirebaseAuth.getInstance()
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
                trySend(mapDocumentToUserProfile(snapshot.id, snapshot.data.orEmpty()))
            }

        awaitClose { listener.remove() }
    }

    override fun observeLeaderboard(): Flow<List<UserProfile>> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        seedSampleLeaderboardIfEmpty(db)

        val listener = db.collection("users")
            .orderBy("xp", Query.Direction.DESCENDING)
            .limit(10)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot.documents.map { doc ->
                    mapDocumentToUserProfile(doc.id, doc.data.orEmpty())
                }
                trySend(list)
            }

        awaitClose { listener.remove() }
    }

    private fun seedSampleLeaderboardIfEmpty(db: FirebaseFirestore) {
        runCatching {
            db.collection("users").limit(3).get().addOnSuccessListener { snapshot ->
                if (snapshot == null || snapshot.isEmpty) {
                    val samples = listOf(
                        hashMapOf(
                            "uid" to "sample_user_a",
                            "username" to "User A",
                            "email" to "usera@example.com",
                            "xp" to 2100,
                            "level" to 11,
                            "streakDays" to 15,
                            "latitude" to 10.765100,
                            "longitude" to 106.685000,
                            "isSharingLocation" to true,
                            "createdAt" to System.currentTimeMillis(),
                        ),
                        hashMapOf(
                            "uid" to "sample_user_b",
                            "username" to "User B",
                            "email" to "userb@example.com",
                            "xp" to 1850,
                            "level" to 10,
                            "streakDays" to 12,
                            "latitude" to 10.760000,
                            "longitude" to 106.680000,
                            "isSharingLocation" to true,
                            "createdAt" to System.currentTimeMillis(),
                        ),
                        hashMapOf(
                            "uid" to "sample_user_c",
                            "username" to "User C",
                            "email" to "userc@example.com",
                            "xp" to 1700,
                            "level" to 9,
                            "streakDays" to 9,
                            "latitude" to 10.763000,
                            "longitude" to 106.683000,
                            "isSharingLocation" to true,
                            "createdAt" to System.currentTimeMillis(),
                        ),
                    )
                    samples.forEach { item ->
                        db.collection("users").document(item["uid"] as String)
                            .set(item, SetOptions.merge())
                    }
                }
            }
        }
    }

    override fun observeNearbyLearners(): Flow<List<UserProfile>> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = db.collection("users")
            .whereEqualTo("isSharingLocation", true)
            .limit(20)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val list = snapshot.documents.map { doc ->
                    mapDocumentToUserProfile(doc.id, doc.data.orEmpty())
                }
                trySend(list)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun getUserProfile(uid: String): UserProfile? {
        val db = firestore ?: return null
        if (uid.isBlank()) return null
        return try {
            val snapshot = db.collection("users").document(uid).get().await()
            if (!snapshot.exists()) return null
            mapDocumentToUserProfile(snapshot.id, snapshot.data.orEmpty())
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun syncUserProfileOnLogin(user: AuthUser) {
        val db = firestore ?: return
        if (user.uid.isBlank()) return
        withContext(Dispatchers.IO) {
            runCatching {
                val docRef = db.collection("users").document(user.uid)
                val now = System.currentTimeMillis()
                val defaultName = user.displayName.ifBlank { user.email.substringBefore("@") }.ifBlank { "Learner" }

                val updates = hashMapOf<String, Any>(
                    "uid" to user.uid,
                    "username" to defaultName,
                    "email" to user.email,
                    "lastLoginAt" to now,
                )
                if (user.photoUrl.isNotBlank()) {
                    updates["avatarUrl"] = user.photoUrl
                }
                docRef.set(updates, SetOptions.merge())
            }
        }
    }

    override suspend fun addXp(uid: String, xpAmount: Int) {
        val db = firestore ?: return
        if (uid.isBlank() || xpAmount <= 0) return
        withContext(Dispatchers.IO) {
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
                docRef.set(data, SetOptions.merge())
            }
        }
    }

    override suspend fun syncTotalWords(uid: String, totalWords: Int) {
        val db = firestore ?: return
        if (uid.isBlank()) return
        withContext(Dispatchers.IO) {
            runCatching {
                val docRef = db.collection("users").document(uid)
                docRef.set(mapOf("totalWords" to totalWords), SetOptions.merge())
            }
        }
    }

    override suspend fun updateUserLocation(
        uid: String,
        lat: Double,
        lng: Double,
        isSharing: Boolean,
    ) {
        val db = firestore ?: return
        if (uid.isBlank()) return
        withContext(Dispatchers.IO) {
            runCatching {
                val docRef = db.collection("users").document(uid)
                docRef.set(
                    mapOf(
                        "latitude" to lat,
                        "longitude" to lng,
                        "isSharingLocation" to isSharing,
                    ),
                    SetOptions.merge(),
                )
            }
        }
    }

    private fun mapDocumentToUserProfile(docId: String, data: Map<String, Any>): UserProfile {
        val currentUser = auth?.currentUser
        val emailPrefix = currentUser?.email?.substringBefore("@").orEmpty()
        val defaultName = currentUser?.displayName.orEmpty().ifBlank { emailPrefix }.ifBlank { "Learner" }

        return UserProfile(
            uid = (data["uid"] as? String).orEmpty().ifBlank { docId },
            username = (data["username"] as? String).orEmpty().ifBlank { defaultName },
            email = (data["email"] as? String).orEmpty().ifBlank { currentUser?.email.orEmpty() },
            avatarUrl = (data["avatarUrl"] as? String).orEmpty(),
            xp = (data["xp"] as? Long)?.toInt() ?: 100,
            level = (data["level"] as? Long)?.toInt() ?: 1,
            streakDays = (data["streakDays"] as? Long)?.toInt() ?: 1,
            totalWords = (data["totalWords"] as? Long)?.toInt() ?: 0,
            latitude = (data["latitude"] as? Double) ?: 10.762622,
            longitude = (data["longitude"] as? Double) ?: 106.682221,
            isSharingLocation = (data["isSharingLocation"] as? Boolean) ?: false,
            createdAt = (data["createdAt"] as? Long) ?: System.currentTimeMillis(),
            lastLoginAt = (data["lastLoginAt"] as? Long) ?: System.currentTimeMillis(),
        )
    }
}
