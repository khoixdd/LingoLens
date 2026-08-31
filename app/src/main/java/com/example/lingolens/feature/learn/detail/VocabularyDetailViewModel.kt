package com.example.lingolens.feature.learn.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lingolens.domain.model.Vocabulary
import com.example.lingolens.domain.repository.VocabularyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class VocabularyDetailViewModel @Inject constructor(
    private val repository: VocabularyRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(VocabularyDetailUiState())
    val uiState: StateFlow<VocabularyDetailUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null
    private var currentVocabulary: Vocabulary? = null

    fun load(wordId: String) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            repository.getVocabularyById(wordId).collect { word ->
                if (word != null) {
                    currentVocabulary = word
                    _uiState.update {
                        it.copy(
                            id = word.id,
                            word = word.word,
                            pronunciation = word.pronunciation,
                            partOfSpeech = word.partOfSpeech,
                            meaning = word.meaning,
                            example = word.example,
                            tags = word.tags,
                            mastery = word.masteryLevel,
                            isFavorite = word.isFavorite,
                            isLoading = false,
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun onAction(action: VocabularyDetailAction) {
        when (action) {
            VocabularyDetailAction.ToggleFavorite -> {
                val id = _uiState.value.id
                if (id.isNotBlank()) {
                    viewModelScope.launch {
                        repository.toggleFavorite(id)
                    }
                }
            }
            VocabularyDetailAction.Delete -> {
                val id = _uiState.value.id
                if (id.isNotBlank()) {
                    viewModelScope.launch {
                        repository.deleteVocabulary(id)
                        _uiState.update { it.copy(isDeleted = true) }
                    }
                }
            }
            VocabularyDetailAction.Edit -> {
                _uiState.update { it.copy(showEditDialog = true) }
            }
            is VocabularyDetailAction.ShowEditDialog -> {
                _uiState.update { it.copy(showEditDialog = action.show) }
            }
            is VocabularyDetailAction.SaveEdit -> {
                val current = currentVocabulary
                if (current != null) {
                    val updated = current.copy(
                        meaning = action.meaning,
                        example = action.example,
                        pronunciation = action.pronunciation,
                        partOfSpeech = action.partOfSpeech,
                    )
                    viewModelScope.launch {
                        repository.updateVocabulary(updated)
                        _uiState.update { it.copy(showEditDialog = false) }
                    }
                }
            }
            VocabularyDetailAction.Back, VocabularyDetailAction.PlayPronunciation -> Unit
        }
    }
}
