package com.example.lingolens.feature.learn

sealed interface LearnAction {
    data object OpenNotebook : LearnAction
    data object StartReview : LearnAction
    data object StartQuiz : LearnAction
    data object OpenStatistics : LearnAction
}
