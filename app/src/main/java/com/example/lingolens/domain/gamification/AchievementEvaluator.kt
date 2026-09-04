package com.example.lingolens.domain.gamification

data class AchievementSnapshot(
    val dailyGoalCompleted: Boolean,
    val streak: Int,
    val xp: Int,
    val level: Int,
    val vocabularyCount: Int,
)

object AchievementEvaluator {
    fun newlyUnlocked(
        snapshot: AchievementSnapshot,
        alreadyUnlocked: Set<String>,
    ): Set<String> = AchievementDefinitions.all
        .asSequence()
        .filter { definition ->
            when (definition.metric) {
                AchievementMetric.DailyGoal -> snapshot.dailyGoalCompleted
                AchievementMetric.Streak -> snapshot.streak >= definition.threshold
                AchievementMetric.Xp -> snapshot.xp >= definition.threshold
                AchievementMetric.Level -> snapshot.level >= definition.threshold
                AchievementMetric.Vocabulary -> snapshot.vocabularyCount >= definition.threshold
            }
        }
        .map { it.id }
        .filterNot(alreadyUnlocked::contains)
        .toSet()
}

