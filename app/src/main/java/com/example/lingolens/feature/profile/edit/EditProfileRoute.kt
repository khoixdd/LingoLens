package com.example.lingolens.feature.profile.edit

import androidx.compose.runtime.*
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun EditProfileRoute(onBack: () -> Unit, viewModel: EditProfileViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val currentBack by rememberUpdatedState(onBack)
    LaunchedEffect(state.isSaved) { if (state.isSaved) currentBack() }
    EditProfileScreen(state) { action ->
        if (action == EditProfileAction.Back) onBack() else viewModel.onAction(action)
    }
}
