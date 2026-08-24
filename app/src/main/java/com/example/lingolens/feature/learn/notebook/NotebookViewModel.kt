package com.example.lingolens.feature.learn.notebook

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class NotebookViewModel @Inject constructor() : ViewModel() {
    private var allWords = sampleVocabulary
    private val _uiState = MutableStateFlow(NotebookUiState(content = NotebookContentState.Content(allWords)))
    val uiState: StateFlow<NotebookUiState> = _uiState.asStateFlow()

    fun onAction(action: NotebookAction) {
        when (action) {
            is NotebookAction.SearchChanged -> _uiState.update { it.copy(searchQuery = action.query) }.also { refresh() }
            is NotebookAction.FilterSelected -> _uiState.update { it.copy(selectedFilter = action.filter) }.also { refresh() }
            is NotebookAction.FavoriteToggled -> {
                allWords = allWords.map { word -> if (word.id == action.wordId) word.copy(isFavorite = !word.isFavorite) else word }
                refresh()
            }
            NotebookAction.Back, is NotebookAction.WordSelected -> Unit
        }
    }

    private fun refresh() {
        val state = _uiState.value
        val words = allWords.filter { word ->
            val matchesSearch = state.searchQuery.isBlank() || word.word.contains(state.searchQuery, ignoreCase = true) || word.meaning.contains(state.searchQuery, ignoreCase = true)
            val matchesFilter = when (state.selectedFilter) {
                NotebookFilter.All -> true
                NotebookFilter.Favorite -> word.isFavorite
                NotebookFilter.Technology -> "Technology" in word.tags
                NotebookFilter.Travel -> "Travel" in word.tags
            }
            matchesSearch && matchesFilter
        }
        val content = when {
            allWords.isEmpty() -> NotebookContentState.Empty
            words.isEmpty() -> NotebookContentState.NoSearchResults
            else -> NotebookContentState.Content(words)
        }
        _uiState.update { it.copy(content = content) }
    }
}
