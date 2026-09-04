package com.example.lingolens.feature.progress

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.lingolens.domain.model.WeeklyActivityDay
import com.example.lingolens.ui.components.LingoLensCard
import com.example.lingolens.ui.components.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(state: StatisticsUiState, onAction: (StatisticsAction) -> Unit) {
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Statistics") },
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
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item { SectionHeader("Today") }
                item {
                    LingoLensCard(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                        Row { Text("Daily goal", Modifier.weight(1f)); Text("${state.todayWords} / ${state.dailyGoal} words") }
                        Spacer(Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { (state.todayWords.toFloat() / state.dailyGoal).coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth().height(7.dp).clip(CircleShape),
                        )
                    }
                }
                item { SectionHeader("This week") }
                item { LingoLensCard { ActivityChart(state.weeklyActivity) } }
                item { SectionHeader("Vocabulary") }
                item {
                    LingoLensCard(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)) {
                        MetricRow("Total saved", state.totalWords)
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        MetricRow("New", state.newWords)
                        MetricRow("Learning", state.learningWords)
                        MetricRow("Familiar", state.familiarWords)
                        MetricRow("Mastered", state.masteredWords)
                    }
                }
                item { SectionHeader("Gamification") }
                item {
                    LingoLensCard {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column(Modifier.weight(1f)) { Text("Level ${state.level}", style = MaterialTheme.typography.titleLarge); Text("${state.xp} XP") }
                            Column(horizontalAlignment = Alignment.End) { Text("${state.streakDays}", style = MaterialTheme.typography.titleLarge); Text("day streak") }
                        }
                        Spacer(Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = { state.xpProgress.toFloat() / state.xpPerLevel },
                            modifier = Modifier.fillMaxWidth().height(7.dp).clip(CircleShape),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricRow(label: String, value: Int) {
    Row(Modifier.fillMaxWidth().padding(vertical = 9.dp)) {
        Text(label, Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value.toString(), style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun ActivityChart(activity: List<WeeklyActivityDay>) {
    val highest = activity.maxOfOrNull { it.uniqueWords }?.coerceAtLeast(1) ?: 1
    Row(
        Modifier.fillMaxWidth().height(100.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        activity.forEach { day ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.height(68.dp), contentAlignment = Alignment.BottomCenter) {
                    Box(
                        Modifier.width(14.dp).fillMaxHeight(day.uniqueWords.toFloat() / highest)
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            .background(MaterialTheme.colorScheme.primary),
                    )
                }
                Text(day.dayLabel, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

