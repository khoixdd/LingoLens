package com.example.lingolens.data.repository

import com.example.lingolens.data.local.dao.VocabularyDao
import com.example.lingolens.data.mapper.toDomain
import com.example.lingolens.data.mapper.toEntity
import com.example.lingolens.domain.model.MasteryLevel
import com.example.lingolens.domain.model.Vocabulary
import com.example.lingolens.domain.repository.VocabularyRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class VocabularyRepositoryImpl @Inject constructor(
    private val dao: VocabularyDao,
) : VocabularyRepository {

    override fun getAllVocabulary(): Flow<List<Vocabulary>> {
        return dao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getVocabularyById(id: String): Flow<Vocabulary?> {
        return dao.observeById(id).map { entity ->
            entity?.toDomain()
        }
    }

    override suspend fun addVocabulary(vocabulary: Vocabulary) {
        dao.insert(vocabulary.toEntity())
    }

    override suspend fun updateVocabulary(vocabulary: Vocabulary) {
        dao.update(vocabulary.toEntity())
    }

    override suspend fun toggleFavorite(id: String) {
        val current = dao.getById(id)
        if (current != null) {
            dao.updateFavorite(id, !current.isFavorite)
        }
    }

    override suspend fun deleteVocabulary(id: String) {
        dao.deleteById(id)
    }

    override suspend fun seedSampleDataIfEmpty() {
        if (dao.getCount() == 0) {
            val sampleItems = listOf(
                Vocabulary(
                    id = "ubiquitous",
                    word = "ubiquitous",
                    meaning = "phổ biến, có mặt ở khắp mọi nơi",
                    pronunciation = "/juːˈbɪkwɪtəs/",
                    partOfSpeech = "adjective",
                    example = "Smartphones have become ubiquitous in modern life.",
                    tags = listOf("Technology"),
                    isFavorite = true,
                    masteryLevel = MasteryLevel.Learning,
                ),
                Vocabulary(
                    id = "artificial",
                    word = "artificial",
                    meaning = "nhân tạo",
                    pronunciation = "/ˌɑːrtɪˈfɪʃəl/",
                    partOfSpeech = "adjective",
                    example = "Artificial intelligence is changing the world.",
                    tags = listOf("Technology"),
                    isFavorite = false,
                    masteryLevel = MasteryLevel.Familiar,
                ),
                Vocabulary(
                    id = "intelligence",
                    word = "intelligence",
                    meaning = "trí thông minh",
                    pronunciation = "/ɪnˈtelɪdʒəns/",
                    partOfSpeech = "noun",
                    example = "Human intelligence allows abstract reasoning.",
                    tags = listOf("Technology"),
                    isFavorite = true,
                    masteryLevel = MasteryLevel.Mastered,
                ),
                Vocabulary(
                    id = "transformative",
                    word = "transformative",
                    meaning = "có tính biến đổi mạnh",
                    pronunciation = "/trænsˈfɔːrmətɪv/",
                    partOfSpeech = "adjective",
                    example = "Travel can be a transformative experience.",
                    tags = listOf("Travel"),
                    isFavorite = false,
                    masteryLevel = MasteryLevel.New,
                ),
            )
            dao.insertAll(sampleItems.map { it.toEntity() })
        }
    }
}
