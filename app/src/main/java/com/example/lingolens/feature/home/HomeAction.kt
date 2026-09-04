package com.example.lingolens.feature.home

sealed interface HomeAction {
    data object OpenLearn : HomeAction
    data object OpenReview : HomeAction
    data object OpenNotifications : HomeAction
}
