package com.example.lingolens.feature.learn.quiz

import androidx.compose.ui.platform.testTag
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.graphicsLayer
import com.example.lingolens.ui.components.CelebrationOverlay
import kotlin.math.roundToInt
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lingolens.ui.theme.LingoLensTheme

@Composable
fun QuizResultScreen(
    score: Int,
    total: Int,
    resultId: Long,
    onReviewAnswers: () -> Unit,
    onBackToLearn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Each completed attempt owns its presentation state, even when scores match.
    var entered by rememberSaveable(resultId) { mutableStateOf(false) }
    var celebrating by remember(resultId) { mutableStateOf(false) }
    val reveal = remember(resultId) { Animatable(if (entered) 1f else 0f) }
    LaunchedEffect(resultId) {
        if (!entered) {
            entered = true
            celebrating = total > 0
            reveal.animateTo(1f, tween(900))
        }
    }
    Box(modifier.fillMaxSize()) {
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            Icons.Outlined.EmojiEvents,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(64.dp),
        )
        Text("Great job!", style = MaterialTheme.typography.headlineLarge, modifier = Modifier.padding(top = 14.dp))
        Box(
            modifier = Modifier
                .padding(top = 24.dp)
                .size(132.dp)
                .graphicsLayer { scaleX = 0.85f + reveal.value * 0.15f; scaleY = scaleX },
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                progress = { if (total > 0) score.toFloat() / total * reveal.value else 0f },
                modifier = Modifier.fillMaxSize(),
                strokeWidth = 8.dp,
                trackColor = MaterialTheme.colorScheme.primaryContainer,
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("${(score * reveal.value).roundToInt()} / $total", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                Text("Correct", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Text(
            "+${((score * 10 + 20) * reveal.value).roundToInt()} XP",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 18.dp, bottom = 28.dp),
        )
        Button(onReviewAnswers, Modifier.fillMaxWidth()) { Text("Review Answers") }
        OutlinedButton(onBackToLearn, Modifier.fillMaxWidth().padding(top = 8.dp)) { Text("Back to Learn") }
    }
    key(resultId) {
        CelebrationOverlay(
            celebrating,
            modifier = Modifier.testTag("quiz-result-confetti"),
            onFinished = { celebrating = false },
        )
    }
    }
}

@Preview(showBackground = true)
@Composable
private fun QuizResultPreview() { LingoLensTheme(darkTheme = false) { QuizResultScreen(8, 10, resultId = 1L, {}, {}) } }
