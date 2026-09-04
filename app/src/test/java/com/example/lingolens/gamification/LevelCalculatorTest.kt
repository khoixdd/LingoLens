package com.example.lingolens.gamification

import com.example.lingolens.domain.gamification.LevelCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class LevelCalculatorTest {
    @Test fun zeroXpIsLevelOne() = assertEquals(1, LevelCalculator.levelForXp(0))
    @Test fun boundary199And200() {
        assertEquals(1, LevelCalculator.levelForXp(199))
        assertEquals(2, LevelCalculator.levelForXp(200))
    }
    @Test fun multipleLevels() = assertEquals(6, LevelCalculator.levelForXp(1_000))
    @Test fun xpProgressUsesCurrentLevel() {
        assertEquals(199, LevelCalculator.xpProgressInLevel(399))
        assertEquals(0, LevelCalculator.xpProgressInLevel(400))
        assertEquals(400, LevelCalculator.xpForLevel(3))
    }
}

