package com.example.lingolens.feature.learn.review

data class ReviewUiState(
    val isLoading: Boolean = true,
    val isCompleted: Boolean = false,
    val isEmpty: Boolean = false,
    val currentIndex: Int = 0,
    val total: Int = 0,
    val word: String = "",
    val pronunciation: String = "",
    val meaning: String = "",
    val example: String = "",
    val isRevealed: Boolean = false,
)
