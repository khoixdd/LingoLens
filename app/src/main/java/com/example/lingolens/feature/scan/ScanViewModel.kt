package com.example.lingolens.feature.scan

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.UUID
import com.example.lingolens.domain.repository.VocabularyRepository
import com.example.lingolens.domain.model.Vocabulary

sealed interface ScanEvent {
    data object NavigateToLearn : ScanEvent
}

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val repository: VocabularyRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    private val _events = Channel<ScanEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: ScanAction) {
        when (action) {
            is ScanAction.ToggleFlash -> _uiState.update { it.copy(isFlashEnabled = !it.isFlashEnabled) }
            is ScanAction.Capture -> _uiState.update {
                it.copy(isScanning = true)
                it.copy(feedbackMessage = "Camera and text recognition are not connected yet.")
            }
            is ScanAction.OpenGallery -> _uiState.update {
                it.copy(isScanning = false, feedbackMessage = "Gallery import is coming in the next scan integration.")
            }
            is ScanAction.TextDetected -> { 
                if (action.words.isEmpty()) {
                    _uiState.update { 
                        it.copy(isScanning = false, feedbackMessage = "No text detected. Please try again.") 
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isScanning = false, 
                            extractedText = action.words, 
                            feedbackMessage = "Found ${action.words.size} words!"
                        ) 
                    }
                    viewModelScope.launch {
                        action.words.forEach { word ->
                            if (!repository.isWordDuplicate(word)) {
                                repository.addVocabulary(
                                    Vocabulary(
                                        id = UUID.randomUUID().toString(),
                                        word = word,
                                        pronunciation = "",
                                        partOfSpeech = "",
                                        meaning = "",
                                        example = "",
                                        tags = emptyList(),
                                    )
                                )
                            }
                        }

                        _events.send(ScanEvent.NavigateToLearn)
                    }
                }
            }
            is ScanAction.ErrorOccurred -> _uiState.update { it.copy(isScanning = false, feedbackMessage = action.message) }
            is ScanAction.DismissFeedback -> _uiState.update { it.copy(isScanning = false, feedbackMessage = null) }

            is ScanAction.Close -> _uiState.update { it.copy(isScanning = false, feedbackMessage = null) }
            else -> {}
        }
    }
}
