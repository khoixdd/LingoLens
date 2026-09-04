package com.example.lingolens.feature.profile.achievements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lingolens.domain.gamification.AchievementDefinitions
import com.example.lingolens.domain.repository.AuthRepository
import com.example.lingolens.domain.repository.LearningProgressRepository
import com.example.lingolens.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AchievementsViewModel @Inject constructor(
    authRepository: AuthRepository,
    userRepository: UserRepository,
    progressRepository: LearningProgressRepository,
) : ViewModel() {
    init {
        viewModelScope.launch { progressRepository.evaluateCurrentAchievements() }
    }

    val uiState: StateFlow<AchievementsUiState> = authRepository.observeAuthState()
        .flatMapLatest { user ->
            if (user == null) flowOf(null) else userRepository.observeUserProfile(user.uid)
        }
        .map { profile ->
            AchievementsUiState(
                isLoading = false,
                achievements = AchievementDefinitions.all.map { definition ->
                    AchievementItemUi(definition, definition.id in profile?.unlockedAchievementIds.orEmpty())
                },
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AchievementsUiState())
}

