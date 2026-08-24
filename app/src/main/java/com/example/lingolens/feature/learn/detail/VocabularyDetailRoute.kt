package com.example.lingolens.feature.learn.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun VocabularyDetailRoute(wordId: String, onBack: () -> Unit, viewModel: VocabularyDetailViewModel = hiltViewModel()) {
    LaunchedEffect(wordId) { viewModel.load(wordId) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    VocabularyDetailScreen(state, { action -> if (action == VocabularyDetailAction.Back) onBack() else viewModel.onAction(action) })
}
