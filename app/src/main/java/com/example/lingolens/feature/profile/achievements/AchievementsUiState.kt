package com.example.lingolens.feature.profile.achievements

import com.example.lingolens.domain.gamification.AchievementDefinition

data class AchievementItemUi(
    val definition: AchievementDefinition,
    val isUnlocked: Boolean,
)

data class AchievementsUiState(
    val isLoading: Boolean = true,
    val achievements: List<AchievementItemUi> = emptyList(),
)

