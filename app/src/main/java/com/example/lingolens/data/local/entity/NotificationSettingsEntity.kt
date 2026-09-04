package com.example.lingolens.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification_settings")
data class NotificationSettingsEntity(
    @PrimaryKey val userId: String,
    val dailyReminderEnabled: Boolean,
    val dailyGoalReminderEnabled: Boolean,
    val reviewReminderEnabled: Boolean,
    val achievementNotificationsEnabled: Boolean,
    val streakAlertsEnabled: Boolean,
    val reminderHour: Int,
    val reminderMinute: Int,
)

