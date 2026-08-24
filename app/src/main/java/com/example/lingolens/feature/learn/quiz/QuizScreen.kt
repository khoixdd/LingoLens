package com.example.lingolens.feature.learn.quiz

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lingolens.ui.components.LingoLensCard
import com.example.lingolens.ui.theme.LingoLensTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(state: QuizUiState, onAction: (QuizAction) -> Unit, modifier: Modifier = Modifier) {
    Scaffold(modifier, topBar = { TopAppBar(title = { Text("Quiz") }, navigationIcon = { IconButton({ onAction(QuizAction.Back) }) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back") } }) }) { insets ->
        LazyColumn(Modifier.fillMaxSize().padding(insets), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item { Text("Question ${state.questionIndex + 1} / ${state.totalQuestions}", fontWeight = FontWeight.SemiBold); LinearProgressIndicator(progress = { (state.questionIndex + 1) / state.totalQuestions.toFloat() }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) }
            item { Text(state.prompt, color = MaterialTheme.colorScheme.onSurfaceVariant); Text(state.word, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp)) }
            items(state.options.size) { index -> QuizOption(index, state.options[index], state, { onAction(QuizAction.SelectAnswer(index)) }) }
            if (state.answerState == QuizAnswerState.Correct || state.answerState == QuizAnswerState.Incorrect) item {
                val correct = state.answerState == QuizAnswerState.Correct
                LingoLensCard { Icon(if (correct) Icons.Outlined.CheckCircle else Icons.Outlined.Cancel, null, tint = if (correct) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error); Text(if (correct) "Correct! Well done." else "Not quite. The correct answer is C.", fontWeight = FontWeight.SemiBold) }
            }
            item {
                val checked = state.answerState == QuizAnswerState.Correct || state.answerState == QuizAnswerState.Incorrect
                Button(onClick = { onAction(if (checked) QuizAction.Next else QuizAction.CheckAnswer) }, enabled = state.selectedIndex != null, modifier = Modifier.fillMaxWidth()) { Text(if (checked) "Next" else "Check answer") }
            }
        }
    }
}

@Composable
private fun QuizOption(index: Int, text: String, state: QuizUiState, onClick: () -> Unit) {
    val checked = state.answerState == QuizAnswerState.Correct || state.answerState == QuizAnswerState.Incorrect
    val isSelected = state.selectedIndex == index
    val isCorrect = checked && index == state.correctIndex
    val isWrong = checked && isSelected && !isCorrect
    val container = when { isCorrect -> MaterialTheme.colorScheme.primaryContainer; isWrong -> MaterialTheme.colorScheme.errorContainer; isSelected -> MaterialTheme.colorScheme.surfaceVariant; else -> MaterialTheme.colorScheme.surface }
    val border = when { isCorrect -> MaterialTheme.colorScheme.primary; isWrong -> MaterialTheme.colorScheme.error; isSelected -> MaterialTheme.colorScheme.primary; else -> MaterialTheme.colorScheme.outlineVariant }
    Card(onClick = onClick, enabled = !checked, colors = CardDefaults.cardColors(containerColor = container), border = BorderStroke(if (isSelected || isCorrect) 2.dp else 1.dp, border), shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Text("${('A'.code + index).toChar()}.  $text", modifier = Modifier.padding(18.dp), fontWeight = if (isSelected || isCorrect) FontWeight.SemiBold else FontWeight.Normal)
    }
}

@Preview(showBackground = true)
@Composable
private fun QuizPreview() { LingoLensTheme(darkTheme = false) { QuizScreen(QuizUiState(selectedIndex = 2, answerState = QuizAnswerState.Correct), {}) } }
