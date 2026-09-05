package com.example.lingolens.feature.celebration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lingolens.domain.model.AchievementUnlock
import com.example.lingolens.domain.repository.AuthRepository
import com.example.lingolens.domain.repository.LearningProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CelebrationViewModel @Inject constructor(
    progress: LearningProgressRepository,
    private val auth: AuthRepository,
) : ViewModel() {
    private val pending = MutableStateFlow<List<AchievementUnlock>>(emptyList())
    val uiState = pending.asStateFlow()
    private val seen = mutableSetOf<AchievementUnlock>()

    init {
        viewModelScope.launch {
            progress.achievementUnlocks.collect { event ->
                if (event.userId == auth.getCurrentUser()?.uid && seen.add(event)) {
                    pending.update { it + event }
                }
            }
        }
        viewModelScope.launch {
            auth.observeAuthState().collect { user ->
                pending.update { events -> events.filter { it.userId == user?.uid } }
            }
        }
    }

    fun dismiss(event: AchievementUnlock) { pending.update { it - event } }
}
