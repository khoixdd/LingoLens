package com.example.lingolens.feature.learn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lingolens.domain.model.MasteryLevel
import com.example.lingolens.domain.repository.VocabularyRepository
import com.example.lingolens.domain.repository.DailyActivityRepository
import com.example.lingolens.domain.model.DEFAULT_DAILY_GOAL
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class LearnViewModel @Inject constructor(
    private val repository: VocabularyRepository,
    dailyActivityRepository: DailyActivityRepository,
) : ViewModel() {

    private val todayEpochDay = java.time.LocalDate.now().toEpochDay()

    val uiState: StateFlow<LearnUiState> = combine(
        repository.getAllVocabulary(),
        dailyActivityRepository.observeUniqueWords(todayEpochDay),
    ) { allWords, dailyWords ->
        val now = System.currentTimeMillis()
        val dueForReview = allWords.count { word ->
            word.nextReviewAt == null || word.nextReviewAt <= now
        }
        val newWords = allWords.count { it.masteryLevel == MasteryLevel.New }
        val learningWords = allWords.count { it.masteryLevel == MasteryLevel.Learning }
        val familiarWords = allWords.count { it.masteryLevel == MasteryLevel.Familiar }
        val masteredWords = allWords.count { it.masteryLevel == MasteryLevel.Mastered }

        LearnUiState(
            isLoading = false,
            reviewCount = dueForReview,
            notebookCount = allWords.size,
            newCount = newWords,
            learningCount = learningWords,
            familiarCount = familiarWords,
            masteredCount = masteredWords,
            dailyGoalCompleted = dailyWords,
            dailyGoalTarget = DEFAULT_DAILY_GOAL,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LearnUiState(),
    )
}
