package com.example.lingolens.data.repository

import com.example.lingolens.data.local.dao.NotificationSettingsDao
import com.example.lingolens.data.local.entity.NotificationSettingsEntity
import com.example.lingolens.domain.model.NotificationSettings
import com.example.lingolens.domain.repository.AuthRepository
import com.example.lingolens.domain.repository.NotificationSettingsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@Singleton
class NotificationSettingsRepositoryImpl @Inject constructor(
    private val dao: NotificationSettingsDao,
    private val authRepository: AuthRepository,
) : NotificationSettingsRepository {
    override fun observeSettings(): Flow<NotificationSettings> {
        val userId = authRepository.getCurrentUser()?.uid ?: return flowOf(NotificationSettings())
        return dao.observe(userId).map { it?.toDomain() ?: NotificationSettings() }
    }

    override suspend fun getSettings(): NotificationSettings {
        val userId = authRepository.getCurrentUser()?.uid ?: return NotificationSettings()
        return dao.get(userId)?.toDomain() ?: NotificationSettings()
    }

    override suspend fun saveSettings(settings: NotificationSettings) {
        val userId = authRepository.getCurrentUser()?.uid ?: return
        dao.upsert(settings.toEntity(userId))
    }
}

private fun NotificationSettingsEntity.toDomain() = NotificationSettings(
    dailyReminderEnabled = dailyReminderEnabled,
    dailyGoalReminderEnabled = dailyGoalReminderEnabled,
    reviewReminderEnabled = reviewReminderEnabled,
    achievementNotificationsEnabled = achievementNotificationsEnabled,
    streakAlertsEnabled = streakAlertsEnabled,
    reminderHour = reminderHour,
    reminderMinute = reminderMinute,
)

private fun NotificationSettings.toEntity(userId: String) = NotificationSettingsEntity(
    userId = userId,
    dailyReminderEnabled = dailyReminderEnabled,
    dailyGoalReminderEnabled = dailyGoalReminderEnabled,
    reviewReminderEnabled = reviewReminderEnabled,
    achievementNotificationsEnabled = achievementNotificationsEnabled,
    streakAlertsEnabled = streakAlertsEnabled,
    reminderHour = reminderHour,
    reminderMinute = reminderMinute,
)

