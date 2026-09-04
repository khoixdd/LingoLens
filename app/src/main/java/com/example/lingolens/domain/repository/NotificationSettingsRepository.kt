package com.example.lingolens.domain.repository

import com.example.lingolens.domain.model.NotificationSettings
import kotlinx.coroutines.flow.Flow

interface NotificationSettingsRepository {
    fun observeSettings(): Flow<NotificationSettings>
    suspend fun getSettings(): NotificationSettings
    suspend fun saveSettings(settings: NotificationSettings)
}

