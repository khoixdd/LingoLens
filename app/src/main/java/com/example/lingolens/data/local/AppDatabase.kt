package com.example.lingolens.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.lingolens.data.local.converter.VocabularyConverters
import com.example.lingolens.data.local.dao.DailyActivityDao
import com.example.lingolens.data.local.dao.NotificationSettingsDao
import com.example.lingolens.data.local.dao.VocabularyDao
import com.example.lingolens.data.local.entity.DailyWordActivityEntity
import com.example.lingolens.data.local.entity.NotificationSettingsEntity
import com.example.lingolens.data.local.entity.VocabularyEntity

@Database(
    entities = [
        VocabularyEntity::class,
        DailyWordActivityEntity::class,
        NotificationSettingsEntity::class,
    ],
    version = 3,
    exportSchema = false,
)
@TypeConverters(VocabularyConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vocabularyDao(): VocabularyDao
    abstract fun dailyActivityDao(): DailyActivityDao
    abstract fun notificationSettingsDao(): NotificationSettingsDao
}
