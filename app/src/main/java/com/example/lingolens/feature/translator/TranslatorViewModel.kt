package com.example.lingolens.feature.translator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lingolens.core.mlkit.TranslationManager
import com.example.lingolens.core.mlkit.TranslationPair
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class TranslatorViewModel @Inject constructor(
    private val translationManager: TranslationManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TranslatorUiState())
    val uiState: StateFlow<TranslatorUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                translationManager.modelState(TranslationPair.EnglishToVietnamese),
                translationManager.modelState(TranslationPair.VietnameseToEnglish),
            ) { enViModelState, viEnModelState ->
                _uiState.update {
                    it.copy(
                        enViModelState = enViModelState,
                        viEnModelState = viEnModelState,
                    )
                }
            }.collect()
        }

        // Preload the default direction's model so the first translation is instant.
        warmUpModel(TranslationPair.EnglishToVietnamese)
    }

    fun onAction(action: TranslatorAction) {
        when (action) {
            is TranslatorAction.UpdateInput -> _uiState.update {
                it.copy(sourceText = action.text, errorMessage = null)
            }
            TranslatorAction.SwapDirection -> {
                val newDirection = if (_uiState.value.direction == TranslationPair.EnglishToVietnamese) {
                    TranslationPair.VietnameseToEnglish
                } else {
                    TranslationPair.EnglishToVietnamese
                }
                _uiState.update {
                    it.copy(
                        direction = newDirection,
                        result = null,
                        errorMessage = null,
                    )
                }
                warmUpModel(newDirection)
            }
            TranslatorAction.Translate -> translate()
            TranslatorAction.DismissError -> _uiState.update { it.copy(errorMessage = null) }
            TranslatorAction.Back -> Unit
        }
    }

    private fun warmUpModel(pair: TranslationPair) {
        viewModelScope.launch {
            runCatching { translationManager.ensureModelDownloaded(pair) }
        }
    }

    private fun translate() {
        val text = _uiState.value.sourceText
        val pair = _uiState.value.direction
        if (text.isBlank() || _uiState.value.isTranslating) return

        viewModelScope.launch {
            _uiState.update { it.copy(isTranslating = true, errorMessage = null) }
            try {
                val translated = translationManager.translate(pair, text)
                _uiState.update { it.copy(result = translated, isTranslating = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isTranslating = false, errorMessage = e.message ?: "Translation failed.")
                }
            }
        }
    }
}