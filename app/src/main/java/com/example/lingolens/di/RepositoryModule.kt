package com.example.lingolens.di

import com.example.lingolens.data.repository.VocabularyRepositoryImpl
import com.example.lingolens.data.repository.DailyActivityRepositoryImpl
import com.example.lingolens.data.repository.NotificationSettingsRepositoryImpl
import com.example.lingolens.data.repository.LearningProgressRepositoryImpl
import com.example.lingolens.domain.repository.VocabularyRepository
import com.example.lingolens.data.repository.WordFetcherImpl
import com.example.lingolens.data.repository.WordFetcher
import com.example.lingolens.domain.repository.DailyActivityRepository
import com.example.lingolens.domain.repository.NotificationSettingsRepository
import com.example.lingolens.domain.repository.LearningProgressRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindVocabularyRepository(
        impl: VocabularyRepositoryImpl,
    ): VocabularyRepository

    @Binds
    @Singleton
    abstract fun bindWordFetcher(
        impl: WordFetcherImpl
    ): WordFetcher
    abstract fun bindDailyActivityRepository(
        impl: DailyActivityRepositoryImpl,
    ): DailyActivityRepository

    @Binds
    @Singleton
    abstract fun bindNotificationSettingsRepository(
        impl: NotificationSettingsRepositoryImpl,
    ): NotificationSettingsRepository

    @Binds
    @Singleton
    abstract fun bindLearningProgressRepository(
        impl: LearningProgressRepositoryImpl,
    ): LearningProgressRepository
}
