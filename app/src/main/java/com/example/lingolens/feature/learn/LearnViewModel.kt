package com.example.lingolens.feature.learn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lingolens.domain.model.MasteryLevel
import com.example.lingolens.domain.repository.VocabularyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class LearnViewModel @Inject constructor(
    private val repository: VocabularyRepository,
) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.seedSampleDataIfEmpty()
        }
    }

    val uiState: StateFlow<LearnUiState> = repository.getAllVocabulary().map { allWords ->
        val now = System.currentTimeMillis()
        val dueForReview = allWords.count { word ->
            word.nextReviewAt == null || word.nextReviewAt <= now
        }
        val newWords = allWords.count { it.masteryLevel == MasteryLevel.New }
        val learningWords = allWords.count { it.masteryLevel == MasteryLevel.Learning }
        val familiarWords = allWords.count { it.masteryLevel == MasteryLevel.Familiar }
        val masteredWords = allWords.count { it.masteryLevel == MasteryLevel.Mastered }

        LearnUiState(
            reviewCount = dueForReview,
            notebookCount = allWords.size,
            newCount = newWords,
            learningCount = learningWords,
            familiarCount = familiarWords,
            masteredCount = masteredWords,
            dailyGoalCompleted = (allWords.size - dueForReview).coerceAtLeast(0),
            dailyGoalTarget = 10,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LearnUiState(),
    )
}
