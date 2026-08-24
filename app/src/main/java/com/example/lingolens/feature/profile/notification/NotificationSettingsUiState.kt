package com.example.lingolens.feature.profile.notification

enum class NotificationSetting { DailyReminder, DailyGoalReminder, ReviewReminder, AchievementUnlocked, StreakAlert }

data class NotificationSettingsUiState(
    val dailyReminder: Boolean = true,
    val dailyGoalReminder: Boolean = true,
    val reviewReminder: Boolean = true,
    val achievementUnlocked: Boolean = true,
    val streakAlert: Boolean = true,
    val reminderTime: String = "8:00 PM",
)
