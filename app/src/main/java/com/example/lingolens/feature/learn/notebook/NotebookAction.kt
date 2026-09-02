package com.example.lingolens.feature.learn.notebook

sealed interface NotebookAction {
    data class SearchChanged(val query: String) : NotebookAction
    data class FilterSelected(val filter: NotebookFilter) : NotebookAction
    data class SortSelected(val sort: NotebookSortOption) : NotebookAction
    data class FavoriteToggled(val wordId: String) : NotebookAction
    data class WordSelected(val wordId: String) : NotebookAction
    data class DeleteWord(val wordId: String) : NotebookAction
    data class ShowAddDialog(val show: Boolean) : NotebookAction
    data class PlayPronunciation(val text: String) : NotebookAction
    data class AddWord(
        val word: String,
        val meaning: String,
        val pronunciation: String = "",
        val partOfSpeech: String = "",
        val example: String = "",
        val tag: String = "",
    ) : NotebookAction
    data object Back : NotebookAction
}
