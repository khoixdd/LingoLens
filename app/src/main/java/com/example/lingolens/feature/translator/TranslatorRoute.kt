package com.example.lingolens.feature.translator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun TranslatorRoute(
    onBack: () -> Unit,
    viewModel: TranslatorViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    TranslatorScreen(
        state = state,
        onAction = { action ->
            if (action == TranslatorAction.Back) onBack() else viewModel.onAction(action)
        },
    )
}