package com.example.lingolens

import com.example.lingolens.feature.learn.notebook.NotebookAction
import com.example.lingolens.feature.learn.notebook.NotebookContentState
import com.example.lingolens.feature.learn.notebook.NotebookViewModel
import com.example.lingolens.feature.learn.quiz.QuizAction
import com.example.lingolens.feature.learn.quiz.QuizAnswerState
import com.example.lingolens.feature.learn.quiz.QuizViewModel
import com.example.lingolens.feature.learn.review.ReviewAction
import com.example.lingolens.feature.learn.review.ReviewRating
import com.example.lingolens.feature.learn.review.ReviewViewModel
import com.example.lingolens.feature.profile.notification.NotificationSetting
import com.example.lingolens.feature.profile.notification.NotificationSettingsAction
import com.example.lingolens.feature.profile.notification.NotificationSettingsViewModel
import com.example.lingolens.feature.profile.privacy.PrivacySettingsAction
import com.example.lingolens.feature.profile.privacy.PrivacySettingsViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FeatureViewModelTest {
    @Test fun notebookSearchExposesNoResultsState() {
        val viewModel = NotebookViewModel()
        viewModel.onAction(NotebookAction.SearchChanged("not in notebook"))
        assertTrue(viewModel.uiState.value.content is NotebookContentState.NoSearchResults)
    }

    @Test fun reviewRevealAndRatingAdvanceTheCard() {
        val viewModel = ReviewViewModel()
        viewModel.onAction(ReviewAction.Reveal)
        assertTrue(viewModel.uiState.value.isRevealed)
        viewModel.onAction(ReviewAction.Rate(ReviewRating.Good))
        assertEquals(1, viewModel.uiState.value.currentIndex)
        assertFalse(viewModel.uiState.value.isRevealed)
    }

    @Test fun quizShowsCorrectFeedbackBeforeMovingNext() {
        val viewModel = QuizViewModel()
        viewModel.onAction(QuizAction.SelectAnswer(2))
        assertEquals(QuizAnswerState.Selected, viewModel.uiState.value.answerState)
        viewModel.onAction(QuizAction.CheckAnswer)
        assertEquals(QuizAnswerState.Correct, viewModel.uiState.value.answerState)
        viewModel.onAction(QuizAction.Next)
        assertEquals(3, viewModel.uiState.value.questionIndex)
        assertEquals(QuizAnswerState.Unanswered, viewModel.uiState.value.answerState)
    }

    @Test fun notificationToggleUpdatesLocalState() {
        val viewModel = NotificationSettingsViewModel()
        viewModel.onAction(NotificationSettingsAction.Toggle(NotificationSetting.DailyReminder, false))
        assertFalse(viewModel.uiState.value.dailyReminder)
    }

    @Test fun privacyToggleUpdatesLocalStateWithoutRequestingPermission() {
        val viewModel = PrivacySettingsViewModel()
        viewModel.onAction(PrivacySettingsAction.ShareLocationChanged(true))
        assertTrue(viewModel.uiState.value.shareLocation)
        assertEquals("Required", viewModel.uiState.value.locationPermission)
    }
}
