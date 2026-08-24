package com.example.lingolens.feature.profile.notification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lingolens.ui.components.LingoLensCard
import com.example.lingolens.ui.components.SettingToggleRow
import com.example.lingolens.ui.theme.LingoLensTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(state: NotificationSettingsUiState, onAction: (NotificationSettingsAction) -> Unit, modifier: Modifier = Modifier) {
    Scaffold(modifier, topBar = { TopAppBar(title = { Text("Notifications") }, navigationIcon = { IconButton({ onAction(NotificationSettingsAction.Back) }) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back") } }) }) { insets ->
        LazyColumn(Modifier.fillMaxSize().padding(insets), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item { LingoLensCard { Text("Reminder time", color = MaterialTheme.colorScheme.onSurfaceVariant); Text(state.reminderTime, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) } }
            item { LingoLensCard { SettingToggleRow("Daily Reminder", checked = state.dailyReminder) { onAction(NotificationSettingsAction.Toggle(NotificationSetting.DailyReminder, it)) }; SettingToggleRow("Daily Goal Reminder", checked = state.dailyGoalReminder) { onAction(NotificationSettingsAction.Toggle(NotificationSetting.DailyGoalReminder, it)) }; SettingToggleRow("Review Reminder", checked = state.reviewReminder) { onAction(NotificationSettingsAction.Toggle(NotificationSetting.ReviewReminder, it)) }; SettingToggleRow("Achievement Unlocked", checked = state.achievementUnlocked) { onAction(NotificationSettingsAction.Toggle(NotificationSetting.AchievementUnlocked, it)) }; SettingToggleRow("Streak Alert", checked = state.streakAlert) { onAction(NotificationSettingsAction.Toggle(NotificationSetting.StreakAlert, it)) } } }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationsPreview() { LingoLensTheme(darkTheme = false) { NotificationSettingsScreen(NotificationSettingsUiState(), {}) } }
