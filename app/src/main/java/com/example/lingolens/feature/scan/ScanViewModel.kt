package com.example.lingolens.feature.scan

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class ScanViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    fun onAction(action: ScanAction) {
        when (action) {
            is ScanAction.ToggleFlash -> _uiState.update { it.copy(isFlashEnabled = !it.isFlashEnabled) }
            is ScanAction.Capture -> _uiState.update {
                it.copy(feedbackMessage = "Camera and text recognition are not connected yet.")
            }
            is ScanAction.OpenGallery -> _uiState.update {
                it.copy(feedbackMessage = "Gallery import is coming in the next scan integration.")
            }
            is ScanAction.TextDetected -> _uiState.update { it.copy(extractedText = action.words) }
            is ScanAction.ErrorOccurred -> _uiState.update { it.copy(feedbackMessage = action.message) }
            is ScanAction.DismissFeedback -> _uiState.update { it.copy(feedbackMessage = null) }

            is ScanAction.Close -> Unit
        }
    }
}
