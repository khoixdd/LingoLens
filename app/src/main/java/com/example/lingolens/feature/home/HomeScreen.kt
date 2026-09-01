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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.lingolens.ui.theme.LingoLensTheme

@Composable
fun HomeScreen(
    state: HomeUiState,
    onAction: (HomeAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                text = "Hello, ${state.name}!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "A little practice today goes a long way.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HomeStat(
                    icon = { Icon(Icons.Outlined.LocalFireDepartment, contentDescription = null) },
                    value = "${state.streakDays} days",
                    label = "Streak",
                    modifier = Modifier.weight(1f),
                )
                HomeStat(
                    icon = { Icon(Icons.Outlined.Stars, contentDescription = null) },
                    value = "Lv. ${state.level}",
                    label = state.title,
                    modifier = Modifier.weight(1f),
                )
                HomeStat(
                    icon = { Text("XP", fontWeight = FontWeight.Bold) },
                    value = state.xp.toString(),
                    label = "Experience",
                    modifier = Modifier.weight(1f),
                )
            }
        }
        item {
            LingoLensCard {
                Text("Daily goal", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = {
                        (state.dailyWordsCompleted.toFloat() / state.dailyWordsGoal.coerceAtLeast(1))
                            .coerceIn(0f, 1f)
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "${state.dailyWordsCompleted} / ${state.dailyWordsGoal} words",
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    "Keep going! ${(state.dailyWordsGoal - state.dailyWordsCompleted).coerceAtLeast(0)} words left",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        item {
            LingoLensCard {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.AutoStories,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                        Text("Today's review", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text("${state.reviewWordsDue} words due", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Button(onClick = { onAction(HomeAction.OpenReview) }) { Text("Review now") }
                }
            }
        }
        item {
            LingoLensCard {
                Text("Weekly activity", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(16.dp))
                WeeklyActivityChart(state.weeklyActivity)
            }
        }
        item {
            Button(
                onClick = { onAction(HomeAction.OpenLearn) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Continue learning")
            }
        }
    }
}

@Composable
private fun HomeStat(
    icon: @Composable () -> Unit,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    LingoLensCard(modifier = modifier, contentPadding = PaddingValues(12.dp)) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Box(contentAlignment = Alignment.Center) { icon() }
            Text(value, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun WeeklyActivityChart(activity: List<DailyActivity>) {
    val highest = activity.maxOfOrNull { it.words }?.coerceAtLeast(1) ?: 1
    Row(
        modifier = Modifier.fillMaxWidth().height(104.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        activity.forEach { item ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier.height(72.dp),
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    Box(
                        modifier = Modifier
                            .width(16.dp)
                            .fillMaxHeight(item.words.toFloat() / highest)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.primary),
                    )
                }
                Text(item.day, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 6.dp))
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
