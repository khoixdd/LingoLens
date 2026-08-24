package com.example.lingolens.feature.learn.notebook

sealed interface NotebookAction {
    data class SearchChanged(val query: String) : NotebookAction
    data class FilterSelected(val filter: NotebookFilter) : NotebookAction
    data class FavoriteToggled(val wordId: String) : NotebookAction
    data class WordSelected(val wordId: String) : NotebookAction
    data object Back : NotebookAction
}
