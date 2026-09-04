package com.example.lingolens.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.lingolens.data.local.AppDatabase
import com.example.lingolens.data.local.MIGRATION_1_2
import com.example.lingolens.data.local.MIGRATION_2_3
import com.example.lingolens.data.local.dao.DailyActivityDao
import com.example.lingolens.data.local.dao.NotificationSettingsDao
import com.example.lingolens.data.local.dao.VocabularyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase {
        lateinit var database: AppDatabase
        database = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "lingolens.db",
        ).addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Executors.newSingleThreadExecutor().execute {
                    database.vocabularyDao()
                    // Pre-population will happen via initial check or repository seed if empty
                }
            }
        })
        .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
        .build()
        return database
    }

    @Provides
    fun provideVocabularyDao(database: AppDatabase): VocabularyDao {
        return database.vocabularyDao()
    }

    @Provides
    fun provideDailyActivityDao(database: AppDatabase): DailyActivityDao = database.dailyActivityDao()

    @Provides
    fun provideNotificationSettingsDao(database: AppDatabase): NotificationSettingsDao =
        database.notificationSettingsDao()
}
