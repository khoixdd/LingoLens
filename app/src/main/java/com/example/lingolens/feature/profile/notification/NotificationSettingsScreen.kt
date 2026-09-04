package com.example.lingolens.feature.profile.notification

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lingolens.ui.components.LingoLensCard
import com.example.lingolens.ui.components.SettingToggleRow
import com.example.lingolens.ui.theme.LingoLensTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    state: NotificationSettingsUiState,
    onAction: (NotificationSettingsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
                navigationIcon = {
                    IconButton({ onAction(NotificationSettingsAction.Back) }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back")
                    }
                },
            )
        },
    ) { insets ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(insets), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }
        LazyColumn(
            Modifier.fillMaxSize().padding(insets),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            if (state.permissionDenied) {
                item {
                    LingoLensCard(containerColor = MaterialTheme.colorScheme.errorContainer) {
                        Text(
                            "Notification permission was denied. Reminders remain off until permission is granted.",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
            item {
                LingoLensCard(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)) {
                    SettingToggleRow(
                        title = "Daily reminder",
                        checked = state.dailyReminder,
                        description = "Get reminded to learn",
                    ) { onAction(NotificationSettingsAction.Toggle(NotificationSetting.DailyReminder, it)) }
                    SettingToggleRow(
                        title = "Daily goal reminder",
                        checked = state.dailyGoalReminder,
                        description = "You're on track with your goal",
                    ) { onAction(NotificationSettingsAction.Toggle(NotificationSetting.DailyGoalReminder, it)) }
                    SettingToggleRow(
                        title = "Review reminder",
                        checked = state.reviewReminder,
                        description = "Review words when they are due",
                    ) { onAction(NotificationSettingsAction.Toggle(NotificationSetting.ReviewReminder, it)) }
                    SettingToggleRow(
                        title = "Achievement unlocked",
                        checked = state.achievementUnlocked,
                        description = "Celebrate newly earned rewards",
                    ) { onAction(NotificationSettingsAction.Toggle(NotificationSetting.AchievementUnlocked, it)) }
                    SettingToggleRow(
                        title = "Streak alert",
                        checked = state.streakAlert,
                        description = "Don't break your streak",
                        showDivider = false,
                    ) { onAction(NotificationSettingsAction.Toggle(NotificationSetting.StreakAlert, it)) }
                }
            }
            item {
                LingoLensCard(contentPadding = PaddingValues(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.AccessTime, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                            Text("Reminder time", style = MaterialTheme.typography.titleMedium)
                            Text(
                                state.reminderTime,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                        TextButton(onClick = { onAction(NotificationSettingsAction.ChangeReminderTime) }) {
                            Text("Change")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationsPreview() { LingoLensTheme(darkTheme = false) { NotificationSettingsScreen(NotificationSettingsUiState(), {}) } }
