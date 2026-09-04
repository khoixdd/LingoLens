package com.example.lingolens.feature.profile.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lingolens.domain.model.NotificationSettings
import com.example.lingolens.domain.repository.NotificationSettingsRepository
import com.example.lingolens.notification.NotificationScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor(
    private val repository: NotificationSettingsRepository,
    private val scheduler: NotificationScheduler,
) : ViewModel() {
    private val permissionState = MutableStateFlow(false to false)

    val uiState: StateFlow<NotificationSettingsUiState> = combine(
        repository.observeSettings(),
        permissionState,
    ) { settings, permission -> settings.toUiState(permission.first, permission.second) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NotificationSettingsUiState())

    fun onAction(action: NotificationSettingsAction) {
        when (action) {
            is NotificationSettingsAction.Toggle -> update { current ->
                when (action.setting) {
                    NotificationSetting.DailyReminder -> current.copy(dailyReminderEnabled = action.enabled)
                    NotificationSetting.DailyGoalReminder -> current.copy(dailyGoalReminderEnabled = action.enabled)
                    NotificationSetting.ReviewReminder -> current.copy(reviewReminderEnabled = action.enabled)
                    NotificationSetting.AchievementUnlocked -> current.copy(achievementNotificationsEnabled = action.enabled)
                    NotificationSetting.StreakAlert -> current.copy(streakAlertsEnabled = action.enabled)
                }
            }
            is NotificationSettingsAction.SetReminderTime -> update {
                it.copy(reminderHour = action.hour.coerceIn(0, 23), reminderMinute = action.minute.coerceIn(0, 59))
            }
            is NotificationSettingsAction.PermissionStatusChanged -> {
                permissionState.value = action.granted to action.denied
            }
            NotificationSettingsAction.Back,
            NotificationSettingsAction.ChangeReminderTime,
            -> Unit
        }
    }

    private fun update(transform: (NotificationSettings) -> NotificationSettings) {
        viewModelScope.launch {
            val updated = transform(repository.getSettings())
            repository.saveSettings(updated)
            scheduler.sync(updated)
        }
    }
}

private fun NotificationSettings.toUiState(permissionGranted: Boolean, permissionDenied: Boolean) =
    NotificationSettingsUiState(
        isLoading = false,
        dailyReminder = dailyReminderEnabled && permissionGranted,
        dailyGoalReminder = dailyGoalReminderEnabled && permissionGranted,
        reviewReminder = reviewReminderEnabled && permissionGranted,
        achievementUnlocked = achievementNotificationsEnabled && permissionGranted,
        streakAlert = streakAlertsEnabled && permissionGranted,
        reminderHour = reminderHour,
        reminderMinute = reminderMinute,
        permissionGranted = permissionGranted,
        permissionDenied = permissionDenied,
        reminderTime = LocalTime.of(reminderHour, reminderMinute)
            .format(DateTimeFormatter.ofPattern("h:mm a")),
    )
