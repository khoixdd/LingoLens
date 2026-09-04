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
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material3.Icon
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lingolens.ui.components.LingoLensCard
import com.example.lingolens.ui.components.ProfileMenuItem
import com.example.lingolens.ui.theme.LingoLensTheme

@Composable
fun ProfileScreen(
    state: ProfileUiState,
    onAction: (ProfileAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isLoading) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item { Text("Profile", style = MaterialTheme.typography.headlineMedium) }
        item {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(82.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        state.name.take(1).uppercase(),
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Text(
                    state.name,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(top = 10.dp),
                )
                Text(
                    if (state.email.isNotBlank()) state.email else "@${state.name.lowercase()}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ProfileStat(Icons.Outlined.Stars, "Lv. ${state.level}", state.title, Modifier.weight(1f))
                ProfileStat(Icons.Outlined.EmojiEvents, state.streakDays.toString(), "day streak", Modifier.weight(1f))
                ProfileStat(null, state.xp.toString(), "total XP", Modifier.weight(1f), symbol = "XP")
            }
        }
        item {
            LingoLensCard(contentPadding = PaddingValues(vertical = 4.dp)) {
                ProfileMenuItem("My Words (${state.words})", Icons.AutoMirrored.Outlined.MenuBook, { onAction(ProfileAction.OpenMyWords) })
                ProfileMenuItem("Achievements", Icons.Outlined.EmojiEvents, { onAction(ProfileAction.OpenAchievements) })
                ProfileMenuItem("Statistics", Icons.Outlined.BarChart, { onAction(ProfileAction.OpenStatistics) }, showDivider = false)
            }
        }
        item {
            LingoLensCard(contentPadding = PaddingValues(vertical = 4.dp)) {
                ProfileMenuItem("Notification Settings", Icons.Outlined.Notifications, { onAction(ProfileAction.OpenNotifications) })
                ProfileMenuItem("Location & Privacy", Icons.Outlined.LocationOn, { onAction(ProfileAction.OpenPrivacy) })
                ProfileMenuItem("Logout", Icons.AutoMirrored.Outlined.Logout, { onAction(ProfileAction.Logout) }, showDivider = false)
            }
        }
    }
}

@Composable
private fun ProfileStat(
    icon: ImageVector?,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    symbol: String? = null,
) {
    LingoLensCard(modifier, PaddingValues(horizontal = 8.dp, vertical = 12.dp)) {
        Column(
            Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
            } else {
                Text(symbol.orEmpty(), color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
            }
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfilePreview() { LingoLensTheme(darkTheme = false) { ProfileScreen(ProfileUiState(), {}) } }
