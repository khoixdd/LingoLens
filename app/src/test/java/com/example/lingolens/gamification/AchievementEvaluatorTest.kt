package com.example.lingolens.gamification

import com.example.lingolens.domain.gamification.AchievementEvaluator
import com.example.lingolens.domain.gamification.AchievementSnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AchievementEvaluatorTest {
    @Test fun freshProfileUnlocksNothing() {
        assertTrue(AchievementEvaluator.newlyUnlocked(AchievementSnapshot(false, 0, 0, 1, 0), emptySet()).isEmpty())
    }

    @Test fun thresholdsUnlockAndAlreadyUnlockedDoesNotDuplicate() {
        val unlocked = AchievementEvaluator.newlyUnlocked(
            AchievementSnapshot(true, 7, 500, 5, 50),
            setOf("rising_star"),
        )
        assertTrue("rising_star" !in unlocked)
        assertTrue(setOf("goal_getter", "on_fire", "week_warrior", "xp_hunter", "leveling_up", "word_collector", "vocabulary_builder").all { it in unlocked })
    }

    @Test fun individualMetricsUnlockExpectedAchievements() {
        assertEquals(setOf("goal_getter"), AchievementEvaluator.newlyUnlocked(AchievementSnapshot(true, 0, 0, 1, 0), emptySet()))
        assertTrue("on_fire" in AchievementEvaluator.newlyUnlocked(AchievementSnapshot(false, 3, 0, 1, 0), emptySet()))
        assertTrue("rising_star" in AchievementEvaluator.newlyUnlocked(AchievementSnapshot(false, 0, 100, 1, 0), emptySet()))
        assertTrue("leveling_up" in AchievementEvaluator.newlyUnlocked(AchievementSnapshot(false, 0, 0, 5, 0), emptySet()))
        assertTrue("word_collector" in AchievementEvaluator.newlyUnlocked(AchievementSnapshot(false, 0, 0, 1, 10), emptySet()))
    }
}

