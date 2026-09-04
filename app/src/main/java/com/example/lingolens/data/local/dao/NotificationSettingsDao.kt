package com.example.lingolens.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.lingolens.data.local.entity.NotificationSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationSettingsDao {
    @Query("SELECT * FROM notification_settings WHERE userId = :userId")
    fun observe(userId: String): Flow<NotificationSettingsEntity?>

    @Query("SELECT * FROM notification_settings WHERE userId = :userId")
    suspend fun get(userId: String): NotificationSettingsEntity?

    @Upsert
    suspend fun upsert(settings: NotificationSettingsEntity)
}

