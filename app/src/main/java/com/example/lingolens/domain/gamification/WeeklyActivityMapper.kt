package com.example.lingolens.domain.gamification

import com.example.lingolens.domain.model.DailyActivityCount
import com.example.lingolens.domain.model.WeeklyActivityDay
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

object WeeklyActivityMapper {
    fun map(
        history: List<DailyActivityCount>,
        todayEpochDay: Long,
        locale: Locale = Locale.getDefault(),
    ): List<WeeklyActivityDay> {
        val counts = history.associate { it.epochDay to it.uniqueWords }
        return (6L downTo 0L).map { daysAgo ->
            val epochDay = todayEpochDay - daysAgo
            WeeklyActivityDay(
                epochDay = epochDay,
                dayLabel = LocalDate.ofEpochDay(epochDay).dayOfWeek
                    .getDisplayName(TextStyle.SHORT, locale),
                uniqueWords = counts[epochDay] ?: 0,
            )
        }
    }
}

