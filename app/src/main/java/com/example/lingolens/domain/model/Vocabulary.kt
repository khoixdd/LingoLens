package com.example.lingolens.domain.model

enum class MasteryLevel(val label: String) {
    New("New"),
    Learning("Learning"),
    Familiar("Familiar"),
    Mastered("Mastered"),
}

data class Vocabulary(
    val id: String,
    val word: String,
    val meaning: String,
    val pronunciation: String = "",
    val partOfSpeech: String = "",
    val example: String = "",
    val tags: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val masteryLevel: MasteryLevel = MasteryLevel.New,
    val createdAt: Long = System.currentTimeMillis(),
    val lastReviewedAt: Long? = null,
    val nextReviewAt: Long? = null,
    val correctCount: Int = 0,
    val wrongCount: Int = 0,
    val source: String = "manual",
)
