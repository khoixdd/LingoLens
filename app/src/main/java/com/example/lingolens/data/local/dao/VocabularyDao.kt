package com.example.lingolens.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.lingolens.data.local.entity.VocabularyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabularyDao {
    @Query("SELECT * FROM vocabulary WHERE userId = :userId ORDER BY createdAt DESC")
    fun observeAll(userId: String): Flow<List<VocabularyEntity>>

    @Query("SELECT * FROM vocabulary WHERE userId = :userId AND id = :id")
    fun observeById(userId: String, id: String): Flow<VocabularyEntity?>

    @Query("SELECT * FROM vocabulary WHERE userId = :userId AND id = :id")
    suspend fun getById(userId: String, id: String): VocabularyEntity?

    @Query("SELECT * FROM vocabulary WHERE userId = :userId AND LOWER(word) = LOWER(:word) LIMIT 1")
    suspend fun getByWordText(userId: String, word: String): VocabularyEntity?

    @Query("SELECT COUNT(*) FROM vocabulary WHERE userId = :userId")
    suspend fun getCount(userId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: VocabularyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<VocabularyEntity>)

    @Update
    suspend fun update(item: VocabularyEntity): Int

    @Query("UPDATE vocabulary SET isFavorite = :isFavorite WHERE userId = :userId AND id = :id")
    suspend fun updateFavorite(userId: String, id: String, isFavorite: Boolean): Int

    @Query("DELETE FROM vocabulary WHERE userId = :userId AND id = :id")
    suspend fun deleteById(userId: String, id: String): Int
}
