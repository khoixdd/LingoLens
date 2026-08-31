package com.example.lingolens.feature.learn.detail

import com.example.lingolens.domain.model.MasteryLevel
import com.example.lingolens.domain.model.Vocabulary

data class VocabularyDetailUiState(
    val id: String = "",
    val word: String = "",
    val pronunciation: String = "",
    val partOfSpeech: String = "",
    val meaning: String = "",
    val example: String = "",
    val tags: List<String> = emptyList(),
    val mastery: MasteryLevel = MasteryLevel.New,
    val isFavorite: Boolean = false,
    val isLoading: Boolean = true,
    val showEditDialog: Boolean = false,
    val isDeleted: Boolean = false,
)
