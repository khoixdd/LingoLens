package com.example.lingolens.feature.profile.notification

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class NotificationSettingsViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(NotificationSettingsUiState())
    val uiState: StateFlow<NotificationSettingsUiState> = _uiState.asStateFlow()
    fun onAction(action: NotificationSettingsAction) {
        if (action is NotificationSettingsAction.Toggle) _uiState.update { state -> when (action.setting) { NotificationSetting.DailyReminder -> state.copy(dailyReminder = action.enabled); NotificationSetting.DailyGoalReminder -> state.copy(dailyGoalReminder = action.enabled); NotificationSetting.ReviewReminder -> state.copy(reviewReminder = action.enabled); NotificationSetting.AchievementUnlocked -> state.copy(achievementUnlocked = action.enabled); NotificationSetting.StreakAlert -> state.copy(streakAlert = action.enabled) } }
    }
}
