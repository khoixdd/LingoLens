package com.example.lingolens.feature.learn.review

data class ReviewUiState(
    val currentIndex: Int = 0,
    val total: Int = 14,
    val word: String = "ubiquitous",
    val pronunciation: String = "/juːˈbɪkwɪtəs/",
    val meaning: String = "phổ biến, có mặt ở khắp mọi nơi",
    val example: String = "Smartphones have become ubiquitous in modern life.",
    val isRevealed: Boolean = false,
)
