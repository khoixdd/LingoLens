package com.example.lingolens.data.repository

import android.content.Context
import android.os.SystemClock
import android.util.Log
import com.example.lingolens.domain.model.AuthUser
import com.example.lingolens.domain.repository.AuthRepository
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

@Singleton
class AuthRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : AuthRepository {

    private val auth: FirebaseAuth? by lazy {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
            FirebaseAuth.getInstance()
        } catch (_: Exception) {
            null
        }
    }

    override fun getCurrentUser(): AuthUser? {
        val user = auth?.currentUser ?: return null
        val emailPrefix = user.email?.substringBefore("@").orEmpty()
        val displayName = user.displayName.orEmpty().ifBlank { emailPrefix }.ifBlank { "Learner" }
        return AuthUser(
            uid = user.uid,
            displayName = displayName,
            email = user.email.orEmpty(),
            photoUrl = user.photoUrl?.toString().orEmpty(),
        )
    }

    override fun observeAuthState(): Flow<AuthUser?> = callbackFlow {
        val firebaseAuth = auth
        if (firebaseAuth == null) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listener = FirebaseAuth.AuthStateListener { firebase ->
            val user = firebase.currentUser
            if (user != null) {
                val emailPrefix = user.email?.substringBefore("@").orEmpty()
                val displayName = user.displayName.orEmpty().ifBlank { emailPrefix }.ifBlank { "Learner" }
                trySend(
                    AuthUser(
                        uid = user.uid,
                        displayName = displayName,
                        email = user.email.orEmpty(),
                        photoUrl = user.photoUrl?.toString().orEmpty(),
                    ),
                )
            } else {
                trySend(null)
            }
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override suspend fun loginWithEmail(email: String, password: String): Result<AuthUser> {
        val firebaseAuth = auth ?: return Result.failure(IllegalStateException("Firebase Auth not initialized"))
        val startedAt = SystemClock.elapsedRealtime()
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email.trim(), password).await()
            val user = result.user ?: return Result.failure(IllegalStateException("User is null after sign in"))
            val emailPrefix = user.email?.substringBefore("@").orEmpty()
            val displayName = user.displayName.orEmpty().ifBlank { emailPrefix }.ifBlank { "Learner" }
            Log.d(TAG, "Email authentication completed in ${SystemClock.elapsedRealtime() - startedAt} ms")
            Result.success(
                AuthUser(
                    uid = user.uid,
                    displayName = displayName,
                    email = user.email.orEmpty(),
                    photoUrl = user.photoUrl?.toString().orEmpty(),
                ),
            )
        } catch (e: Exception) {
            Log.w(TAG, "Email authentication failed after ${SystemClock.elapsedRealtime() - startedAt} ms", e)
            Result.failure(e)
        }
    }

    override suspend fun registerWithEmail(
        username: String,
        email: String,
        password: String,
    ): Result<AuthUser> {
        val firebaseAuth = auth ?: return Result.failure(IllegalStateException("Firebase Auth not initialized"))
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email.trim(), password).await()
            val user = result.user ?: return Result.failure(IllegalStateException("User is null after registration"))

            val finalUsername = username.ifBlank { email.substringBefore("@") }
            if (finalUsername.isNotBlank()) {
                val profileUpdate = UserProfileChangeRequest.Builder()
                    .setDisplayName(finalUsername.trim())
                    .build()
                runCatching { user.updateProfile(profileUpdate).await() }
            }

            Result.success(
                AuthUser(
                    uid = user.uid,
                    displayName = finalUsername,
                    email = user.email.orEmpty(),
                    photoUrl = user.photoUrl?.toString().orEmpty(),
                ),
            )
        } catch (e: Exception) {
            Log.w(TAG, "Registration failed", e)
            Result.failure(e)
        }
    }

    override suspend fun loginWithGoogle(idToken: String): Result<AuthUser> {
        val firebaseAuth = auth ?: return Result.failure(IllegalStateException("Firebase Auth not initialized"))
        val startedAt = SystemClock.elapsedRealtime()
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val user = result.user ?: return Result.failure(IllegalStateException("User is null after Google sign in"))
            val emailPrefix = user.email?.substringBefore("@").orEmpty()
            val displayName = user.displayName.orEmpty().ifBlank { emailPrefix }.ifBlank { "Learner" }
            Log.d(TAG, "Google authentication completed in ${SystemClock.elapsedRealtime() - startedAt} ms")
            Result.success(
                AuthUser(
                    uid = user.uid,
                    displayName = displayName,
                    email = user.email.orEmpty(),
                    photoUrl = user.photoUrl?.toString().orEmpty(),
                ),
            )
        } catch (e: Exception) {
            Log.w(TAG, "Google authentication failed after ${SystemClock.elapsedRealtime() - startedAt} ms", e)
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        auth?.signOut()
    }

    private companion object {
        const val TAG = "AuthRepository"
    }
}
