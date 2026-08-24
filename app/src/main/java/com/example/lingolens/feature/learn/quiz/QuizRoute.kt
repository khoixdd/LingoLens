package com.example.lingolens.feature.learn.quiz

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun QuizRoute(onBack: () -> Unit, onFinished: (QuizCompletion) -> Unit, viewModel: QuizViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    QuizScreen(state, { action -> if (action == QuizAction.Back) onBack() else viewModel.onAction(action)?.let(onFinished) })
}
