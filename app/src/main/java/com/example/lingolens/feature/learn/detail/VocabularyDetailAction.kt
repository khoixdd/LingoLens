package com.example.lingolens.feature.learn.detail

sealed interface VocabularyDetailAction {
    data object Back : VocabularyDetailAction
    data object ToggleFavorite : VocabularyDetailAction
    data object PlayPronunciation : VocabularyDetailAction
    data object Edit : VocabularyDetailAction
    data object Delete : VocabularyDetailAction
    data class ShowEditDialog(val show: Boolean) : VocabularyDetailAction
    data class SaveEdit(
        val meaning: String,
        val example: String,
        val pronunciation: String,
        val partOfSpeech: String,
    ) : VocabularyDetailAction
}
