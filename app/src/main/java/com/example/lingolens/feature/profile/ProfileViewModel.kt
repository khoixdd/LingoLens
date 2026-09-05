package com.example.lingolens.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lingolens.domain.model.UserProfile
import com.example.lingolens.domain.repository.AuthRepository
import com.example.lingolens.domain.repository.UserRepository
import com.example.lingolens.domain.repository.VocabularyRepository
import com.example.lingolens.domain.gamification.LevelCalculator
import com.example.lingolens.domain.gamification.StreakCalculator
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val vocabularyRepository: VocabularyRepository,
) : ViewModel() {

    private val logoutState = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            vocabularyRepository.getAllVocabulary().collect { allWords ->
                val currentUser = authRepository.getCurrentUser()
                if (currentUser != null) {
                    userRepository.syncTotalWords(currentUser.uid, allWords.size)
                }
            }
        }
    }

    val uiState: StateFlow<ProfileUiState> = combine(
        authRepository.observeAuthState().flatMapLatest { authUser ->
            if (authUser != null) {
                userRepository.observeUserProfile(authUser.uid)
            } else {
                flowOf(null)
            }
        },
        vocabularyRepository.getAllVocabulary(),
        logoutState,
    ) { userProfile: UserProfile?, allWords, isLoggedOut ->
        val authUser = authRepository.getCurrentUser()
        val displayName = userProfile?.username.orEmpty()
            .ifBlank { authUser?.displayName.orEmpty() }
            .ifBlank { "Learner" }
        val email = userProfile?.email.orEmpty().ifBlank { authUser?.email.orEmpty() }

        ProfileUiState(
            isLoading = false,
            name = displayName,
            avatarId = userProfile?.avatarId ?: "leaf",
            email = email,
            level = LevelCalculator.levelForXp(userProfile?.xp ?: 0),
            title = LevelCalculator.titleForLevel(LevelCalculator.levelForXp(userProfile?.xp ?: 0)),
            streakDays = StreakCalculator.effectiveStreak(
                userProfile?.streakDays ?: 0,
                userProfile?.lastActivityEpochDay,
                java.time.LocalDate.now().toEpochDay(),
            ),
            xp = userProfile?.xp ?: 0,
            words = allWords.size,
            isLoggedOut = isLoggedOut,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState(),
    )

    fun onAction(action: ProfileAction) {
        when (action) {
            ProfileAction.Logout -> performLogout()
            else -> Unit
        }
    }

    private fun performLogout() {
        viewModelScope.launch {
            authRepository.logout()
            logoutState.value = true
        }
    }
}
