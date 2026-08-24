package com.example.lingolens.feature.learn.detail

import androidx.lifecycle.ViewModel
import com.example.lingolens.feature.learn.notebook.sampleVocabulary
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class VocabularyDetailViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(VocabularyDetailUiState())
    val uiState: StateFlow<VocabularyDetailUiState> = _uiState.asStateFlow()

    fun load(wordId: String) {
        sampleVocabulary.firstOrNull { it.id == wordId }?.let { word -> _uiState.value = VocabularyDetailUiState(word.id, word.word, word.pronunciation, word.partOfSpeech, word.meaning, "Smartphones have become ${word.word} in modern life.", word.tags, word.mastery, word.isFavorite) }
    }

    fun onAction(action: VocabularyDetailAction) {
        if (action == VocabularyDetailAction.ToggleFavorite) _uiState.update { it.copy(isFavorite = !it.isFavorite) }
    }
}
