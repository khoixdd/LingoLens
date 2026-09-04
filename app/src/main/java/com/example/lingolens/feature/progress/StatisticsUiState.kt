package com.example.lingolens.feature.progress

import com.example.lingolens.domain.gamification.LevelCalculator
import com.example.lingolens.domain.model.DEFAULT_DAILY_GOAL
import com.example.lingolens.domain.model.WeeklyActivityDay

data class StatisticsUiState(
    val isLoading: Boolean = true,
    val todayWords: Int = 0,
    val dailyGoal: Int = DEFAULT_DAILY_GOAL,
    val weeklyActivity: List<WeeklyActivityDay> = emptyList(),
    val totalWords: Int = 0,
    val newWords: Int = 0,
    val learningWords: Int = 0,
    val familiarWords: Int = 0,
    val masteredWords: Int = 0,
    val xp: Int = 0,
    val level: Int = 1,
    val xpProgress: Int = 0,
    val xpPerLevel: Int = LevelCalculator.XP_PER_LEVEL,
    val streakDays: Int = 0,
)

