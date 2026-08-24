package com.example.lingolens.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lingolens.ui.components.LingoLensCard
import com.example.lingolens.ui.components.ProfileMenuItem
import com.example.lingolens.ui.theme.LingoLensTheme

@Composable
fun ProfileScreen(state: ProfileUiState, onAction: (ProfileAction) -> Unit, modifier: Modifier = Modifier) {
    LazyColumn(modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { Text("Profile", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }
        item {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.size(88.dp).clip(androidx.compose.foundation.shape.CircleShape).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) { Text(state.name.take(1), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) }
                Text(state.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
                Text("Level ${state.level}", color = MaterialTheme.colorScheme.primary)
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ProfileStat("🔥", "${state.streakDays}", "day streak", Modifier.weight(1f)); ProfileStat("XP", "${state.xp}", "experience", Modifier.weight(1f)); ProfileStat("Aa", "${state.words}", "words", Modifier.weight(1f))
            }
        }
        item {
            LingoLensCard(contentPadding = PaddingValues(vertical = 4.dp)) {
                ProfileMenuItem("My Words", Icons.AutoMirrored.Outlined.MenuBook, { onAction(ProfileAction.OpenMyWords) }); ProfileMenuItem("Achievements", Icons.Outlined.EmojiEvents, { onAction(ProfileAction.OpenAchievements) }); ProfileMenuItem("Statistics", Icons.Outlined.BarChart, { onAction(ProfileAction.OpenStatistics) })
            }
        }
        item {
            LingoLensCard(contentPadding = PaddingValues(vertical = 4.dp)) {
                ProfileMenuItem("Notification Settings", Icons.Outlined.Notifications, { onAction(ProfileAction.OpenNotifications) }); ProfileMenuItem("Location & Privacy", Icons.Outlined.LocationOn, { onAction(ProfileAction.OpenPrivacy) }); ProfileMenuItem("Logout", Icons.AutoMirrored.Outlined.Logout, { onAction(ProfileAction.Logout) })
            }
        }
    }
}

@Composable
private fun ProfileStat(symbol: String, value: String, label: String, modifier: Modifier = Modifier) { LingoLensCard(modifier, PaddingValues(12.dp)) { Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) { Text(symbol, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold); Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) } } }

@Preview(showBackground = true)
@Composable
private fun ProfilePreview() { LingoLensTheme(darkTheme = false) { ProfileScreen(ProfileUiState(), {}) } }
