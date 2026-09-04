package com.example.lingolens.domain.gamification

object StreakCalculator {
    fun updatedStreak(
        storedStreak: Int,
        lastActivityEpochDay: Long?,
        activityEpochDay: Long,
    ): Int = when {
        lastActivityEpochDay == null -> 1
        activityEpochDay <= lastActivityEpochDay -> storedStreak.coerceAtLeast(1)
        activityEpochDay == lastActivityEpochDay + 1 -> storedStreak.coerceAtLeast(0) + 1
        else -> 1
    }

    fun effectiveStreak(
        storedStreak: Int,
        lastActivityEpochDay: Long?,
        todayEpochDay: Long,
    ): Int = when {
        lastActivityEpochDay == null -> 0
        lastActivityEpochDay >= todayEpochDay - 1 -> storedStreak.coerceAtLeast(0)
        else -> 0
    }
}

