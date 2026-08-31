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
    @Query("SELECT * FROM vocabulary ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<VocabularyEntity>>

    @Query("SELECT * FROM vocabulary WHERE id = :id")
    fun observeById(id: String): Flow<VocabularyEntity?>

    @Query("SELECT * FROM vocabulary WHERE id = :id")
    suspend fun getById(id: String): VocabularyEntity?

    @Query("SELECT COUNT(*) FROM vocabulary")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: VocabularyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<VocabularyEntity>)

    @Update
    suspend fun update(item: VocabularyEntity): Int

    @Query("UPDATE vocabulary SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: String, isFavorite: Boolean): Int

    @Query("DELETE FROM vocabulary WHERE id = :id")
    suspend fun deleteById(id: String): Int
}
