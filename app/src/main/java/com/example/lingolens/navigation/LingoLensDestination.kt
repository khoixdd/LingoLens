package com.example.lingolens.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface LingoLensDestination : NavKey

@Serializable data object Home : LingoLensDestination
@Serializable data object Scan : LingoLensDestination
@Serializable data object Learn : LingoLensDestination
@Serializable data object Community : LingoLensDestination
@Serializable data object Notebook : LingoLensDestination
@Serializable data class VocabularyDetail(val wordId: String) : LingoLensDestination
@Serializable data object Review : LingoLensDestination
@Serializable data object Quiz : LingoLensDestination
@Serializable data class QuizResult(val score: Int, val total: Int) : LingoLensDestination
@Serializable data object Profile : LingoLensDestination
@Serializable data object NotificationSettings : LingoLensDestination
@Serializable data object PrivacySettings : LingoLensDestination
@Serializable data object Welcome : LingoLensDestination
