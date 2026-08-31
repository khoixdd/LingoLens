package com.example.lingolens.feature.learn.quiz

enum class QuizAnswerState { Unanswered, Selected, Correct, Incorrect }

data class QuizUiState(
    val isLoading: Boolean = true,
    val isEmpty: Boolean = false,
    val questionIndex: Int = 0,
    val totalQuestions: Int = 0,
    val prompt: String = "Choose the correct meaning of the word below.",
    val word: String = "",
    val options: List<String> = emptyList(),
    val correctIndex: Int = 0,
    val selectedIndex: Int? = null,
    val answerState: QuizAnswerState = QuizAnswerState.Unanswered,
    val score: Int = 0,
)

data class QuizCompletion(val score: Int, val total: Int)
