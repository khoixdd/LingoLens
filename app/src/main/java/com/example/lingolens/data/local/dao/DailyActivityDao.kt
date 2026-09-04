package com.example.lingolens.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.lingolens.data.local.entity.DailyWordActivityEntity
import kotlinx.coroutines.flow.Flow

data class DailyActivityCountRow(
    val epochDay: Long,
    val uniqueWords: Int,
)

@Dao
interface DailyActivityDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(activity: DailyWordActivityEntity): Long

    @Query("SELECT COUNT(*) FROM daily_word_activity WHERE userId = :userId AND epochDay = :epochDay")
    fun observeUniqueWordCount(userId: String, epochDay: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM daily_word_activity WHERE userId = :userId AND epochDay = :epochDay")
    suspend fun getUniqueWordCount(userId: String, epochDay: Long): Int

    @Query(
        "SELECT epochDay, COUNT(*) AS uniqueWords FROM daily_word_activity " +
            "WHERE userId = :userId AND epochDay BETWEEN :startEpochDay AND :endEpochDay " +
            "GROUP BY epochDay ORDER BY epochDay ASC",
    )
    fun observeCountsBetween(
        userId: String,
        startEpochDay: Long,
        endEpochDay: Long,
    ): Flow<List<DailyActivityCountRow>>
}
