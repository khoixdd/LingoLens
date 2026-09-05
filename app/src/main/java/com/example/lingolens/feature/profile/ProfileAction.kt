package com.example.lingolens.feature.profile

sealed interface ProfileAction {
    data object EditProfile : ProfileAction
    data object OpenMyWords : ProfileAction
    data object OpenAchievements : ProfileAction
    data object OpenStatistics : ProfileAction
    data object OpenNotifications : ProfileAction
    data object OpenPrivacy : ProfileAction
    data object OpenTranslator : ProfileAction
    data object Logout : ProfileAction
}
