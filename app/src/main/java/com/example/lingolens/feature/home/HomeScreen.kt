package com.example.lingolens.feature.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lingolens.ui.components.WeeklyActivityChart
import com.example.lingolens.ui.components.LingoLensCard
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    state: HomeUiState,
    onAction: (HomeAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isLoading) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }
    val goalProgress = (state.dailyWordsCompleted.toFloat() / state.dailyWordsGoal.coerceAtLeast(1)).coerceIn(0f, 1f)
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f).padding(end = 8.dp)) {
                    Text("Hello, ${state.name}!", style = MaterialTheme.typography.headlineSmall)
                    Text("Ready for a little progress today?",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 3.dp))
                }
                FilledTonalIconButton(onClick = { onAction(HomeAction.OpenNotifications) },
                    modifier = Modifier.size(48.dp)) {
                    Icon(Icons.Outlined.NotificationsNone, "Notifications", Modifier.size(21.dp))
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CompactStat(
                    icon = Icons.Outlined.LocalFireDepartment,
                    value = state.streakDays.toString(),
                    label = "day streak",
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    accent = Color(0xFFBB632D),
                )
                CompactStat(
                    icon = Icons.Outlined.Stars,
                    value = "Lv. ${state.level}",
                    label = state.title,
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    xpLabel = "${state.xp} XP",
                    progress = state.xpProgressInLevel.toFloat() / state.xpPerLevel.coerceAtLeast(1),
                )
            }
        }
        item {
            LingoLensCard(containerColor = MaterialTheme.colorScheme.primaryContainer, contentPadding = PaddingValues(16.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Daily Goal", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)) {
                        Text("${(goalProgress * 100).roundToInt()}%",
                            Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
                    }
                }
                Text("${state.dailyWordsCompleted} / ${state.dailyWordsGoal} words",
                    style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(top = 6.dp, bottom = 10.dp))
                LinearProgressIndicator(
                    progress = { goalProgress },
                    modifier = Modifier.fillMaxWidth().height(9.dp).clip(CircleShape),
                    trackColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                    drawStopIndicator = {},
                )
                Text(
                    if (state.dailyWordsCompleted >= state.dailyWordsGoal) "Goal complete! Nice work today." else
                        "Keep going! ${(state.dailyWordsGoal - state.dailyWordsCompleted).coerceAtLeast(0)} words left",
                    style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 8.dp))
            }
        }
        item {
            LingoLensCard(contentPadding = PaddingValues(12.dp),
                containerColor = lerp(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.primaryContainer, 0.45f),
                elevation = 0.dp) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.AutoStories, null, Modifier.size(28.dp), tint = MaterialTheme.colorScheme.primary)
                    Column(Modifier.weight(1f).padding(start = 10.dp)) {
                        Text("Today's Review", style = MaterialTheme.typography.titleSmall)
                        Text("${state.reviewWordsDue} ${if (state.reviewWordsDue == 1) "word" else "words"} due",
                            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (state.reviewWordsDue == 0) {
                            Text("You're all caught up!", style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    TextButton(onClick = { onAction(HomeAction.OpenReview) },
                        contentPadding = PaddingValues(horizontal = 8.dp)) {
                        Text("Review now", style = MaterialTheme.typography.labelMedium)
                        Icon(Icons.AutoMirrored.Outlined.ArrowForward, null, Modifier.padding(start = 4.dp).size(14.dp))
                    }
                }
            }
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Quick Actions", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 2.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuickAction("Notebook", Icons.AutoMirrored.Outlined.MenuBook, Modifier.weight(1f)) {
                        onAction(HomeAction.OpenNotebook)
                    }
                    QuickAction("Quiz", Icons.Outlined.Quiz, Modifier.weight(1f), Color(0xFF8261A7)) {
                        onAction(HomeAction.OpenQuiz)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuickAction("Statistics", Icons.Outlined.BarChart, Modifier.weight(1f), Color(0xFF398482)) {
                        onAction(HomeAction.OpenStatistics)
                    }
                    QuickAction("Achievements", Icons.Outlined.EmojiEvents, Modifier.weight(1f), Color(0xFFA77A13)) {
                        onAction(HomeAction.OpenAchievements)
                    }
                }
            }
        }
        item {
            LingoLensCard(contentPadding = PaddingValues(14.dp)) {
                Text("This Week", style = MaterialTheme.typography.titleSmall)
                Text("${state.weeklyActivity.sumOf { it.uniqueWords }} words studied this week",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp, bottom = 10.dp))
                WeeklyActivityChart(state.weeklyActivity)
            }
        }
        item {
            Surface(onClick = { onAction(HomeAction.OpenLearn) }, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)) {
                Row(Modifier.padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.School, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                    Text("Continue Learning", Modifier.weight(1f).padding(horizontal = 10.dp),
                        style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Icon(Icons.AutoMirrored.Outlined.ArrowForward, null, Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
private fun CompactStat(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    accent: Color = MaterialTheme.colorScheme.primary,
    xpLabel: String? = null,
    progress: Float? = null,
) {
    LingoLensCard(modifier, PaddingValues(10.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        elevation = 0.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, Modifier.size(21.dp), tint = accent)
            Text(value, Modifier.weight(1f).padding(start = 7.dp),
                style = if (xpLabel == null) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        Text(if (xpLabel == null) label else "$label · $xpLabel", style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 2.dp))
        progress?.let {
            LinearProgressIndicator(progress = { it.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().padding(top = 5.dp).height(4.dp).clip(CircleShape),
                drawStopIndicator = {})
        }
    }
}

@Composable
private fun QuickAction(
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    accent: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit,
) {
    Surface(onClick = onClick, modifier = modifier, shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.22f)) {
        Row(Modifier.padding(horizontal = 12.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, Modifier.size(20.dp), tint = accent)
            Text(label, Modifier.weight(1f).padding(start = 8.dp), style = MaterialTheme.typography.labelMedium)
        }
    }
}
