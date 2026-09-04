package com.example.lingolens.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "daily_word_activity",
    primaryKeys = ["userId", "epochDay", "vocabularyId"],
)
data class DailyWordActivityEntity(
    val userId: String,
    val epochDay: Long,
    val vocabularyId: String,
    val recordedAt: Long,
)

