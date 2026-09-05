package com.example.lingolens.feature.learn

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lingolens.ui.components.DailyGoalCard
import com.example.lingolens.ui.components.LingoLensCard
import com.example.lingolens.ui.components.MasteryOverview
import com.example.lingolens.ui.theme.LingoLensTheme

@Composable
fun LearnScreen(
    state: LearnUiState,
    onAction: (LearnAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.isLoading) {
        Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Column {
                Text("Ready to learn?", style = MaterialTheme.typography.headlineSmall)
                Text("Small steps, stronger vocabulary.", Modifier.padding(top = 3.dp),
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item {
            LingoLensCard(containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentPadding = PaddingValues(14.dp), elevation = 0.dp) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.AutoStories, null, Modifier.size(28.dp), tint = MaterialTheme.colorScheme.primary)
                    Column(Modifier.weight(1f).padding(start = 10.dp)) {
                        Text("Review Today", style = MaterialTheme.typography.titleMedium)
                        Text(
                            if (state.reviewCount == 0) "You're all caught up!"
                            else "${state.reviewCount} ${if (state.reviewCount == 1) "word" else "words"} due",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
                FilledTonalButton(
                    onClick = { onAction(LearnAction.StartReview) },
                    modifier = Modifier.align(Alignment.End).padding(top = 6.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                ) {
                    Text(if (state.reviewCount > 0) "Start Review" else "Review", style = MaterialTheme.typography.labelLarge)
                    Icon(Icons.AutoMirrored.Outlined.ArrowForward, null, Modifier.padding(start = 6.dp).size(16.dp))
                }
            }
        }
        item {
            Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                LearningShortcut("Notebook", "${state.notebookCount} words", Icons.Outlined.AutoStories,
                    Modifier.weight(1f).fillMaxHeight()) { onAction(LearnAction.OpenNotebook) }
                LearningShortcut("Quiz", "Practice", Icons.Outlined.Quiz,
                    Modifier.weight(1f).fillMaxHeight(), Color(0xFF8261A7)) { onAction(LearnAction.StartQuiz) }
            }
        }
        item {
            DailyGoalCard(
                completed = state.dailyGoalCompleted,
                target = state.dailyGoalTarget,
                containerColor = lerp(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.primaryContainer, 0.25f),
                action = {
                    TextButton(onClick = { onAction(LearnAction.OpenStatistics) },
                        contentPadding = PaddingValues(horizontal = 4.dp)) {
                        Icon(Icons.Outlined.BarChart, null, Modifier.size(16.dp))
                        Text("Statistics", Modifier.padding(horizontal = 6.dp), style = MaterialTheme.typography.labelMedium)
                        Icon(Icons.AutoMirrored.Outlined.ArrowForward, null, Modifier.size(14.dp))
                    }
                },
            )
        }
        item {
            MasteryOverview("Learning Status", state.notebookCount,
                state.newCount, state.learningCount, state.familiarCount, state.masteredCount)
        }
    }
}

@Composable
private fun LearningShortcut(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    accent: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit,
) {
    Surface(onClick = onClick, modifier = modifier, shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface, shadowElevation = 0.dp) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, Modifier.size(24.dp), tint = accent)
            Column(Modifier.weight(1f).padding(start = 10.dp)) {
                Text(title, style = MaterialTheme.typography.titleSmall)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Preview(name = "Learn populated", showBackground = true, widthDp = 360, heightDp = 760)
@Composable
private fun LearnPopulatedPreview() {
    LingoLensTheme(darkTheme = false) {
        LearnScreen(LearnUiState(isLoading = false, reviewCount = 4, notebookCount = 16,
            newCount = 0, learningCount = 9, familiarCount = 4, masteredCount = 3,
            dailyGoalCompleted = 6), {})
    }
}

@Preview(name = "Learn caught up", showBackground = true, widthDp = 320, heightDp = 568)
@Composable
private fun LearnCaughtUpPreview() {
    LingoLensTheme(darkTheme = false) {
        LearnScreen(LearnUiState(isLoading = false, reviewCount = 0, notebookCount = 16,
            newCount = 0, learningCount = 9, familiarCount = 4, masteredCount = 3,
            dailyGoalCompleted = 10), {})
    }
}
