package com.example.lingolens.feature.profile.achievements

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AchievementsRoute(
    onBack: () -> Unit,
    viewModel: AchievementsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    AchievementsScreen(state) { if (it == AchievementsAction.Back) onBack() }
}

