package com.example.lingolens.feature.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lingolens.domain.model.LeaderboardLoadState
import com.example.lingolens.domain.model.UserProfile
import com.example.lingolens.domain.repository.AuthRepository
import com.example.lingolens.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    val uiState: StateFlow<CommunityUiState> = combine(
        authRepository.observeAuthState().flatMapLatest { authUser ->
            if (authUser != null) {
                userRepository.observeUserProfile(authUser.uid)
            } else {
                flowOf(null)
            }
        }.onStart { emit(null) },
        userRepository.observeLeaderboard(),
    ) { userProfile: UserProfile?, leaderboardState: LeaderboardLoadState ->
        val authUser = authRepository.getCurrentUser()
        val currentUid = authUser?.uid.orEmpty()
        val emailPrefix = authUser?.email?.substringBefore("@").orEmpty()
        val currentName = userProfile?.username.orEmpty()
            .ifBlank { authUser?.displayName.orEmpty() }
            .ifBlank { emailPrefix }

        when (leaderboardState) {
            LeaderboardLoadState.Loading -> CommunityUiState(isLeaderboardLoading = true)
            is LeaderboardLoadState.Error -> CommunityUiState(
                isLeaderboardLoading = false,
                leaderboardError = leaderboardState.message,
            )
            is LeaderboardLoadState.Data -> {
                val mergedProfiles = leaderboardState.users.associateByTo(mutableMapOf()) { it.uid }
                if (currentUid.isNotBlank() && userProfile != null) {
                    mergedProfiles[currentUid] = UserProfile(
                        uid = currentUid,
                        username = currentName.ifBlank { "Learner" },
                        email = userProfile?.email.orEmpty().ifBlank { authUser?.email.orEmpty() },
                        avatarUrl = userProfile?.avatarUrl.orEmpty(),
                        xp = userProfile?.xp ?: 100,
                        level = userProfile?.level ?: 1,
                        streakDays = userProfile?.streakDays ?: 1,
                    )
                }
                CommunityUiState(
                    leaderboard = mergedProfiles.values
                        .sortedByDescending { it.xp }
                        .mapIndexed { index, profile ->
                            val isCurrent = profile.uid == currentUid
                            LeaderboardEntry(
                                rank = index + 1,
                                name = if (isCurrent && currentName.isNotBlank()) currentName else profile.username,
                                level = profile.level,
                                xp = profile.xp,
                                streakDays = profile.streakDays,
                                isCurrentUser = isCurrent,
                            )
                        },
                    isLeaderboardLoading = false,
                    hasLeaderboardData = true,
                    isLeaderboardFromCache = leaderboardState.isFromCache,
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CommunityUiState(),
    )
}
