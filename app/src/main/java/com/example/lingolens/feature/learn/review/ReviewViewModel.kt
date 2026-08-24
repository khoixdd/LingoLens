package com.example.lingolens.feature.learn.review

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class ReviewViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    fun onAction(action: ReviewAction) {
        when (action) {
            ReviewAction.Reveal -> _uiState.update { it.copy(isRevealed = true) }
            is ReviewAction.Rate -> _uiState.update { state -> state.copy(currentIndex = (state.currentIndex + 1).coerceAtMost(state.total - 1), isRevealed = false) }
            ReviewAction.Back, ReviewAction.PlayPronunciation -> Unit
        }
    }
}
