package com.example.lingolens.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lingolens.domain.gamification.LevelCalculator
import com.example.lingolens.domain.gamification.StreakCalculator
import com.example.lingolens.domain.gamification.WeeklyActivityMapper
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
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val vocabularyRepository: VocabularyRepository,
    dailyActivityRepository: DailyActivityRepository,
) : ViewModel() {
    private val todayEpochDay = LocalDate.now().toEpochDay()

    val uiState: StateFlow<HomeUiState> = combine(
        authRepository.observeAuthState().flatMapLatest { authUser ->
            if (authUser != null) userRepository.observeUserProfile(authUser.uid) else flowOf(null)
        },
        vocabularyRepository.getAllVocabulary(),
        dailyActivityRepository.observeUniqueWords(todayEpochDay),
        dailyActivityRepository.observeCounts(todayEpochDay - 6, todayEpochDay),
    ) { userProfile, allWords, completedToday, activityHistory ->
        val authUser = authRepository.getCurrentUser()
        val name = userProfile?.username.orEmpty()
            .ifBlank { authUser?.displayName.orEmpty() }
            .ifBlank { "Learner" }
        val xp = userProfile?.xp ?: 0
        val level = LevelCalculator.levelForXp(xp)
        HomeUiState(
            isLoading = false,
            name = name,
            streakDays = StreakCalculator.effectiveStreak(
                userProfile?.streakDays ?: 0,
                userProfile?.lastActivityEpochDay,
                todayEpochDay,
            ),
            level = level,
            title = LevelCalculator.titleForLevel(level),
            xp = xp,
            xpProgressInLevel = LevelCalculator.xpProgressInLevel(xp),
            totalWords = allWords.size,
            dailyWordsCompleted = completedToday,
            reviewWordsDue = allWords.count {
                it.nextReviewAt == null || it.nextReviewAt <= System.currentTimeMillis()
            },
            weeklyActivity = WeeklyActivityMapper.map(activityHistory, todayEpochDay),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState())
}
