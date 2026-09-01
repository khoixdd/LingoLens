package com.example.lingolens.feature.learn

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Text("Learn", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Build vocabulary one small session at a time.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                        title = "Review",
                        subtitle = "${state.reviewCount} due",
                        icon = Icons.Outlined.Refresh,
                        onClick = { onAction(LearnAction.StartReview) },
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    LearningShortcut(
                        modifier = Modifier.weight(1f),
                        title = "Quiz",
                        subtitle = "Practice",
                        icon = Icons.Outlined.Quiz,
                        onClick = { onAction(LearnAction.StartQuiz) },
                    )
                    LearningShortcut(
                        modifier = Modifier.weight(1f),
                        title = "Statistics",
                        subtitle = "See progress",
                        icon = Icons.Outlined.BarChart,
                    )
                }
            }
        }
        item { SectionHeader("Keep learning") }
        item {
            LingoLensCard(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Daily goal", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "${state.dailyGoalCompleted} of ${state.dailyGoalTarget} words",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    Text(
                        "${(state.dailyGoalCompleted * 100 / state.dailyGoalTarget.coerceAtLeast(1)).coerceIn(0, 100)}%",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Spacer(Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = {
                        (state.dailyGoalCompleted.toFloat() / state.dailyGoalTarget.coerceAtLeast(1)).coerceIn(0f, 1f)
                    },
                    modifier = Modifier.fillMaxWidth().height(7.dp).clip(CircleShape),
                    trackColor = MaterialTheme.colorScheme.surface,
                )
            }
        }
        item { SectionHeader("Learning status") }
        item {
            LingoLensCard(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)) {
                LearningStatusRow("New", state.newCount)
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                LearningStatusRow("Learning", state.learningCount)
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                LearningStatusRow("Familiar", state.familiarCount)
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
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
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
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
