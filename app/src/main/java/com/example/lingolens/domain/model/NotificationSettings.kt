package com.example.lingolens.domain.model

data class NotificationSettings(
    val dailyReminderEnabled: Boolean = false,
    val dailyGoalReminderEnabled: Boolean = false,
    val reviewReminderEnabled: Boolean = false,
    val achievementNotificationsEnabled: Boolean = false,
    val streakAlertsEnabled: Boolean = false,
    val reminderHour: Int = 20,
    val reminderMinute: Int = 0,
) {
    val hasScheduledReminder: Boolean
        get() = dailyReminderEnabled || dailyGoalReminderEnabled ||
            reviewReminderEnabled || streakAlertsEnabled
}

