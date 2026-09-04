package com.example.lingolens.feature.home

import com.example.lingolens.domain.gamification.LevelCalculator
import com.example.lingolens.domain.model.DEFAULT_DAILY_GOAL
import com.example.lingolens.domain.model.WeeklyActivityDay

data class HomeUiState(
    val isLoading: Boolean = true,
    val name: String = "",
    val streakDays: Int = 0,
    val level: Int = 1,
    val title: String = LevelCalculator.titleForLevel(1),
    val xp: Int = 0,
    val xpProgressInLevel: Int = 0,
    val xpPerLevel: Int = LevelCalculator.XP_PER_LEVEL,
    val totalWords: Int = 0,
    val dailyWordsCompleted: Int = 0,
    val dailyWordsGoal: Int = DEFAULT_DAILY_GOAL,
    val reviewWordsDue: Int = 0,
    val weeklyActivity: List<WeeklyActivityDay> = emptyList(),
)
