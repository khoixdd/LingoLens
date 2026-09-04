package com.example.lingolens.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.lingolens.domain.gamification.StreakCalculator
import com.example.lingolens.domain.model.DEFAULT_DAILY_GOAL
import com.example.lingolens.domain.repository.AuthRepository
import com.example.lingolens.domain.repository.DailyActivityRepository
import com.example.lingolens.domain.repository.NotificationSettingsRepository
import com.example.lingolens.domain.repository.UserRepository
import com.example.lingolens.domain.repository.VocabularyRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.time.LocalDate
import kotlinx.coroutines.flow.first

class DailyReminderWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            ReminderWorkerEntryPoint::class.java,
        )
        val settings = entryPoint.notificationSettingsRepository().getSettings()
        if (!settings.hasScheduledReminder || !entryPoint.notificationHelper().canPostNotifications()) {
            return Result.success()
        }
        val today = LocalDate.now().toEpochDay()
        val completed = entryPoint.dailyActivityRepository().uniqueWords(today)
        val words = entryPoint.vocabularyRepository().getAllVocabulary().first()
        val due = words.count { it.nextReviewAt == null || it.nextReviewAt <= System.currentTimeMillis() }
        val uid = entryPoint.authRepository().getCurrentUser()?.uid.orEmpty()
        val profile = if (uid.isBlank()) null else entryPoint.userRepository().getUserProfile(uid)
        val effectiveStreak = profile?.let {
            StreakCalculator.effectiveStreak(it.streakDays, it.lastActivityEpochDay, today)
        } ?: 0
        val message = when {
            settings.dailyGoalReminderEnabled && completed < DEFAULT_DAILY_GOAL ->
                "You still have ${DEFAULT_DAILY_GOAL - completed} words left to reach today's goal."
            settings.reviewReminderEnabled && due > 0 -> "$due words are ready for review."
            settings.streakAlertsEnabled && effectiveStreak > 0 && completed == 0 ->
                "Keep your $effectiveStreak-day streak alive — review a few words today."
            settings.dailyReminderEnabled -> "A short vocabulary session keeps your progress moving."
            else -> null
        }
        message?.let(entryPoint.notificationHelper()::showReminder)
        return Result.success()
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface ReminderWorkerEntryPoint {
    fun notificationSettingsRepository(): NotificationSettingsRepository
    fun dailyActivityRepository(): DailyActivityRepository
    fun vocabularyRepository(): VocabularyRepository
    fun authRepository(): AuthRepository
    fun userRepository(): UserRepository
    fun notificationHelper(): NotificationHelper
}
