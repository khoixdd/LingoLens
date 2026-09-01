package com.example.lingolens.feature.scan

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ScanRoute(
    onClose: () -> Unit,
    viewModel: ScanViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ScanScreen(
        state = state,
        onAction = { action ->
            if (action == ScanAction.Close) onClose() else viewModel.onAction(action)
        },
    )
}
