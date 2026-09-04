package com.example.lingolens.feature.profile.notification

enum class NotificationSetting { DailyReminder, DailyGoalReminder, ReviewReminder, AchievementUnlocked, StreakAlert }

data class NotificationSettingsUiState(
    val isLoading: Boolean = true,
    val dailyReminder: Boolean = false,
    val dailyGoalReminder: Boolean = false,
    val reviewReminder: Boolean = false,
    val achievementUnlocked: Boolean = false,
    val streakAlert: Boolean = false,
    val reminderHour: Int = 20,
    val reminderMinute: Int = 0,
    val permissionGranted: Boolean = false,
    val permissionDenied: Boolean = false,
    val reminderTime: String = "8:00 PM",
)
