package com.example.lingolens.feature.community

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lingolens.ui.components.LingoLensCard
import com.example.lingolens.ui.components.SectionHeader
import com.example.lingolens.ui.theme.LingoLensTheme

@Composable
fun CommunityScreen(
    state: CommunityUiState,
    onAction: (CommunityAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Text("Community", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Learn together and celebrate progress.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                LeaderboardPeriod.entries.forEach { period ->
                    FilterChip(
                        selected = state.selectedPeriod == period,
                        onClick = { onAction(CommunityAction.SelectPeriod(period)) },
                        label = { Text(period.label) },
                    )
                }
            }
        }
        item {
            SectionHeader(
                title = "Leaderboard",
                action = {
                    Icon(
                        Icons.Outlined.EmojiEvents,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                    )
                },
            )
        }
        item {
            LingoLensCard(contentPadding = PaddingValues(vertical = 4.dp)) {
                state.leaderboard.forEachIndexed { index, entry ->
                    LeaderboardRow(entry)
                    if (index != state.leaderboard.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant,
                        )
                    }
                }
            }
        }
        item { SectionHeader("Nearby learners") }
        item {
            LingoLensCard(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surface) {
                        Icon(
                            Icons.Outlined.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(10.dp),
                        )
                    }
                    Column(Modifier.padding(start = 12.dp)) {
                        Text("Find your learning circle", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Nearby discovery will appear here when location sharing is available.",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardRow(entry: LeaderboardEntry) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (entry.isCurrentUser) {
                    Modifier.clip(MaterialTheme.shapes.medium).background(MaterialTheme.colorScheme.primaryContainer)
                } else {
                    Modifier
                },
            )
            .padding(horizontal = 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            entry.rank.toString(),
            modifier = Modifier.size(28.dp).padding(top = 4.dp),
            style = MaterialTheme.typography.labelLarge,
            color = if (entry.rank <= 3) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
            Box(modifier = Modifier.size(38.dp), contentAlignment = Alignment.Center) {
                Text(entry.name.take(1), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
        Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
            Text(
                if (entry.isCurrentUser) "${entry.name} (You)" else entry.name,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                "Lv. ${entry.level}  |  ${entry.streakDays} day streak",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text("${entry.xp} XP", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
    }
}

@Preview(showBackground = true)
@Composable
private fun CommunityScreenPreview() {
    LingoLensTheme(darkTheme = false) {
        CommunityScreen(state = CommunityUiState(), onAction = {})
    }
}
