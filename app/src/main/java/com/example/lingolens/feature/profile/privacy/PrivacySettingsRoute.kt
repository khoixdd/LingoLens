package com.example.lingolens.feature.profile.privacy

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PrivacySettingsRoute(onBack: () -> Unit, viewModel: PrivacySettingsViewModel = hiltViewModel()) { val state by viewModel.uiState.collectAsStateWithLifecycle(); PrivacySettingsScreen(state, { if (it == PrivacySettingsAction.Back) onBack() else viewModel.onAction(it) }) }
