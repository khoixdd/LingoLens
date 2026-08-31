package com.example.lingolens.domain.repository

import com.example.lingolens.domain.model.Vocabulary
import kotlinx.coroutines.flow.Flow

interface VocabularyRepository {
    fun getAllVocabulary(): Flow<List<Vocabulary>>
    fun getVocabularyById(id: String): Flow<Vocabulary?>
    suspend fun addVocabulary(vocabulary: Vocabulary)
    suspend fun updateVocabulary(vocabulary: Vocabulary)
    suspend fun toggleFavorite(id: String)
    suspend fun deleteVocabulary(id: String)
    suspend fun seedSampleDataIfEmpty()
}
