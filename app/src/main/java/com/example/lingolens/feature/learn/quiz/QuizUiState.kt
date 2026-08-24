package com.example.lingolens.feature.learn.quiz

enum class QuizAnswerState { Unanswered, Selected, Correct, Incorrect }

data class QuizUiState(
    val questionIndex: Int = 2,
    val totalQuestions: Int = 10,
    val prompt: String = "Choose the correct meaning of the word below.",
    val word: String = "ubiquitous",
    val options: List<String> = listOf("hiếm hoi", "khó khăn", "phổ biến, có mặt ở khắp mọi nơi", "tạm thời"),
    val correctIndex: Int = 2,
    val selectedIndex: Int? = null,
    val answerState: QuizAnswerState = QuizAnswerState.Unanswered,
    val score: Int = 2,
)

data class QuizCompletion(val score: Int, val total: Int)
