package com.example.lingolens.domain.model

sealed interface LeaderboardLoadState {
    data object Loading : LeaderboardLoadState
    data class Data(
        val users: List<UserProfile>,
        val isFromCache: Boolean = false,
    ) : LeaderboardLoadState
    data class Error(val message: String) : LeaderboardLoadState
}
