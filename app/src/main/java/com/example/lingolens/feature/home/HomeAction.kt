package com.example.lingolens.feature.home

sealed interface HomeAction {
    data object OpenNotebook : HomeAction
    data object OpenQuiz : HomeAction
    data object OpenStatistics : HomeAction
    data object OpenAchievements : HomeAction
    data object OpenLearn : HomeAction
    data object OpenReview : HomeAction
    data object OpenNotifications : HomeAction
}
