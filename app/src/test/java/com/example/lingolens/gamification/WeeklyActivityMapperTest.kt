package com.example.lingolens.gamification

import com.example.lingolens.domain.gamification.WeeklyActivityMapper
import com.example.lingolens.domain.model.DailyActivityCount
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test

class WeeklyActivityMapperTest {
    private val today = 1_000L

    @Test fun emptyHistoryProducesSevenZeroDays() {
        val result = WeeklyActivityMapper.map(emptyList(), today, Locale.US)
        assertEquals(7, result.size)
        assertEquals(List(7) { 0 }, result.map { it.uniqueWords })
    }

    @Test fun sparseHistoryFillsGaps() {
        val result = WeeklyActivityMapper.map(listOf(DailyActivityCount(today - 3, 4)), today, Locale.US)
        assertEquals(4, result[3].uniqueWords)
        assertEquals(0, result[2].uniqueWords)
    }

    @Test fun sevenConsecutiveDaysArePreservedInOrder() {
        val history = (0L..6L).map { DailyActivityCount(today - 6 + it, it.toInt() + 1) }
        val result = WeeklyActivityMapper.map(history, today, Locale.US)
        assertEquals((1..7).toList(), result.map { it.uniqueWords })
        assertEquals((today - 6..today).toList(), result.map { it.epochDay })
    }

    @Test fun historyOlderThanSevenDaysIsIgnored() {
        val result = WeeklyActivityMapper.map(
            listOf(DailyActivityCount(today - 9, 9), DailyActivityCount(today, 2)),
            today,
            Locale.US,
        )
        assertEquals(2, result.last().uniqueWords)
        assertEquals(0, result.first().uniqueWords)
    }
}

