package com.example.lingolens.domain.gamification

object LevelCalculator {
    const val XP_PER_LEVEL = 200

    fun levelForXp(xp: Int): Int = xp.coerceAtLeast(0) / XP_PER_LEVEL + 1

    fun xpForLevel(level: Int): Int = (level.coerceAtLeast(1) - 1) * XP_PER_LEVEL

    fun xpProgressInLevel(xp: Int): Int = xp.coerceAtLeast(0) % XP_PER_LEVEL

    fun titleForLevel(level: Int): String = when (level.coerceAtLeast(1)) {
        in 1..3 -> "Explorer"
        in 4..7 -> "Polyglot"
        else -> "Master"
    }
}

