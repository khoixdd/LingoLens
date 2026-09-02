package com.example.lingolens.feature.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lingolens.domain.repository.AuthRepository
import com.example.lingolens.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val selectedPeriod = MutableStateFlow(LeaderboardPeriod.ThisWeek)

    val uiState: StateFlow<CommunityUiState> = combine(
        authRepository.observeAuthState().flatMapLatest { authUser ->
            if (authUser != null) {
                userRepository.observeUserProfile(authUser.uid)
            } else {
                flowOf(null)
            }
        },
        selectedPeriod,
    ) { userProfile, period ->
        val authUser = authRepository.getCurrentUser()
        val currentName = userProfile?.username.orEmpty()
            .ifBlank { authUser?.displayName.orEmpty() }
            .ifBlank { "Learner" }
        val currentXp = userProfile?.xp ?: 100
        val currentLevel = userProfile?.level ?: 1
        val currentStreak = userProfile?.streakDays ?: 1

        val currentUserEntry = LeaderboardEntry(
            rank = 4,
            name = currentName,
            level = currentLevel,
            xp = if (period == LeaderboardPeriod.ThisWeek) currentXp else currentXp * 8,
            streakDays = currentStreak,
            isCurrentUser = true,
        )

        val rawList = listOf(
            LeaderboardEntry(1, "Minh", 12, if (period == LeaderboardPeriod.ThisWeek) 2480 else 19840, 21),
            LeaderboardEntry(2, "An", 10, if (period == LeaderboardPeriod.ThisWeek) 2210 else 17680, 18),
            LeaderboardEntry(3, "Khoi", 9, if (period == LeaderboardPeriod.ThisWeek) 1980 else 15840, 16),
            currentUserEntry,
            LeaderboardEntry(5, "Lan", 6, if (period == LeaderboardPeriod.ThisWeek) 1340 else 10720, 9),
        )

        val sortedList = rawList.sortedByDescending { it.xp }
            .mapIndexed { index, entry -> entry.copy(rank = index + 1) }

        CommunityUiState(
            selectedPeriod = period,
            leaderboard = sortedList,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CommunityUiState(),
    )

    fun onAction(action: CommunityAction) {
        when (action) {
            is CommunityAction.SelectPeriod -> selectedPeriod.value = action.period
        }
    }
}
