package com.example.lingolens.domain.model

/** A committed transaction's new unlock, never a replay of the profile's stored IDs. */
data class AchievementUnlock(val userId: String, val achievementId: String)
