package com.example.lingolens.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class AchievementVisual(val icon: ImageVector, val accent: Color)

fun achievementVisual(id: String): AchievementVisual = when (id) {
    "goal_getter" -> AchievementVisual(Icons.Outlined.MyLocation, Color(0xFF087326))
    "on_fire" -> AchievementVisual(Icons.Outlined.LocalFireDepartment, Color(0xFFC0582D))
    "week_warrior" -> AchievementVisual(Icons.Outlined.EventAvailable, Color(0xFF287F7E))
    "rising_star" -> AchievementVisual(Icons.Outlined.Star, Color(0xFF9B7200))
    "xp_hunter" -> AchievementVisual(Icons.Outlined.Bolt, Color(0xFF986500))
    "leveling_up" -> AchievementVisual(Icons.Outlined.WorkspacePremium, Color(0xFF7856AD))
    "word_collector" -> AchievementVisual(Icons.Outlined.Bookmark, Color(0xFF3576A4))
    "vocabulary_builder" -> AchievementVisual(Icons.Outlined.LocalLibrary, Color(0xFF358441))
    else -> AchievementVisual(Icons.Outlined.EmojiEvents, Color(0xFF087326))
}
