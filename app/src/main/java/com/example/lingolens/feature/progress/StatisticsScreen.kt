package com.example.lingolens.feature.progress

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lingolens.domain.model.WeeklyActivityDay
import com.example.lingolens.ui.components.DailyGoalCard
import com.example.lingolens.ui.components.LingoLensCard
import com.example.lingolens.ui.components.MasteryOverview
import com.example.lingolens.ui.components.WeeklyActivityChart
import com.example.lingolens.ui.theme.LingoLensTheme
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(state: StatisticsUiState, onAction: (StatisticsAction) -> Unit) {
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Statistics", style = MaterialTheme.typography.titleLarge) },
            navigationIcon = {
                IconButton({ onAction(StatisticsAction.Back) }) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back")
                }
            },
        )
    }) { insets ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(insets), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                Modifier.fillMaxSize().padding(insets),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item {
                    LingoLensCard(contentPadding = PaddingValues(14.dp), elevation = 0.dp) {
                        Text("Your Progress", style = MaterialTheme.typography.titleMedium)
                        Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            ProgressMetric("Lv. ${state.level}", "level", Icons.Outlined.Stars, Modifier.weight(1f))
                            ProgressMetric(state.streakDays.toString(), "day streak", Icons.Outlined.LocalFireDepartment,
                                Modifier.weight(1f), Color(0xFFBB632D))
                        }
                        Text("${state.xp} XP", Modifier.padding(top = 12.dp, bottom = 7.dp),
                            style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                        LinearProgressIndicator(
                            progress = { (state.xpProgress.toFloat() / state.xpPerLevel.coerceAtLeast(1)).coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth().height(5.dp).clip(CircleShape),
                            trackColor = MaterialTheme.colorScheme.primaryContainer,
                            drawStopIndicator = {},
                        )
                        Text("${state.xpProgress} / ${state.xpPerLevel} XP toward the next level",
                            Modifier.padding(top = 5.dp), style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                item { DailyGoalCard(completed = state.todayWords, target = state.dailyGoal) }
                item {
                    LingoLensCard(contentPadding = PaddingValues(14.dp), elevation = 0.dp) {
                        Text("This Week", style = MaterialTheme.typography.titleMedium)
                        Text("${state.weeklyActivity.sumOf { it.uniqueWords }} words studied this week",
                            Modifier.padding(top = 2.dp, bottom = 10.dp),
                            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        WeeklyActivityChart(state.weeklyActivity)
                    }
                }
                item {
                    MasteryOverview("Vocabulary", state.totalWords,
                        state.newWords, state.learningWords, state.familiarWords, state.masteredWords,
                        showTotal = true)
                }
            }
        }
    }
}

@Composable
private fun ProgressMetric(
    value: String,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    accent: Color = MaterialTheme.colorScheme.primary,
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, Modifier.size(23.dp), tint = accent)
        Column(Modifier.weight(1f).padding(start = 8.dp)) {
            Text(value, style = MaterialTheme.typography.headlineSmall)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Preview(name = "Statistics populated", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun StatisticsPopulatedPreview() {
    val today = LocalDate.of(2026, 9, 5)
    LingoLensTheme(darkTheme = false) {
        StatisticsScreen(StatisticsUiState(
            isLoading = false, todayWords = 6, totalWords = 16,
            newWords = 0, learningWords = 9, familiarWords = 4, masteredWords = 3,
            xp = 600, level = 4, xpProgress = 0, streakDays = 4,
            weeklyActivity = listOf(3, 5, 2, 7, 4, 8, 6).mapIndexed { index, count ->
                WeeklyActivityDay(today.minusDays(6L - index).toEpochDay(), "", count)
            },
        ), {})
    }
}
