package com.example.lingolens.feature.learn.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lingolens.core.common.TextToSpeechHelper
import com.example.lingolens.domain.model.MasteryLevel
import com.example.lingolens.domain.model.Vocabulary
import com.example.lingolens.domain.repository.VocabularyRepository
import com.example.lingolens.domain.repository.LearningProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ReviewViewModel @Inject constructor(
    val repository: VocabularyRepository,
    val ttsHelper: TextToSpeechHelper,
    private val learningProgressRepository: LearningProgressRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewUiState())
    val uiState: StateFlow<ReviewUiState> = _uiState.asStateFlow()

    private var allVocabularyList: List<Vocabulary> = emptyList()
    private var reviewWords: List<Vocabulary> = emptyList()
    private val ratedIndexes = mutableSetOf<Int>()

    init {
        loadReviewQueue()
    }

    private fun loadReviewQueue() {
        viewModelScope.launch {
            repository.seedSampleDataIfEmpty()
            allVocabularyList = repository.getAllVocabulary().first()
            val now = System.currentTimeMillis()

            val dueWords = allVocabularyList.filter { word ->
                word.nextReviewAt == null || word.nextReviewAt <= now
            }

            if (dueWords.isEmpty()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isEmpty = true,
                        total = 0,
                    )
                }
            } else {
                reviewWords = dueWords
                updateStateForCurrentIndex(0)
            }
        }
    }

    private fun updateStateForCurrentIndex(index: Int) {
        if (index >= reviewWords.size) {
            _uiState.update { it.copy(isCompleted = true, isRevealed = false) }
            return
        }
        val current = reviewWords[index]
        _uiState.update {
            it.copy(
                isLoading = false,
                isEmpty = false,
                isCompleted = false,
                currentIndex = index,
                total = reviewWords.size,
                word = current.word,
                pronunciation = current.pronunciation,
                meaning = current.meaning,
                example = current.example,
                isRevealed = false,
            )
        }
    }

    fun onAction(action: ReviewAction) {
        when (action) {
            ReviewAction.Reveal -> _uiState.update { it.copy(isRevealed = !it.isRevealed) }
            ReviewAction.PlayPronunciation -> {
                val currentWord = _uiState.value.word
                if (currentWord.isNotBlank()) {
                    ttsHelper.speak(currentWord)
                }
            }
            ReviewAction.StartReviewAll -> {
                if (allVocabularyList.isNotEmpty()) {
                    reviewWords = allVocabularyList
                    updateStateForCurrentIndex(0)
                }
            }
            is ReviewAction.Rate -> {
                val index = _uiState.value.currentIndex
                if (_uiState.value.isRevealed && index < reviewWords.size && ratedIndexes.add(index)) {
                    val currentWord = reviewWords[index]
                    val updatedWord = calculateSrsUpdate(currentWord, action.rating)
                    viewModelScope.launch {
                        repository.updateVocabulary(updatedWord)
                        learningProgressRepository.recordActivity(currentWord.id, REVIEW_XP)
                    }
                    updateStateForCurrentIndex(index + 1)
                }
            }
            ReviewAction.Back -> Unit
        }
    }

    private fun calculateSrsUpdate(current: Vocabulary, rating: ReviewRating): Vocabulary {
        val now = System.currentTimeMillis()
        val oneDayMs = 24 * 60 * 60 * 1000L

        val (nextMastery, addedCorrect, addedWrong, daysUntilNext) = when (rating) {
            ReviewRating.Again -> Tuple(
                MasteryLevel.Learning,
                0,
                1,
                1,
            )
            ReviewRating.Hard -> Tuple(
                current.masteryLevel,
                0,
                0,
                2,
            )
            ReviewRating.Good -> Tuple(
                when (current.masteryLevel) {
                    MasteryLevel.New -> MasteryLevel.Learning
                    MasteryLevel.Learning -> MasteryLevel.Familiar
                    MasteryLevel.Familiar -> MasteryLevel.Mastered
                    MasteryLevel.Mastered -> MasteryLevel.Mastered
                },
                1,
                0,
                5,
            )
            ReviewRating.Easy -> Tuple(
                MasteryLevel.Mastered,
                1,
                0,
                10,
            )
        }

        return current.copy(
            masteryLevel = nextMastery,
            correctCount = current.correctCount + addedCorrect,
            wrongCount = current.wrongCount + addedWrong,
            lastReviewedAt = now,
            nextReviewAt = now + (daysUntilNext * oneDayMs),
        )
    }

    override fun onCleared() {
        super.onCleared()
        ttsHelper.shutdown()
    }

    private data class Tuple(
        val masteryLevel: MasteryLevel,
        val correct: Int,
        val wrong: Int,
        val days: Int,
    )

    private companion object {
        const val REVIEW_XP = 5
    }
}
