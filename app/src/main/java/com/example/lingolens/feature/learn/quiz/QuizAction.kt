package com.example.lingolens.feature.learn.quiz

sealed interface QuizAction { data object Back : QuizAction; data class SelectAnswer(val index: Int) : QuizAction; data object CheckAnswer : QuizAction; data object Next : QuizAction }
