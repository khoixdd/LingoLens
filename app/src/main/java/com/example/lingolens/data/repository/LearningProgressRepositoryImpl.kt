package com.example.lingolens.data.repository

import com.example.lingolens.domain.gamification.AchievementDefinitions
import com.example.lingolens.domain.model.DEFAULT_DAILY_GOAL
import com.example.lingolens.domain.repository.AuthRepository
import com.example.lingolens.domain.repository.DailyActivityRepository
import com.example.lingolens.domain.repository.LearningProgressRepository
import com.example.lingolens.domain.repository.NotificationSettingsRepository
import com.example.lingolens.domain.repository.UserRepository
import com.example.lingolens.domain.repository.VocabularyRepository
import com.example.lingolens.notification.NotificationHelper
import com.example.lingolens.domain.model.AchievementUnlock
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

@Singleton
class LearningProgressRepositoryImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val dailyActivityRepository: DailyActivityRepository,
    private val vocabularyRepository: VocabularyRepository,
    private val userRepository: UserRepository,
    private val notificationSettingsRepository: NotificationSettingsRepository,
    private val notificationHelper: NotificationHelper,
) : LearningProgressRepository {
    private val unlockEvents = MutableSharedFlow<AchievementUnlock>(extraBufferCapacity = 16)
    override val achievementUnlocks = unlockEvents.asSharedFlow()

    override suspend fun recordActivity(vocabularyId: String, xpReward: Int) {
        val uid = authRepository.getCurrentUser()?.uid ?: return
        val today = LocalDate.now().toEpochDay()
        dailyActivityRepository.recordUniqueWord(today, vocabularyId)
        updateRemoteProgress(uid, today, xpReward)
    }

    override suspend fun awardQuizXp(xpReward: Int) {
        val uid = authRepository.getCurrentUser()?.uid ?: return
        updateRemoteProgress(uid, null, xpReward)
    }

    override suspend fun evaluateCurrentAchievements() {
        val uid = authRepository.getCurrentUser()?.uid ?: return
        updateRemoteProgress(uid, null, 0)
    }

    private suspend fun updateRemoteProgress(uid: String, activityEpochDay: Long?, xpDelta: Int) {
        val today = LocalDate.now().toEpochDay()
        val dailyWords = dailyActivityRepository.uniqueWords(today)
        val totalWords = vocabularyRepository.getAllVocabulary().first().size
        val result = userRepository.updateGamification(
            uid = uid,
            activityEpochDay = activityEpochDay,
            xpDelta = xpDelta,
            dailyWords = dailyWords,
            totalWords = totalWords,
        ) ?: return
        if (result.newlyUnlockedAchievementIds.isEmpty()) return
        result.newlyUnlockedAchievementIds.forEach { id -> unlockEvents.tryEmit(AchievementUnlock(uid, id)) }
        val settings = notificationSettingsRepository.getSettings()
        if (!settings.achievementNotificationsEnabled) return
        val definitionsById = AchievementDefinitions.all.associateBy { it.id }
        result.newlyUnlockedAchievementIds.forEach { id ->
            definitionsById[id]?.let { notificationHelper.showAchievement(it.name) }
        }
    }
}

