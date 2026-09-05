package com.example.lingolens.domain.repository

interface LearningProgressRepository {
    val achievementUnlocks: kotlinx.coroutines.flow.Flow<com.example.lingolens.domain.model.AchievementUnlock>
    suspend fun recordActivity(vocabularyId: String, xpReward: Int = 0)
    suspend fun awardQuizXp(xpReward: Int)
    suspend fun evaluateCurrentAchievements()
}

