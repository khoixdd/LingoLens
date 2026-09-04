package com.example.lingolens.data.repository

import com.example.lingolens.data.local.dao.DailyActivityDao
import com.example.lingolens.data.local.entity.DailyWordActivityEntity
import com.example.lingolens.domain.model.DailyActivityCount
import com.example.lingolens.domain.repository.AuthRepository
import com.example.lingolens.domain.repository.DailyActivityRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

@Singleton
class DailyActivityRepositoryImpl @Inject constructor(
    private val dao: DailyActivityDao,
    private val authRepository: AuthRepository,
) : DailyActivityRepository {
    override fun observeUniqueWords(epochDay: Long): Flow<Int> {
        val userId = authRepository.getCurrentUser()?.uid ?: return flowOf(0)
        return dao.observeUniqueWordCount(userId, epochDay)
    }

    override fun observeCounts(
        startEpochDay: Long,
        endEpochDay: Long,
    ): Flow<List<DailyActivityCount>> {
        val userId = authRepository.getCurrentUser()?.uid ?: return flowOf(emptyList())
        return dao.observeCountsBetween(userId, startEpochDay, endEpochDay).map { rows ->
            rows.map { DailyActivityCount(it.epochDay, it.uniqueWords) }
        }
    }

    override suspend fun recordUniqueWord(epochDay: Long, vocabularyId: String): Boolean {
        val userId = authRepository.getCurrentUser()?.uid ?: return false
        if (vocabularyId.isBlank()) return false
        return dao.insert(
            DailyWordActivityEntity(
                userId = userId,
                epochDay = epochDay,
                vocabularyId = vocabularyId,
                recordedAt = System.currentTimeMillis(),
            ),
        ) != -1L
    }

    override suspend fun uniqueWords(epochDay: Long): Int {
        val userId = authRepository.getCurrentUser()?.uid ?: return 0
        return dao.getUniqueWordCount(userId, epochDay)
    }
}

