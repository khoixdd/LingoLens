package com.example.lingolens.data.repository

import com.example.lingolens.data.local.dao.VocabularyDao
import com.example.lingolens.data.mapper.toDomain
import com.example.lingolens.data.mapper.toEntity
import com.example.lingolens.domain.model.MasteryLevel
import com.example.lingolens.domain.model.Vocabulary
import com.example.lingolens.domain.repository.AuthRepository
import com.example.lingolens.domain.repository.VocabularyRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flowOf

@Singleton
class VocabularyRepositoryImpl @Inject constructor(
    private val dao: VocabularyDao,
    private val authRepository: AuthRepository,
) : VocabularyRepository {

    override fun getAllVocabulary(): Flow<List<Vocabulary>> {
        val userId = currentUserId() ?: return flowOf(emptyList())
        return dao.observeAll(userId)
            .map { entities -> entities.map { it.toDomain() } }
            .catch { emit(emptyList()) }
    }

    override fun getVocabularyById(id: String): Flow<Vocabulary?> {
        val userId = currentUserId() ?: return flowOf(null)
        return dao.observeById(userId, id)
            .map { entity -> entity?.toDomain() }
            .catch { emit(null) }
    }

    override suspend fun getWordByText(word: String): Vocabulary? {
        val userId = currentUserId() ?: return null
        return runCatching { dao.getByWordText(userId, word.trim())?.toDomain() }.getOrNull()
    }

    override suspend fun isWordDuplicate(word: String): Boolean {
        val userId = currentUserId() ?: return false
        return runCatching { dao.getByWordText(userId, word.trim()) != null }.getOrDefault(false)
    }

    override suspend fun addVocabulary(vocabulary: Vocabulary) {
        val userId = currentUserId() ?: return
        runCatching {
            val existing = dao.getByWordText(userId, vocabulary.word.trim())
            if (existing != null) {
                val updatedEntity = vocabulary.copy(
                    id = existing.id,
                    createdAt = existing.createdAt,
                    masteryLevel = try { MasteryLevel.valueOf(existing.masteryLevel) } catch (_: Exception) { vocabulary.masteryLevel },
                    isFavorite = existing.isFavorite || vocabulary.isFavorite,
                ).toEntity(userId)
                dao.update(updatedEntity)
            } else {
                dao.insert(vocabulary.toEntity(userId))
            }
        }
    }

    override suspend fun updateVocabulary(vocabulary: Vocabulary) {
        val userId = currentUserId() ?: return
        runCatching {
            dao.update(vocabulary.toEntity(userId))
        }
    }

    override suspend fun toggleFavorite(id: String) {
        val userId = currentUserId() ?: return
        runCatching {
            val current = dao.getById(userId, id)
            if (current != null) {
                dao.updateFavorite(userId, id, !current.isFavorite)
            }
        }
    }

    override suspend fun deleteVocabulary(id: String) {
        val userId = currentUserId() ?: return
        runCatching {
            dao.deleteById(userId, id)
        }
    }

    override suspend fun seedSampleDataIfEmpty() {
        // New accounts must start with their own vocabulary; demo words are not seeded.
        return
        /*runCatching {
            val userId = currentUserId() ?: return
            if (dao.getCount(userId) == 0) {
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
                dao.insertAll(sampleItems.map { it.toEntity(userId) })
            }
        }*/
    }

    private fun currentUserId(): String? = authRepository.getCurrentUser()?.uid?.takeIf { it.isNotBlank() }
}
