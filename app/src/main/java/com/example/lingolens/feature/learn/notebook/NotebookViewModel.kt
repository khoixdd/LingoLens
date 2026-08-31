package com.example.lingolens.feature.learn.notebook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lingolens.domain.model.Vocabulary
import com.example.lingolens.domain.repository.VocabularyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class NotebookViewModel @Inject constructor(
    private val repository: VocabularyRepository,
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val selectedFilter = MutableStateFlow(NotebookFilter.All)
    private val showAddDialog = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            repository.seedSampleDataIfEmpty()
        }
    }

    val uiState: StateFlow<NotebookUiState> = combine(
        repository.getAllVocabulary(),
        searchQuery,
        selectedFilter,
        showAddDialog,
    ) { allWords, query, filter, showDialog ->
        val filtered = allWords.filter { item ->
            val matchesSearch = query.isBlank() ||
                item.word.contains(query, ignoreCase = true) ||
                item.meaning.contains(query, ignoreCase = true)
            val matchesFilter = when (filter) {
                NotebookFilter.All -> true
                NotebookFilter.Favorite -> item.isFavorite
                NotebookFilter.Technology -> "Technology" in item.tags
                NotebookFilter.Travel -> "Travel" in item.tags
            }
            matchesSearch && matchesFilter
        }

        val contentState = when {
            allWords.isEmpty() -> NotebookContentState.Empty
            filtered.isEmpty() -> NotebookContentState.NoSearchResults
            else -> NotebookContentState.Content(filtered)
        }

        NotebookUiState(
            searchQuery = query,
            selectedFilter = filter,
            showAddDialog = showDialog,
            content = contentState,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NotebookUiState(),
    )

    fun onAction(action: NotebookAction) {
        when (action) {
            is NotebookAction.SearchChanged -> searchQuery.value = action.query
            is NotebookAction.FilterSelected -> selectedFilter.value = action.filter
            is NotebookAction.FavoriteToggled -> {
                viewModelScope.launch {
                    repository.toggleFavorite(action.wordId)
                }
            }
            is NotebookAction.DeleteWord -> {
                viewModelScope.launch {
                    repository.deleteVocabulary(action.wordId)
                }
            }
            is NotebookAction.ShowAddDialog -> showAddDialog.value = action.show
            is NotebookAction.AddWord -> {
                viewModelScope.launch {
                    val newWord = Vocabulary(
                        id = UUID.randomUUID().toString(),
                        word = action.word,
                        meaning = action.meaning,
                        pronunciation = action.pronunciation,
                        partOfSpeech = action.partOfSpeech,
                        example = action.example,
                        tags = if (action.tag.isNotBlank()) listOf(action.tag) else emptyList(),
                    )
                    repository.addVocabulary(newWord)
                    showAddDialog.value = false
                }
            }
            NotebookAction.Back, is NotebookAction.WordSelected -> Unit
        }
    }
}
