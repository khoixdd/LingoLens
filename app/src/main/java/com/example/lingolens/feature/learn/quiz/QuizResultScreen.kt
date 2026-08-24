package com.example.lingolens.feature.learn.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Celebration
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
fun QuizResultScreen(score: Int, total: Int, onReviewAnswers: () -> Unit, onBackToLearn: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Outlined.Celebration, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
        Text("Great job!", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
        Text("$score / $total Correct", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 12.dp))
        Text("+${score * 10} XP", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 8.dp, bottom = 32.dp))
        Button(onReviewAnswers, Modifier.fillMaxWidth()) { Text("Review Answers") }
        OutlinedButton(onBackToLearn, Modifier.fillMaxWidth().padding(top = 8.dp)) { Text("Back to Learn") }
    }
}

@Preview(showBackground = true)
@Composable
private fun QuizResultPreview() { LingoLensTheme(darkTheme = false) { QuizResultScreen(8, 10, {}, {}) } }
