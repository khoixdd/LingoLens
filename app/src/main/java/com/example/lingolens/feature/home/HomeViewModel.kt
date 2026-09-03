package com.example.lingolens.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lingolens.domain.repository.AuthRepository
import com.example.lingolens.domain.repository.UserRepository
import com.example.lingolens.domain.repository.VocabularyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
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
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        authRepository.observeAuthState().flatMapLatest { authUser ->
            if (authUser != null) {
                userRepository.observeUserProfile(authUser.uid)
            } else {
                flowOf(null)
            }
        },
        vocabularyRepository.getAllVocabulary(),
    ) { userProfile, allWords ->
        val authUser = authRepository.getCurrentUser()
        val displayName = userProfile?.username.orEmpty()
            .ifBlank { authUser?.displayName.orEmpty() }
            .ifBlank { "Learner" }

        val now = System.currentTimeMillis()
        val dueForReview = allWords.count { word ->
            word.nextReviewAt == null || word.nextReviewAt <= now
        }

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfTodayMs = calendar.timeInMillis

        val completedToday = allWords.count { word ->
            word.lastReviewedAt != null && word.lastReviewedAt >= startOfTodayMs
        }

        HomeUiState(
            name = displayName,
            streakDays = userProfile?.streakDays ?: 1,
            level = userProfile?.level ?: 1,
            title = when (userProfile?.level ?: 1) {
                in 1..3 -> "Explorer"
                in 4..7 -> "Polyglot"
                else -> "Master"
            },
            xp = userProfile?.xp ?: 100,
            dailyWordsCompleted = completedToday,
            dailyWordsGoal = 10,
            reviewWordsDue = dueForReview,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(),
    )
}
