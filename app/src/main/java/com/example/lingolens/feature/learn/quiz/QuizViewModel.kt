package com.example.lingolens.feature.learn.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lingolens.domain.model.MasteryLevel
import com.example.lingolens.domain.model.Vocabulary
import com.example.lingolens.domain.repository.AuthRepository
import com.example.lingolens.domain.repository.UserRepository
import com.example.lingolens.domain.repository.VocabularyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class QuizViewModel @Inject constructor(
    val repository: VocabularyRepository,
    val authRepository: AuthRepository,
    val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var quizQuestions: List<QuizQuestionData> = emptyList()

    init {
        loadQuizQuestions()
    }

    private fun loadQuizQuestions() {
        viewModelScope.launch {
            repository.seedSampleDataIfEmpty()
            val allWords = repository.getAllVocabulary().first()
            if (allWords.isEmpty()) {
                _uiState.update { it.copy(isLoading = false, isEmpty = true) }
                return@launch
            }

            val defaultDistractors = listOf(
                "hiếm hoi", "khó khăn", "tạm thời", "không rõ ràng",
                "biến đổi", "phức tạp", "đơn giản", "quan trọng",
            )

            quizQuestions = allWords.take(10).map { targetWord ->
                val otherMeanings = allWords
                    .filter { it.id != targetWord.id }
                    .map { it.meaning }
                    .shuffled()

                val neededDistractors = 3
                val distractors = (otherMeanings + defaultDistractors)
                    .distinct()
                    .filter { it != targetWord.meaning }
                    .take(neededDistractors)

                val options = (distractors + targetWord.meaning).shuffled()
                val correctIndex = options.indexOf(targetWord.meaning)

                QuizQuestionData(
                    vocabulary = targetWord,
                    options = options,
                    correctIndex = correctIndex,
                )
            }

            if (quizQuestions.isEmpty()) {
                _uiState.update { it.copy(isLoading = false, isEmpty = true) }
            } else {
                updateQuestionState(0, 0)
            }
        }
    }

    private fun updateQuestionState(index: Int, currentScore: Int) {
        if (index >= quizQuestions.size) return
        val q = quizQuestions[index]
        _uiState.update {
            it.copy(
                isLoading = false,
                isEmpty = false,
                questionIndex = index,
                totalQuestions = quizQuestions.size,
                word = q.vocabulary.word,
                options = q.options,
                correctIndex = q.correctIndex,
                selectedIndex = null,
                answerState = QuizAnswerState.Unanswered,
                score = currentScore,
            )
        }
    }

    fun onAction(action: QuizAction): QuizCompletion? {
        val state = _uiState.value
        when (action) {
            is QuizAction.SelectAnswer -> {
                if (state.answerState in listOf(QuizAnswerState.Unanswered, QuizAnswerState.Selected)) {
                    _uiState.update {
                        it.copy(
                            selectedIndex = action.index,
                            answerState = QuizAnswerState.Selected,
                        )
                    }
                }
            }
            QuizAction.CheckAnswer -> {
                val selected = state.selectedIndex ?: return null
                val isCorrect = selected == state.correctIndex
                val newState = if (isCorrect) QuizAnswerState.Correct else QuizAnswerState.Incorrect
                _uiState.update { it.copy(answerState = newState) }

                val currentQ = quizQuestions.getOrNull(state.questionIndex)
                if (currentQ != null) {
                    val now = System.currentTimeMillis()
                    val target = currentQ.vocabulary
                    val updated = if (isCorrect) {
                        target.copy(
                            correctCount = target.correctCount + 1,
                            lastReviewedAt = now,
                            masteryLevel = when (target.masteryLevel) {
                                MasteryLevel.New -> MasteryLevel.Learning
                                MasteryLevel.Learning -> MasteryLevel.Familiar
                                MasteryLevel.Familiar -> MasteryLevel.Mastered
                                MasteryLevel.Mastered -> MasteryLevel.Mastered
                            },
                        )
                    } else {
                        target.copy(
                            wrongCount = target.wrongCount + 1,
                            lastReviewedAt = now,
                        )
                    }
                    viewModelScope.launch {
                        repository.updateVocabulary(updated)
                    }
                }
            }
            QuizAction.Next -> {
                val isCorrect = state.answerState == QuizAnswerState.Correct
                val earnedScore = state.score + if (isCorrect) 1 else 0
                val nextIndex = state.questionIndex + 1

                if (nextIndex >= state.totalQuestions) {
                    val earnedXp = (earnedScore * 10) + 20 // Award +20 XP base completion bonus + 10 XP per correct answer
                    val currentUser = authRepository.getCurrentUser()
                    if (currentUser != null && earnedXp > 0) {
                        viewModelScope.launch {
                            userRepository.addXp(currentUser.uid, earnedXp)
                        }
                    }
                    return QuizCompletion(earnedScore, state.totalQuestions)
                }

                updateQuestionState(nextIndex, earnedScore)
            }
            QuizAction.Back -> Unit
        }
        return null
    }

    private data class QuizQuestionData(
        val vocabulary: Vocabulary,
        val options: List<String>,
        val correctIndex: Int,
    )
}
