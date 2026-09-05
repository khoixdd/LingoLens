package com.example.lingolens.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeRoute(
    onOpenNotebook: () -> Unit,
    onOpenQuiz: () -> Unit,
    onOpenStatistics: () -> Unit,
    onOpenAchievements: () -> Unit,
    onOpenLearn: () -> Unit,
    onOpenReview: () -> Unit,
    onOpenNotifications: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreen(
        state = state,
        onAction = { action ->
            when (action) {
                HomeAction.OpenNotebook -> onOpenNotebook()
                HomeAction.OpenQuiz -> onOpenQuiz()
                HomeAction.OpenStatistics -> onOpenStatistics()
                HomeAction.OpenAchievements -> onOpenAchievements()
                HomeAction.OpenLearn -> onOpenLearn()
                HomeAction.OpenReview -> onOpenReview()
                HomeAction.OpenNotifications -> onOpenNotifications()
            }
        },
    )
}
