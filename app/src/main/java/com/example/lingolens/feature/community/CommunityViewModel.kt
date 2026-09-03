package com.example.lingolens.feature.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lingolens.domain.model.UserProfile
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
        userRepository.observeLeaderboard(),
        selectedPeriod,
    ) { userProfile: UserProfile?, firestoreLeaderboard: List<UserProfile>, period ->
        val authUser = authRepository.getCurrentUser()
        val currentUid = authUser?.uid.orEmpty()
        val emailPrefix = authUser?.email?.substringBefore("@").orEmpty()
        val currentName = userProfile?.username.orEmpty()
            .ifBlank { authUser?.displayName.orEmpty() }
            .ifBlank { emailPrefix }
            .ifBlank { "Learner" }

        val currentXp = userProfile?.xp ?: 100
        val currentLevel = userProfile?.level ?: 1
        val currentStreak = userProfile?.streakDays ?: 1

        val sampleBaseUsers = listOf(
            UserProfile(uid = "sample_1", username = "User A", xp = 2100, level = 11, streakDays = 15),
            UserProfile(uid = "sample_2", username = "User B", xp = 1850, level = 10, streakDays = 12),
            UserProfile(uid = "sample_3", username = "User C", xp = 1700, level = 9, streakDays = 9),
        )

        val mergedProfilesMap = mutableMapOf<String, UserProfile>()
        sampleBaseUsers.forEach { mergedProfilesMap[it.uid] = it }
        firestoreLeaderboard.forEach { mergedProfilesMap[it.uid] = it }

        if (currentUid.isNotBlank()) {
            val activeProfile = UserProfile(
                uid = currentUid,
                username = currentName,
                email = userProfile?.email.orEmpty().ifBlank { authUser?.email.orEmpty() },
                avatarUrl = userProfile?.avatarUrl.orEmpty(),
                xp = currentXp,
                level = currentLevel,
                streakDays = currentStreak,
            )
            mergedProfilesMap[currentUid] = activeProfile
        }

        val sortedEntries = mergedProfilesMap.values
            .sortedByDescending { it.xp }
            .mapIndexed { index, profile ->
                val isCurrent = (currentUid.isNotBlank() && profile.uid == currentUid) ||
                    (currentName.isNotBlank() && profile.username.equals(currentName, ignoreCase = true) && !profile.uid.startsWith("sample_"))
                val finalXp = if (period == LeaderboardPeriod.ThisWeek) profile.xp else profile.xp * 8
                LeaderboardEntry(
                    rank = index + 1,
                    name = if (isCurrent) currentName else profile.username,
                    level = profile.level,
                    xp = finalXp,
                    streakDays = profile.streakDays,
                    isCurrentUser = isCurrent,
                )
            }

        CommunityUiState(
            selectedPeriod = period,
            leaderboard = sortedEntries,
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
