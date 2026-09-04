package com.example.lingolens.domain.gamification

enum class AchievementMetric { DailyGoal, Streak, Xp, Level, Vocabulary }

data class AchievementDefinition(
    val id: String,
    val name: String,
    val description: String,
    val metric: AchievementMetric,
    val threshold: Int,
)

object AchievementDefinitions {
    val all = listOf(
        AchievementDefinition("goal_getter", "Goal Getter", "Complete your daily goal once", AchievementMetric.DailyGoal, 1),
        AchievementDefinition("on_fire", "On Fire", "Reach a 3-day streak", AchievementMetric.Streak, 3),
        AchievementDefinition("week_warrior", "Week Warrior", "Reach a 7-day streak", AchievementMetric.Streak, 7),
        AchievementDefinition("rising_star", "Rising Star", "Earn 100 XP", AchievementMetric.Xp, 100),
        AchievementDefinition("xp_hunter", "XP Hunter", "Earn 500 XP", AchievementMetric.Xp, 500),
        AchievementDefinition("leveling_up", "Leveling Up", "Reach level 5", AchievementMetric.Level, 5),
        AchievementDefinition("word_collector", "Word Collector", "Save 10 vocabulary words", AchievementMetric.Vocabulary, 10),
        AchievementDefinition("vocabulary_builder", "Vocabulary Builder", "Save 50 vocabulary words", AchievementMetric.Vocabulary, 50),
    )
}

