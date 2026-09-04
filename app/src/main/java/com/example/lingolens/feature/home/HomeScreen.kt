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
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.TextButton
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
import com.example.lingolens.ui.components.SectionHeader
import com.example.lingolens.ui.theme.LingoLensTheme
import com.example.lingolens.domain.model.WeeklyActivityDay
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

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
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(text = "Hello, ${state.name}!", style = MaterialTheme.typography.headlineSmall)
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
                    label = "${state.title} · ${state.xp} XP",
                    modifier = Modifier.weight(1f),
                    progress = state.xpProgressInLevel.toFloat() / state.xpPerLevel.coerceAtLeast(1),
                )
            }
        }
        item {
            LingoLensCard(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentPadding = PaddingValues(16.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Daily Goal", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "${state.dailyWordsCompleted} / ${state.dailyWordsGoal} words",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
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
                Text(
                    if (state.dailyWordsCompleted >= state.dailyWordsGoal) "Goal complete!" else
                        "Keep going! ${(state.dailyWordsGoal - state.dailyWordsCompleted).coerceAtLeast(0)} words left",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
        item {
            LingoLensCard(
                contentPadding = PaddingValues(14.dp),
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ) {
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
                    TextButton(onClick = { onAction(HomeAction.OpenReview) }) {
                        Text("Review now")
                        Icon(Icons.AutoMirrored.Outlined.ArrowForward, null, Modifier.size(16.dp))
                    }
                }
            }
        }
        item { SectionHeader("This week") }
        item { WeeklyActivityChart(state.weeklyActivity) }
    }
}

@Composable
private fun HomeStat(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    progress: Float? = null,
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
            Column(Modifier.padding(start = 10.dp).weight(1f)) {
                Text(value, style = MaterialTheme.typography.titleMedium)
                Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                progress?.let {
                    LinearProgressIndicator(
                        progress = { it.coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().padding(top = 5.dp).height(3.dp).clip(CircleShape),
                    )
                }
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
                            .fillMaxHeight((item.uniqueWords.toFloat() / highest).coerceAtLeast(0.14f))
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            .background(
                                if (item.uniqueWords == 0) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.primary,
                            ),
                    )
                }
                Text(
                    LocalDate.ofEpochDay(item.epochDay).dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH),
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
