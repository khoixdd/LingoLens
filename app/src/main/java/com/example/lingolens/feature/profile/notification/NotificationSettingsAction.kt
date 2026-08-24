package com.example.lingolens.feature.profile.notification

sealed interface NotificationSettingsAction { data object Back : NotificationSettingsAction; data class Toggle(val setting: NotificationSetting, val enabled: Boolean) : NotificationSettingsAction; data object ChangeReminderTime : NotificationSettingsAction }
