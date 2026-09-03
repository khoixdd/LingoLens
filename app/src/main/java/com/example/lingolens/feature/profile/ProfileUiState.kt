package com.example.lingolens.feature.profile

data class ProfileUiState(
    val name: String = "Learner",
    val email: String = "",
    val level: Int = 1,
    val streakDays: Int = 1,
    val xp: Int = 100,
    val words: Int = 0,
    val isLoggedOut: Boolean = false,
)
