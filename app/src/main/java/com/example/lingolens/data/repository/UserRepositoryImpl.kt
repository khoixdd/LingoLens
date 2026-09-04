package com.example.lingolens.data.repository

import android.content.Context
import android.os.SystemClock
import android.util.Log
import com.example.lingolens.domain.model.AuthUser
import com.example.lingolens.domain.model.LeaderboardLoadState
import com.example.lingolens.domain.model.UserProfile
import com.example.lingolens.domain.repository.UserRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.Dispatchers
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

    override fun observeLeaderboard(): Flow<LeaderboardLoadState> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(LeaderboardLoadState.Error("Firebase is unavailable on this device."))
            close()
            return@callbackFlow
        }

        val startedAt = SystemClock.elapsedRealtime()
        var loggedCacheResult = false
        var loggedServerResult = false
        Log.d(TAG, "Leaderboard listener started")
        trySend(LeaderboardLoadState.Loading)
        val listener = db.collection("users")
            .orderBy("xp", Query.Direction.DESCENDING)
            .limit(10)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, error ->
                if (error != null || snapshot == null) {
                    Log.e(TAG, "Unable to load leaderboard after ${SystemClock.elapsedRealtime() - startedAt} ms", error)
                    trySend(
                        LeaderboardLoadState.Error(
                            "Unable to load the leaderboard. Check your internet connection and Firestore Rules.",
                        ),
                    )
                    return@addSnapshotListener
                }
                val list = snapshot.documents.map { doc ->
                    mapDocumentToUserProfile(doc.id, doc.data.orEmpty())
                }
                val isFromCache = snapshot.metadata.isFromCache
                val elapsedMs = SystemClock.elapsedRealtime() - startedAt
                if (isFromCache && !loggedCacheResult) {
                    loggedCacheResult = true
                    Log.d(TAG, "Leaderboard cache result received in $elapsedMs ms")
                } else if (!isFromCache && !loggedServerResult) {
                    loggedServerResult = true
                    Log.d(TAG, "Leaderboard server result received in $elapsedMs ms")
                }
                trySend(LeaderboardLoadState.Data(list, isFromCache))
            }

        awaitClose { listener.remove() }
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

    override fun syncUserProfileOnLogin(user: AuthUser) {
        val db = firestore ?: return
        if (user.uid.isBlank()) return
        val startedAt = SystemClock.elapsedRealtime()
        val docRef = db.collection("users").document(user.uid)
        docRef.get()
            .addOnSuccessListener { snapshot ->
                val now = System.currentTimeMillis()
                val defaultName = user.displayName.ifBlank { user.email.substringBefore("@") }.ifBlank { "Learner" }
                val updates: Map<String, Any> = if (!snapshot.exists() || snapshot.getLong("xp") == null) {
                    hashMapOf(
                        "uid" to user.uid,
                        "username" to defaultName,
                        "email" to user.email,
                        "avatarUrl" to user.photoUrl,
                        "xp" to 100,
                        "level" to 1,
                        "streakDays" to 1,
                        "totalWords" to 0,
                        "latitude" to 10.762622,
                        "longitude" to 106.682221,
                        "isSharingLocation" to false,
                        "createdAt" to now,
                        "lastLoginAt" to now,
                    )
                } else {
                    buildMap {
                        put("lastLoginAt", now)
                        if (user.displayName.isNotBlank()) put("username", user.displayName)
                        if (user.email.isNotBlank()) put("email", user.email)
                        if (user.photoUrl.isNotBlank()) put("avatarUrl", user.photoUrl)
                    }
                }
                docRef.set(updates, SetOptions.merge())
                    .addOnSuccessListener {
                        Log.d(TAG, "Profile sync completed in ${SystemClock.elapsedRealtime() - startedAt} ms")
                    }
                    .addOnFailureListener { error -> Log.e(TAG, "Profile write failed", error) }
            }
            .addOnFailureListener { error -> Log.e(TAG, "Profile lookup failed", error) }
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
                docRef.set(data, SetOptions.merge()).await()
            }
        }
    }

    override suspend fun syncTotalWords(uid: String, totalWords: Int) {
        val db = firestore ?: return
        if (uid.isBlank()) return
        withContext(Dispatchers.IO) {
            runCatching {
                val docRef = db.collection("users").document(uid)
                docRef.set(mapOf("totalWords" to totalWords), SetOptions.merge()).await()
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
                ).await()
            }
        }
    }

    private fun mapDocumentToUserProfile(docId: String, data: Map<String, Any>): UserProfile {
        val currentUser = auth?.currentUser
        val emailPrefix = currentUser?.email?.substringBefore("@").orEmpty()
        val defaultName = currentUser?.displayName.orEmpty().ifBlank { emailPrefix }.ifBlank { "Learner" }

        val docUid = (data["uid"] as? String).orEmpty().ifBlank { docId }
        val rawUsername = (data["username"] as? String).orEmpty()
        val finalUsername = if (docUid == currentUser?.uid && rawUsername.isBlank()) {
            defaultName
        } else {
            rawUsername.ifBlank { (data["email"] as? String).orEmpty().substringBefore("@") }.ifBlank { "Learner" }
        }

        return UserProfile(
            uid = docUid,
            username = finalUsername,
            email = (data["email"] as? String).orEmpty().ifBlank { if (docUid == currentUser?.uid) currentUser?.email.orEmpty() else "" },
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

    private companion object {
        const val TAG = "UserRepository"
    }
}
