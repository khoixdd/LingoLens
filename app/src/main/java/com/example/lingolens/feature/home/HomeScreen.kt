package com.example.lingolens.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lingolens.ui.components.LingoLensCard
import com.example.lingolens.ui.components.LingoLensPrimaryButton
import com.example.lingolens.ui.components.SectionHeader
import com.example.lingolens.ui.theme.LingoLensTheme
import com.example.lingolens.domain.model.WeeklyActivityDay

@Composable
fun HomeScreen(
    state: HomeUiState,
    onAction: (HomeAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(text = "Hello, ${state.name}!", style = MaterialTheme.typography.headlineSmall)
                    Text(
                        text = "Ready for a little progress?",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Surface(
                    onClick = { onAction(HomeAction.OpenNotifications) },
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Outlined.NotificationsNone,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HomeStat(
                    icon = Icons.Outlined.LocalFireDepartment,
                    value = state.streakDays.toString(),
                    label = "day streak",
                    modifier = Modifier.weight(1f),
                )
                HomeStat(
                    icon = Icons.Outlined.Stars,
                    value = "Lv. ${state.level}",
                    label = state.title,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        item {
            LingoLensCard(contentPadding = PaddingValues(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${state.xp} XP", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                    Text(
                        "${state.xpPerLevel - state.xpProgressInLevel} to next level",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { state.xpProgressInLevel.toFloat() / state.xpPerLevel.coerceAtLeast(1) },
                    modifier = Modifier.fillMaxWidth().height(7.dp).clip(CircleShape),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }
        }
        item {
            LingoLensCard(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Daily goal", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "${state.dailyWordsCompleted} / ${state.dailyWordsGoal} words",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                    Text(
                        "${(state.dailyWordsGoal - state.dailyWordsCompleted).coerceAtLeast(0)} left",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
                Spacer(Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = {
                        (state.dailyWordsCompleted.toFloat() / state.dailyWordsGoal.coerceAtLeast(1))
                            .coerceIn(0f, 1f)
                    },
                    modifier = Modifier.fillMaxWidth().height(7.dp).clip(CircleShape),
                    trackColor = MaterialTheme.colorScheme.surface,
                )
            }
        }
        item {
            LingoLensCard(contentPadding = PaddingValues(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                        Icon(
                            Icons.Outlined.AutoStories,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(10.dp).size(20.dp),
                        )
                    }
                    Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                        Text("Today's review", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "${state.reviewWordsDue} words due",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    FilledTonalButton(onClick = { onAction(HomeAction.OpenReview) }) { Text("Review") }
                }
            }
        }
        item { SectionHeader("This week") }
        item {
            LingoLensCard(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)) {
                WeeklyActivityChart(state.weeklyActivity)
            }
        }
        item {
            LingoLensPrimaryButton(text = "Continue learning", onClick = { onAction(HomeAction.OpenLearn) })
        }
    }
}

@Composable
private fun HomeStat(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    LingoLensCard(modifier = modifier, contentPadding = PaddingValues(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(8.dp).size(18.dp),
                )
            }
            Column(Modifier.padding(start = 10.dp)) {
                Text(value, style = MaterialTheme.typography.titleMedium)
                Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun WeeklyActivityChart(activity: List<WeeklyActivityDay>) {
    val highest = activity.maxOfOrNull { it.uniqueWords }?.coerceAtLeast(1) ?: 1
    Row(
        modifier = Modifier.fillMaxWidth().height(90.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        activity.forEach { item ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.height(62.dp),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    Box(
                        modifier = Modifier
                            .width(12.dp)
                            .fillMaxHeight(item.uniqueWords.toFloat() / highest)
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            .background(MaterialTheme.colorScheme.primary),
                    )
                }
                Text(
                    item.dayLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    LingoLensTheme(darkTheme = false) {
        HomeScreen(state = HomeUiState(), onAction = {})
    }
}
