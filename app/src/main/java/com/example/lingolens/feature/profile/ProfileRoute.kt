package com.example.lingolens.feature.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ProfileRoute(
    onOpenMyWords: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenPrivacy: () -> Unit,
    onLogout: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isLoggedOut) {
        if (state.isLoggedOut) {
            onLogout()
        }
    }

    ProfileScreen(
        state = state,
        onAction = { action ->
            when (action) {
                ProfileAction.OpenMyWords -> onOpenMyWords()
                ProfileAction.OpenNotifications -> onOpenNotifications()
                ProfileAction.OpenPrivacy -> onOpenPrivacy()
                ProfileAction.Logout -> viewModel.onAction(ProfileAction.Logout)
                ProfileAction.OpenAchievements, ProfileAction.OpenStatistics -> Unit
            }
        },
    )
}
