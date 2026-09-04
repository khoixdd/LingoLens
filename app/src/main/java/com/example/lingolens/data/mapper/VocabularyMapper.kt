package com.example.lingolens.data.mapper

import com.example.lingolens.data.local.entity.VocabularyEntity
import com.example.lingolens.domain.model.MasteryLevel
import com.example.lingolens.domain.model.Vocabulary

fun VocabularyEntity.toDomain(): Vocabulary {
    return Vocabulary(
        id = id,
        word = word,
        meaning = meaning,
        pronunciation = pronunciation,
        partOfSpeech = partOfSpeech,
        example = example,
        tags = tags,
        isFavorite = isFavorite,
        masteryLevel = try {
            MasteryLevel.valueOf(masteryLevel)
        } catch (_: Exception) {
            MasteryLevel.New
        },
        createdAt = createdAt,
        lastReviewedAt = lastReviewedAt,
        nextReviewAt = nextReviewAt,
        correctCount = correctCount,
        wrongCount = wrongCount,
        source = source,
    )
}

fun Vocabulary.toEntity(userId: String): VocabularyEntity {
    return VocabularyEntity(
        userId = userId,
        id = id,
        word = word,
        meaning = meaning,
        pronunciation = pronunciation,
        partOfSpeech = partOfSpeech,
        example = example,
        tags = tags,
        isFavorite = isFavorite,
        masteryLevel = masteryLevel.name,
        createdAt = createdAt,
        lastReviewedAt = lastReviewedAt,
        nextReviewAt = nextReviewAt,
        correctCount = correctCount,
        wrongCount = wrongCount,
        source = source,
    )
}
