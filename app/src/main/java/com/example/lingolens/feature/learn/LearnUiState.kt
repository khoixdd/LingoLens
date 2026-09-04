package com.example.lingolens.feature.learn

data class LearnUiState(
    val isLoading: Boolean = true,
    val reviewCount: Int = 0,
    val notebookCount: Int = 0,
    val newCount: Int = 0,
    val learningCount: Int = 0,
    val familiarCount: Int = 0,
    val masteredCount: Int = 0,
    val dailyGoalCompleted: Int = 0,
    val dailyGoalTarget: Int = com.example.lingolens.domain.model.DEFAULT_DAILY_GOAL,
)
