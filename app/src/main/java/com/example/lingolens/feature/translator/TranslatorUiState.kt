package com.example.lingolens.feature.translator

import com.example.lingolens.core.mlkit.TranslationModelState
import com.example.lingolens.core.mlkit.TranslationPair

data class TranslatorUiState(
    val sourceText: String = "",
    val direction: TranslationPair = TranslationPair.EnglishToVietnamese,
    val result: String? = null,
    val isTranslating: Boolean = false,
    val errorMessage: String? = null,
    val enViModelState: TranslationModelState = TranslationModelState.NotDownloaded,
    val viEnModelState: TranslationModelState = TranslationModelState.NotDownloaded,
)