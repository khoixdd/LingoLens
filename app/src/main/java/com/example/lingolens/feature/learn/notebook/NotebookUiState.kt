package com.example.lingolens.feature.learn.notebook

import com.example.lingolens.domain.model.Vocabulary

enum class NotebookFilter(val label: String) {
    All("All"),
    Favorite("Favorite"),
    Technology("Technology"),
    Travel("Travel"),
}

sealed interface NotebookContentState {
    data object Loading : NotebookContentState
    data object Empty : NotebookContentState
    data object NoSearchResults : NotebookContentState
    data class Content(val words: List<Vocabulary>) : NotebookContentState
}

data class NotebookUiState(
    val searchQuery: String = "",
    val selectedFilter: NotebookFilter = NotebookFilter.All,
    val showAddDialog: Boolean = false,
    val content: NotebookContentState = NotebookContentState.Loading,
)
