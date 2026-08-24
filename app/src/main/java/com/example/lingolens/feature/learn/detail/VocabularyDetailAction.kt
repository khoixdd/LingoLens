package com.example.lingolens.feature.learn.detail

sealed interface VocabularyDetailAction { data object Back : VocabularyDetailAction; data object ToggleFavorite : VocabularyDetailAction; data object PlayPronunciation : VocabularyDetailAction; data object Edit : VocabularyDetailAction }
