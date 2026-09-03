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
    data object LaunchGallery : ScanEvent
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
                it.copy(feedbackMessage = "Capturing image and extracting text...")
            }
            is ScanAction.OpenGallery -> _uiState.update {
                if (!uiState.value.isScanning) {
                    viewModelScope.launch {
                        _events.send(ScanEvent.LaunchGallery)
                    }
                }

                it.copy(isScanning = false, feedbackMessage = "Opening gallery...")
            }
            // is ScanAction.TextDetected -> { 
            //     // _uiState.update { 
            //     //     it.copy(
            //     //         extractedText = action.words, 
            //     //         feedbackMessage = "Found ${action.words.size} words!"
            //     //     ) 
            //     // }
            // }
            is ScanAction.CaptureText -> {
                if (action.words.isEmpty()) {
                    _uiState.update { 
                        it.copy(
                            isScanning = false, 
                            feedbackMessage = "No text detected. Please try again."
                        ) 
                    }
                } 
                else if (action.words.size > 10) {
                    _uiState.update { 
                        it.copy(
                            isScanning = false, 
                            feedbackMessage = "Too many words detected (${action.words.size}). Please try again with a clearer image."
                        ) 
                    }
                } 
                else if (action.words.any { it.length < 2 }) {
                    _uiState.update { 
                        it.copy(
                            isScanning = false, 
                            feedbackMessage = "Some detected words are too short. Please try again with a clearer image."
                        ) 
                    }
                } 
                else if (action.words.any { !it.all { char -> char.isLetter() } }) {
                    _uiState.update { 
                        it.copy(
                            isScanning = false, 
                            feedbackMessage = "Some detected words contain non-letter characters. Please try again with a clearer image."
                        ) 
                    }
                } 
                // else if (action.words.any { !repository.isWordValid(it) }) {
                //     _uiState.update { 
                //         it.copy(
                //             isScanning = false, 
                //             feedbackMessage = "Some detected words are not valid English words. Please try again with a clearer image."
                //         ) 
                //     }
                // } 
                else {
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

                    _uiState.update { 
                        it.copy(
                            isScanning = false
                        ) 
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
