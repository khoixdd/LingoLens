package com.example.lingolens.domain.gamification

object DailyActivityCalculator {
    fun uniqueWordCount(vocabularyIds: Iterable<String>): Int =
        vocabularyIds.filter { it.isNotBlank() }.toSet().size
}

