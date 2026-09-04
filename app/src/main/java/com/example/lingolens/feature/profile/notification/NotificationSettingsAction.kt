package com.example.lingolens.feature.profile.notification

sealed interface NotificationSettingsAction {
    data object Back : NotificationSettingsAction
    data class Toggle(val setting: NotificationSetting, val enabled: Boolean) : NotificationSettingsAction
    data object ChangeReminderTime : NotificationSettingsAction
    data class SetReminderTime(val hour: Int, val minute: Int) : NotificationSettingsAction
    data class PermissionStatusChanged(val granted: Boolean, val denied: Boolean = false) : NotificationSettingsAction
}
