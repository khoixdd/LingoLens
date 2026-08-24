package com.example.lingolens.feature.learn.quiz

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class QuizViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    fun onAction(action: QuizAction): QuizCompletion? {
        when (action) {
            is QuizAction.SelectAnswer -> if (_uiState.value.answerState in listOf(QuizAnswerState.Unanswered, QuizAnswerState.Selected)) _uiState.update { it.copy(selectedIndex = action.index, answerState = QuizAnswerState.Selected) }
            QuizAction.CheckAnswer -> _uiState.update { state -> state.copy(answerState = if (state.selectedIndex == state.correctIndex) QuizAnswerState.Correct else QuizAnswerState.Incorrect) }
            QuizAction.Next -> {
                val state = _uiState.value
                val earnedScore = state.score + if (state.answerState == QuizAnswerState.Correct) 1 else 0
                if (state.questionIndex == state.totalQuestions - 1) return QuizCompletion(earnedScore, state.totalQuestions)
                _uiState.value = state.copy(questionIndex = state.questionIndex + 1, selectedIndex = null, answerState = QuizAnswerState.Unanswered, score = earnedScore)
            }
            QuizAction.Back -> Unit
        }
        return null
    }
}
