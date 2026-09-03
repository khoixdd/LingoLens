package com.example.lingolens.domain.model

data class UserProfile(
    val uid: String = "",
    val username: String = "Learner",
    val email: String = "",
    val avatarUrl: String = "",
    val xp: Int = 0,
    val level: Int = 1,
    val streakDays: Int = 1,
    val totalWords: Int = 0,
    val latitude: Double = 10.762622,
    val longitude: Double = 106.682221,
    val isSharingLocation: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis(),
)
