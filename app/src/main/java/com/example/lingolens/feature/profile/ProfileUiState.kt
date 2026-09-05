package com.example.lingolens.feature.profile

data class ProfileUiState(
    val isLoading: Boolean = true,
    val name: String = "Learner",
    val avatarId: String = "leaf",
    val email: String = "",
    val level: Int = 1,
    val title: String = "Explorer",
    val streakDays: Int = 0,
    val xp: Int = 0,
    val words: Int = 0,
    val isLoggedOut: Boolean = false,
)
