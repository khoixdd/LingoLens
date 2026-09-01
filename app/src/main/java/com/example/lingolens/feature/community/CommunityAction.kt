package com.example.lingolens.feature.community

sealed interface CommunityAction {
    data class SelectPeriod(val period: LeaderboardPeriod) : CommunityAction
}
