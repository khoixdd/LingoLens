package com.example.lingolens.di

import com.example.lingolens.data.repository.VocabularyRepositoryImpl
import com.example.lingolens.domain.repository.VocabularyRepository
import com.example.lingolens.data.repository.WordFetcherImpl
import com.example.lingolens.data.repository.WordFetcher
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
}
