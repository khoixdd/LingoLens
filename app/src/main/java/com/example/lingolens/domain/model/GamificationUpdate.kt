package com.example.lingolens.domain.model

data class GamificationUpdate(
    val xp: Int,
    val level: Int,
    val streakDays: Int,
    val lastActivityEpochDay: Long?,
    val unlockedAchievementIds: Set<String>,
    val newlyUnlockedAchievementIds: Set<String>,
)

