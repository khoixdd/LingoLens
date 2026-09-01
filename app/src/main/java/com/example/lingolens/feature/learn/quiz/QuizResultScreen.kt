package com.example.lingolens.feature.learn.quiz

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
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
import androidx.compose.runtime.Composable
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
    onReviewAnswers: () -> Unit,
    onBackToLearn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            Icons.Outlined.EmojiEvents,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(48.dp),
        )
        Text("Great job!", style = MaterialTheme.typography.headlineLarge, modifier = Modifier.padding(top = 14.dp))
        Box(
            modifier = Modifier
                .padding(top = 24.dp)
                .size(132.dp)
                .border(BorderStroke(10.dp, MaterialTheme.colorScheme.primaryContainer), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("$score / $total", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                Text("Correct", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Text(
            "+${score * 10} XP",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 18.dp, bottom = 28.dp),
        )
        Button(onReviewAnswers, Modifier.fillMaxWidth()) { Text("Review Answers") }
        OutlinedButton(onBackToLearn, Modifier.fillMaxWidth().padding(top = 8.dp)) { Text("Back to Learn") }
    }
}

@Preview(showBackground = true)
@Composable
private fun QuizResultPreview() { LingoLensTheme(darkTheme = false) { QuizResultScreen(8, 10, {}, {}) } }
