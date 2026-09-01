package com.example.lingolens.feature.community

enum class LeaderboardPeriod(val label: String) {
    ThisWeek("This Week"),
    AllTime("All Time"),
}

data class LeaderboardEntry(
    val rank: Int,
    val name: String,
    val level: Int,
    val xp: Int,
    val streakDays: Int,
    val isCurrentUser: Boolean = false,
)

data class CommunityUiState(
    val selectedPeriod: LeaderboardPeriod = LeaderboardPeriod.ThisWeek,
    val leaderboard: List<LeaderboardEntry> = sampleWeeklyLeaderboard,
)

val sampleWeeklyLeaderboard = listOf(
    LeaderboardEntry(1, "Minh", 12, 2480, 21),
    LeaderboardEntry(2, "An", 10, 2210, 18),
    LeaderboardEntry(3, "Khoi", 9, 1980, 16),
    LeaderboardEntry(4, "Alex", 7, 1560, 12, isCurrentUser = true),
    LeaderboardEntry(5, "Lan", 6, 1340, 9),
)

val sampleAllTimeLeaderboard = sampleWeeklyLeaderboard.map { entry ->
    entry.copy(xp = entry.xp * 8)
}
