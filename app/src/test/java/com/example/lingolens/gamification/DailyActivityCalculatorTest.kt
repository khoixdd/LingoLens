package com.example.lingolens.gamification

import com.example.lingolens.domain.gamification.DailyActivityCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class DailyActivityCalculatorTest {
    @Test fun sameVocabularyTwiceCountsOnce() =
        assertEquals(1, DailyActivityCalculator.uniqueWordCount(listOf("word-1", "word-1")))

    @Test fun twoVocabularyIdsCountTwice() =
        assertEquals(2, DailyActivityCalculator.uniqueWordCount(listOf("word-1", "word-2")))

    @Test fun sameVocabularyOnNextDayCountsAgainPerDay() {
        val byDay = mapOf(100L to listOf("word-1"), 101L to listOf("word-1"))
        assertEquals(2, byDay.values.sumOf(DailyActivityCalculator::uniqueWordCount))
    }
}

