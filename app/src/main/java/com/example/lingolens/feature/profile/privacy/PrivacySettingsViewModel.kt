package com.example.lingolens.feature.profile.privacy

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class PrivacySettingsViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(PrivacySettingsUiState())
    val uiState: StateFlow<PrivacySettingsUiState> = _uiState.asStateFlow()
    fun onAction(action: PrivacySettingsAction) { if (action is PrivacySettingsAction.ShareLocationChanged) _uiState.update { it.copy(shareLocation = action.enabled, locationPermission = if (action.enabled) "Required" else "Not granted") } }
}
