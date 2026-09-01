package com.example.lingolens.feature.home

data class DailyActivity(
    val day: String,
    val words: Int,
)

data class HomeUiState(
    val name: String = "Alex",
    val streakDays: Int = 12,
    val level: Int = 7,
    val title: String = "Explorer",
    val xp: Int = 1560,
    val dailyWordsCompleted: Int = 7,
    val dailyWordsGoal: Int = 10,
    val reviewWordsDue: Int = 14,
    val weeklyActivity: List<DailyActivity> = listOf(
        DailyActivity("Mon", 5),
        DailyActivity("Tue", 8),
        DailyActivity("Wed", 6),
        DailyActivity("Thu", 10),
        DailyActivity("Fri", 7),
        DailyActivity("Sat", 4),
        DailyActivity("Sun", 7),
    ),
)
