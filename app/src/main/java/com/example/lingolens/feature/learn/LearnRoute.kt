package com.example.lingolens.feature.learn

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LearnRoute(
    onOpenNotebook: () -> Unit,
    onStartReview: () -> Unit,
    onStartQuiz: () -> Unit,
    onOpenStatistics: () -> Unit,
    viewModel: LearnViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LearnScreen(
        state = state,
        onAction = { action ->
            when (action) {
                LearnAction.OpenNotebook -> onOpenNotebook()
                LearnAction.StartReview -> onStartReview()
                LearnAction.StartQuiz -> onStartQuiz()
                LearnAction.OpenStatistics -> onOpenStatistics()
            }
        },
    )
}
