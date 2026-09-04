package com.example.lingolens.domain.repository

import com.example.lingolens.domain.model.DailyActivityCount
import kotlinx.coroutines.flow.Flow

interface DailyActivityRepository {
    fun observeUniqueWords(epochDay: Long): Flow<Int>
    fun observeCounts(startEpochDay: Long, endEpochDay: Long): Flow<List<DailyActivityCount>>
    suspend fun recordUniqueWord(epochDay: Long, vocabularyId: String): Boolean
    suspend fun uniqueWords(epochDay: Long): Int
}

