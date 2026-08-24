package com.example.lingolens.feature.learn.detail

import com.example.lingolens.feature.learn.notebook.MasteryLevel

data class VocabularyDetailUiState(
    val id: String = "ubiquitous",
    val word: String = "ubiquitous",
    val pronunciation: String = "/juːˈbɪkwɪtəs/",
    val partOfSpeech: String = "adjective",
    val meaning: String = "phổ biến, có mặt ở khắp mọi nơi",
    val example: String = "Smartphones have become ubiquitous in modern life.",
    val tags: List<String> = listOf("Technology", "Common"),
    val mastery: MasteryLevel = MasteryLevel.Learning,
    val isFavorite: Boolean = true,
)
