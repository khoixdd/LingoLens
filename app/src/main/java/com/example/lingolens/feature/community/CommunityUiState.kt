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
    LeaderboardEntry(1, "User A", 11, 2100, 15),
    LeaderboardEntry(2, "User B", 10, 1850, 12),
    LeaderboardEntry(3, "User C", 9, 1700, 9),
    LeaderboardEntry(4, "Learner", 1, 100, 1, isCurrentUser = true),
)

val sampleAllTimeLeaderboard = sampleWeeklyLeaderboard.map { entry ->
    entry.copy(xp = entry.xp * 8)
}
