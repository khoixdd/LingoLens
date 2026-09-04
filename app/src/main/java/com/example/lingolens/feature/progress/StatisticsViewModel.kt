package com.example.lingolens.feature.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lingolens.domain.gamification.LevelCalculator
import com.example.lingolens.domain.gamification.StreakCalculator
import com.example.lingolens.domain.gamification.WeeklyActivityMapper
import com.example.lingolens.domain.model.MasteryLevel
import com.example.lingolens.domain.repository.AuthRepository
import com.example.lingolens.domain.repository.DailyActivityRepository
import com.example.lingolens.domain.repository.UserRepository
import com.example.lingolens.domain.repository.VocabularyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class StatisticsViewModel @Inject constructor(
    authRepository: AuthRepository,
    userRepository: UserRepository,
    vocabularyRepository: VocabularyRepository,
    dailyActivityRepository: DailyActivityRepository,
) : ViewModel() {
    private val today = LocalDate.now().toEpochDay()

    val uiState: StateFlow<StatisticsUiState> = combine(
        authRepository.observeAuthState().flatMapLatest { user ->
            if (user == null) flowOf(null) else userRepository.observeUserProfile(user.uid)
        },
        vocabularyRepository.getAllVocabulary(),
        dailyActivityRepository.observeUniqueWords(today),
        dailyActivityRepository.observeCounts(today - 6, today),
    ) { profile, words, todayWords, history ->
        val xp = profile?.xp ?: 0
        val level = LevelCalculator.levelForXp(xp)
        StatisticsUiState(
            isLoading = false,
            todayWords = todayWords,
            weeklyActivity = WeeklyActivityMapper.map(history, today),
            totalWords = words.size,
            newWords = words.count { it.masteryLevel == MasteryLevel.New },
            learningWords = words.count { it.masteryLevel == MasteryLevel.Learning },
            familiarWords = words.count { it.masteryLevel == MasteryLevel.Familiar },
            masteredWords = words.count { it.masteryLevel == MasteryLevel.Mastered },
            xp = xp,
            level = level,
            xpProgress = LevelCalculator.xpProgressInLevel(xp),
            streakDays = StreakCalculator.effectiveStreak(
                profile?.streakDays ?: 0,
                profile?.lastActivityEpochDay,
                today,
            ),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StatisticsUiState())
}

