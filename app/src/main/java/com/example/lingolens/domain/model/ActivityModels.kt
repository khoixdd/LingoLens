package com.example.lingolens.domain.model

data class DailyActivityCount(
    val epochDay: Long,
    val uniqueWords: Int,
)

data class WeeklyActivityDay(
    val epochDay: Long,
    val dayLabel: String,
    val uniqueWords: Int,
)

const val DEFAULT_DAILY_GOAL = 10

