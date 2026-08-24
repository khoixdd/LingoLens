package com.example.lingolens.feature.profile.notification

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun NotificationSettingsRoute(onBack: () -> Unit, viewModel: NotificationSettingsViewModel = hiltViewModel()) { val state by viewModel.uiState.collectAsStateWithLifecycle(); NotificationSettingsScreen(state, { if (it == NotificationSettingsAction.Back) onBack() else viewModel.onAction(it) }) }
