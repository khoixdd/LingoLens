package com.example.lingolens.feature.learn.notebook

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lingolens.core.common.TextToSpeechHelper
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
import kotlinx.coroutines.launch

@HiltViewModel
class NotebookViewModel @Inject constructor(
    val repository: VocabularyRepository,
    val ttsHelper: TextToSpeechHelper,
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val selectedFilter = MutableStateFlow(NotebookFilter.All)
    private val selectedSort = MutableStateFlow(NotebookSortOption.Newest)
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
        selectedSort,
        showAddDialog,
    ) { allWords, query, filter, sort, showDialog ->
        val filtered = allWords.filter { item ->
            val matchesSearch = query.isBlank() ||
                item.word.contains(query, ignoreCase = true) ||
                item.meaning.contains(query, ignoreCase = true) ||
                item.tags.any { it.contains(query, ignoreCase = true) }
            val matchesFilter = when (filter) {
                NotebookFilter.All -> true
                NotebookFilter.Favorite -> item.isFavorite
                NotebookFilter.Technology -> "Technology" in item.tags
                NotebookFilter.Travel -> "Travel" in item.tags
            }
            matchesSearch && matchesFilter
        }

        val sorted = when (sort) {
            NotebookSortOption.Newest -> filtered.sortedByDescending { it.createdAt }
            NotebookSortOption.Oldest -> filtered.sortedBy { it.createdAt }
            NotebookSortOption.Alphabetical_AZ -> filtered.sortedBy { it.word.lowercase() }
            NotebookSortOption.Alphabetical_ZA -> filtered.sortedByDescending { it.word.lowercase() }
            NotebookSortOption.Mastery -> filtered.sortedBy { it.masteryLevel.ordinal }
        }

        val contentState = when {
            allWords.isEmpty() -> NotebookContentState.Empty
            sorted.isEmpty() -> NotebookContentState.NoSearchResults
            else -> NotebookContentState.Content(sorted)
        }

        NotebookUiState(
            searchQuery = query,
            selectedFilter = filter,
            selectedSort = sort,
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
            is NotebookAction.SortSelected -> selectedSort.value = action.sort
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
            is NotebookAction.PlayPronunciation -> {
                ttsHelper.speak(action.text)
            }
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

    override fun onCleared() {
        super.onCleared()
        ttsHelper.shutdown()
    }
}
