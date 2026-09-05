package com.example.lingolens.feature.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lingolens.ui.components.UserAvatar
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
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                UserAvatar(state.avatarId, size = 68.dp)
                Column(Modifier.weight(1f).padding(start = 14.dp)) {
                    Text(state.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(
                        "@${state.email.substringBefore("@").ifBlank { state.name.lowercase().replace(" ", "_") }}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                    OutlinedButton(
                        onClick = { onAction(ProfileAction.EditProfile) },
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.padding(top = 2.dp),
                    ) {
                        Icon(Icons.Outlined.Edit, null, Modifier.size(14.dp))
                        Text("Edit Profile", Modifier.padding(start = 6.dp), style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ProfileStat(Icons.Outlined.Stars, "Lv. ${state.level}", state.title, Modifier.weight(1f).fillMaxHeight())
                ProfileStat(Icons.Outlined.LocalFireDepartment, state.streakDays.toString(), "day streak", Modifier.weight(1f).fillMaxHeight())
                ProfileStat(Icons.Outlined.Bolt, state.xp.toString(), "total XP", Modifier.weight(1f).fillMaxHeight())
            }
        }
        item {
            ProfileGroup {
                ProfileSettingsRow("My Words (${state.words})", Icons.AutoMirrored.Outlined.MenuBook, { onAction(ProfileAction.OpenMyWords) })
                ProfileSettingsRow("Achievements", Icons.Outlined.EmojiEvents, { onAction(ProfileAction.OpenAchievements) })
                ProfileSettingsRow("Statistics", Icons.Outlined.BarChart, { onAction(ProfileAction.OpenStatistics) }, showDivider = false)
            }
        }
        item {
            ProfileGroup {
                ProfileSettingsRow("Notification Settings", Icons.Outlined.Notifications, { onAction(ProfileAction.OpenNotifications) })
                ProfileSettingsRow("Translator", Icons.Outlined.Translate, { onAction(ProfileAction.OpenTranslator) })
                ProfileSettingsRow("Location & Privacy", Icons.Outlined.LocationOn, { onAction(ProfileAction.OpenPrivacy) })
                ProfileSettingsRow("Logout", Icons.AutoMirrored.Outlined.Logout, { onAction(ProfileAction.Logout) }, showDivider = false)
            }
        }
    }
}

@Composable
private fun ProfileStat(icon: ImageVector, value: String, label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Column(
            Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp),
        ) {
            Icon(icon, null, Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

@Composable
private fun ProfileGroup(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Column(Modifier.padding(vertical = 2.dp), content = content)
    }
}

@Composable
private fun ProfileSettingsRow(title: String, icon: ImageVector, onClick: () -> Unit, showDivider: Boolean = true) {
    Column {
        Row(
            Modifier.fillMaxWidth().clickable(onClick = onClick).heightIn(min = 56.dp)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Surface(
                shape = CircleShape,
                color = lerp(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.primaryContainer, 0.65f),
            ) {
                Icon(icon, null, Modifier.padding(8.dp).size(18.dp), tint = MaterialTheme.colorScheme.primary)
            }
            Text(title, Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, null, Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (showDivider) {
            HorizontalDivider(
                Modifier.padding(start = 56.dp, end = 12.dp),
                color = lerp(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.outlineVariant, 0.6f),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfilePreview() { LingoLensTheme(darkTheme = false) { ProfileScreen(ProfileUiState(isLoading = false), {}) } }
