package com.example.lingolens.feature.learn

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lingolens.ui.components.LingoLensCard
import com.example.lingolens.ui.components.SectionHeader
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
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text("Ready to learn?", style = MaterialTheme.typography.headlineMedium)
        }
        item {
            LingoLensCard(containerColor = MaterialTheme.colorScheme.primaryContainer, contentPadding = PaddingValues(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Review Today", style = MaterialTheme.typography.titleMedium)
                        Text("${state.reviewCount} words due", style = MaterialTheme.typography.bodySmall)
                    }
                    TextButton(onClick = { onAction(LearnAction.StartReview) }) {
                        Text("Start Review")
                        Icon(Icons.AutoMirrored.Outlined.ArrowForward, null, Modifier.size(16.dp))
                    }
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                LearningShortcut(
                    modifier = Modifier.weight(1f),
                    title = "Notebook",
                    subtitle = "${state.notebookCount} words",
                    icon = Icons.Outlined.AutoStories,
                    onClick = { onAction(LearnAction.OpenNotebook) },
                )
                LearningShortcut(
                    modifier = Modifier.weight(1f),
                    title = "Quiz",
                    subtitle = "Practice",
                    icon = Icons.Outlined.Quiz,
                    onClick = { onAction(LearnAction.StartQuiz) },
                )
            }
        }
        item {
            LingoLensCard(contentPadding = PaddingValues(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Daily goal", style = MaterialTheme.typography.labelLarge)
                        Text(
                            "${state.dailyGoalCompleted} of ${state.dailyGoalTarget} words",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    TextButton(onClick = { onAction(LearnAction.OpenStatistics) }) {
                        Icon(Icons.Outlined.BarChart, null, Modifier.size(18.dp))
                        Text(" Statistics")
                    }
                }
                Spacer(Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = {
                        (state.dailyGoalCompleted.toFloat() / state.dailyGoalTarget.coerceAtLeast(1)).coerceIn(0f, 1f)
                    },
                    modifier = Modifier.fillMaxWidth().height(7.dp).clip(CircleShape),
                    trackColor = MaterialTheme.colorScheme.primaryContainer,
                )
            }
        }
        item { SectionHeader("Learning status") }
        item {
            Column(Modifier.fillMaxWidth().padding(horizontal = 2.dp)) {
                LearningStatusRow("New", state.newCount)
                LearningStatusRow("Learning", state.learningCount)
                LearningStatusRow("Familiar", state.familiarCount)
                LearningStatusRow("Mastered", state.masteredCount)
            }
        }
    }
}

@Composable
private fun LearningShortcut(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Surface(
        modifier = modifier.then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(9.dp).size(20.dp),
                )
            }
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun LearningStatusRow(label: String, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
        Text(count.toString(), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
    }
}

@Preview(showBackground = true)
@Composable
private fun LearnScreenPreview() {
    LingoLensTheme(darkTheme = false) { LearnScreen(LearnUiState(), onAction = {}) }
}
