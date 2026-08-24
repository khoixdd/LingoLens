package com.example.lingolens.feature.learn

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lingolens.ui.components.LingoLensCard
import com.example.lingolens.ui.theme.LingoLensTheme

@Composable
fun LearnScreen(
    state: LearnUiState,
    onAction: (LearnAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text("Ready to learn?", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Keep your momentum going today.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item {
            LingoLensCard {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Outlined.Refresh, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Column(Modifier.weight(1f)) {
                        Text("Review today", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text("${state.reviewCount} words due", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Button(onClick = { onAction(LearnAction.StartReview) }) { Text("Start") }
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                LearningShortcut(
                    modifier = Modifier.weight(1f),
                    title = "Notebook",
                    subtitle = "${state.notebookCount} words",
                    icon = { Icon(Icons.Outlined.AutoStories, contentDescription = null) },
                    onClick = { onAction(LearnAction.OpenNotebook) },
                )
                LearningShortcut(
                    modifier = Modifier.weight(1f),
                    title = "Quiz",
                    subtitle = "Practice",
                    icon = { Icon(Icons.Outlined.Quiz, contentDescription = null) },
                    onClick = { onAction(LearnAction.StartQuiz) },
                )
            }
        }
        item {
            LingoLensCard {
                Text("Daily goal", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { (state.dailyGoalCompleted.toFloat() / state.dailyGoalTarget).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                Text("${state.dailyGoalCompleted} of ${state.dailyGoalTarget} words", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        item { Text("Learning status", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) }
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    LearningStat("New", state.newCount, Modifier.weight(1f))
                    LearningStat("Learning", state.learningCount, Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    LearningStat("Familiar", state.familiarCount, Modifier.weight(1f))
                    LearningStat("Mastered", state.masteredCount, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun LearningShortcut(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    androidx.compose.material3.Card(
        modifier = modifier,
        onClick = onClick,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            icon()
            Text(title, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun LearningStat(label: String, count: Int, modifier: Modifier = Modifier) {
    LingoLensCard(modifier = modifier) {
        Text(count.toString(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Preview(showBackground = true)
@Composable
private fun LearnScreenPreview() {
    LingoLensTheme(darkTheme = false) { LearnScreen(LearnUiState(), onAction = {}) }
}
