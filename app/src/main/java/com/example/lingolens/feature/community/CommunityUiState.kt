package com.example.lingolens.feature.community

data class LeaderboardEntry(
    val rank: Int,
    val name: String,
    val level: Int,
    val xp: Int,
    val streakDays: Int,
    val isCurrentUser: Boolean = false,
)

data class CommunityUiState(
    val leaderboard: List<LeaderboardEntry> = emptyList(),
    val isLeaderboardLoading: Boolean = true,
    val hasLeaderboardData: Boolean = false,
    val isLeaderboardFromCache: Boolean = false,
    val leaderboardError: String? = null,
)
