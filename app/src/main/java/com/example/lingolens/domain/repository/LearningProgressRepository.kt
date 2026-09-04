package com.example.lingolens.domain.repository

interface LearningProgressRepository {
    suspend fun recordActivity(vocabularyId: String, xpReward: Int = 0)
    suspend fun awardQuizXp(xpReward: Int)
    suspend fun evaluateCurrentAchievements()
}

