package com.example.lingolens.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vocabulary")
data class VocabularyEntity(
    @PrimaryKey val id: String,
    val word: String,
    val meaning: String,
    val pronunciation: String,
    val partOfSpeech: String,
    val example: String,
    val tags: List<String>,
    val isFavorite: Boolean,
    val masteryLevel: String,
    val createdAt: Long,
    val lastReviewedAt: Long?,
    val nextReviewAt: Long?,
    val correctCount: Int,
    val wrongCount: Int,
    val source: String,
)
