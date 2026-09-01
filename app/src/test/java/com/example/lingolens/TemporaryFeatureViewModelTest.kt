package com.example.lingolens

import com.example.lingolens.feature.community.CommunityAction
import com.example.lingolens.feature.community.CommunityViewModel
import com.example.lingolens.feature.community.LeaderboardPeriod
import com.example.lingolens.feature.home.HomeViewModel
import com.example.lingolens.feature.scan.ScanAction
import com.example.lingolens.feature.scan.ScanViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TemporaryFeatureViewModelTest {
    @Test
    fun homeExposesFirstPassDashboardState() {
        val state = HomeViewModel().uiState.value
        assertEquals(7, state.dailyWordsCompleted)
        assertEquals(10, state.dailyWordsGoal)
        assertEquals(7, state.weeklyActivity.size)
    }

    @Test
    fun scanInteractionsOnlyUpdateLocalPlaceholderState() {
        val viewModel = ScanViewModel()
        viewModel.onAction(ScanAction.ToggleFlash)
        assertTrue(viewModel.uiState.value.isFlashEnabled)

        viewModel.onAction(ScanAction.Capture)
        assertNotNull(viewModel.uiState.value.feedbackMessage)

        viewModel.onAction(ScanAction.DismissFeedback)
        assertFalse(viewModel.uiState.value.feedbackMessage != null)
    }

    @Test
    fun communityPeriodSwitchesMockLeaderboard() {
        val viewModel = CommunityViewModel()
        viewModel.onAction(CommunityAction.SelectPeriod(LeaderboardPeriod.AllTime))
        assertEquals(LeaderboardPeriod.AllTime, viewModel.uiState.value.selectedPeriod)
        assertEquals(19840, viewModel.uiState.value.leaderboard.first().xp)
    }
}
