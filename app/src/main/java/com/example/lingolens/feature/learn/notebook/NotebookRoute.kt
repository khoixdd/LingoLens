package com.example.lingolens.feature.learn.notebook

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun NotebookRoute(
    onBack: () -> Unit,
    onOpenWord: (String) -> Unit,
    viewModel: NotebookViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    NotebookScreen(state = state, onAction = { action ->
        when (action) {
            NotebookAction.Back -> onBack()
            is NotebookAction.WordSelected -> onOpenWord(action.wordId)
            else -> viewModel.onAction(action)
        }
    })
}
