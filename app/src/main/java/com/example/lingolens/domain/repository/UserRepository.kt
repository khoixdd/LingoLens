package com.example.lingolens.domain.repository

import com.example.lingolens.domain.model.AuthUser
import com.example.lingolens.domain.model.LeaderboardLoadState
import com.example.lingolens.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun observeUserProfile(uid: String): Flow<UserProfile?>
    fun observeLeaderboard(): Flow<LeaderboardLoadState>
    fun observeNearbyLearners(): Flow<List<UserProfile>>
    suspend fun getUserProfile(uid: String): UserProfile?
    /** Starts a Firestore profile sync without blocking the caller. */
    fun syncUserProfileOnLogin(user: AuthUser)
    suspend fun addXp(uid: String, xpAmount: Int)
    suspend fun syncTotalWords(uid: String, totalWords: Int)
    suspend fun updateUserLocation(uid: String, lat: Double, lng: Double, isSharing: Boolean)
}
