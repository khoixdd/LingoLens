package com.example.lingolens.feature.community

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class CommunityViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(CommunityUiState())
    val uiState: StateFlow<CommunityUiState> = _uiState.asStateFlow()

    fun onAction(action: CommunityAction) {
        when (action) {
            is CommunityAction.SelectPeriod -> {
                _uiState.value = CommunityUiState(
                    selectedPeriod = action.period,
                    leaderboard = if (action.period == LeaderboardPeriod.ThisWeek) {
                        sampleWeeklyLeaderboard
                    } else {
                        sampleAllTimeLeaderboard
                    },
                )
            }
        }
    }
}
