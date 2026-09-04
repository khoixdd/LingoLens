package com.example.lingolens.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.lingolens.data.local.converter.VocabularyConverters
import com.example.lingolens.data.local.dao.VocabularyDao
import com.example.lingolens.data.local.entity.VocabularyEntity

@Database(
    entities = [VocabularyEntity::class],
    version = 2,
    exportSchema = false,
)
@TypeConverters(VocabularyConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun vocabularyDao(): VocabularyDao
}
