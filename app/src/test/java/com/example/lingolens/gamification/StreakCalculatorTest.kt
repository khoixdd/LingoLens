package com.example.lingolens.gamification

import com.example.lingolens.domain.gamification.StreakCalculator
import java.time.Instant
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Test

class StreakCalculatorTest {
    @Test fun firstActivityStartsAtOne() = assertEquals(1, StreakCalculator.updatedStreak(0, null, 100))
    @Test fun sameDayDoesNotIncrement() = assertEquals(4, StreakCalculator.updatedStreak(4, 100, 100))
    @Test fun nextDayIncrements() = assertEquals(5, StreakCalculator.updatedStreak(4, 100, 101))
    @Test fun skippedDayResets() = assertEquals(1, StreakCalculator.updatedStreak(4, 100, 102))

    @Test
    fun timezoneBoundaryUsesLocalCalendarDates() {
        val instant = Instant.parse("2026-01-01T00:30:00Z")
        val newYorkDay = instant.atZone(ZoneId.of("America/New_York")).toLocalDate().toEpochDay()
        val tokyoDay = instant.atZone(ZoneId.of("Asia/Tokyo")).toLocalDate().toEpochDay()
        assertEquals(1, tokyoDay - newYorkDay)
        assertEquals(3, StreakCalculator.updatedStreak(2, newYorkDay, tokyoDay))
    }

    @Test fun staleDisplayIsZero() = assertEquals(0, StreakCalculator.effectiveStreak(7, 100, 102))
}

