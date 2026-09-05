package com.example.lingolens

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.lingolens.ui.components.CelebrationOverlay
import com.example.lingolens.ui.theme.LingoLensTheme
import com.example.lingolens.feature.learn.quiz.QuizResultScreen
import com.example.lingolens.navigation.QuizResult
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CelebrationUiTest {
    @get:Rule val compose = createComposeRule()

    @Test fun confettiIsFiniteAndDoesNotBlockInteractionOrReplayOnRecomposition() {
        compose.mainClock.autoAdvance = false
        var completions = 0
        var clicks by mutableIntStateOf(0)
        compose.setContent {
            LingoLensTheme {
                Box {
                    Button(onClick = { clicks++ }) { Text("Continue $clicks") }
                    CelebrationOverlay(visible = true, onFinished = { completions++ })
                }
            }
        }
        compose.onNodeWithText("Continue 0").performTouchInput { click() }
        compose.mainClock.advanceTimeByFrame()
        compose.onNodeWithText("Continue 1").assertExists()
        compose.mainClock.advanceTimeBy(2300)
        compose.runOnIdle { assertEquals(1, completions) }
        compose.onNodeWithText("Continue 1").performClick()
        compose.mainClock.advanceTimeBy(2500)
        compose.runOnIdle { assertEquals(1, completions) }
    }

    @Test fun resultFinishesWithOriginalScoreAndXpAndKeepsNavigationUsable() {
        compose.mainClock.autoAdvance = false
        var left = 0
        compose.setContent {
            LingoLensTheme {
                QuizResultScreen(8, 10, resultId = 1L, onReviewAnswers = {}, onBackToLearn = { left++ })
            }
        }
        compose.mainClock.advanceTimeBy(2400)
        compose.onNodeWithText("8 / 10").assertExists()
        compose.onNodeWithText("+100 XP").assertExists()
        compose.onNodeWithText("Back to Learn").performClick()
        compose.runOnIdle { assertEquals(1, left) }
    }

    @Test fun sameScoreAttemptsCelebrateIndependentlyWithoutReplayingOnRecomposition() {
        compose.mainClock.autoAdvance = false
        val attemptA = QuizResult(8, 10, attemptId = 1L)
        val attemptB = QuizResult(8, 10, attemptId = 2L)
        assertNotEquals(attemptA, attemptB) // Navigation/saveable entry identities must differ.
        var attempt by mutableStateOf(attemptA)
        var revision by mutableIntStateOf(0)
        compose.setContent {
            LingoLensTheme {
                QuizResultScreen(
                    score = attempt.score,
                    total = attempt.total,
                    resultId = attempt.attemptId,
                    onReviewAnswers = {},
                    onBackToLearn = {},
                    modifier = Modifier.testTag("result-$revision"),
                )
            }
        }
        compose.mainClock.advanceTimeBy(100)
        compose.onNodeWithTag("quiz-result-confetti").assertExists()
        compose.mainClock.advanceTimeBy(2300)
        compose.onNodeWithTag("quiz-result-confetti").assertDoesNotExist()

        compose.runOnIdle { attempt = attemptB }
        compose.mainClock.advanceTimeBy(100)
        compose.onNodeWithTag("quiz-result-confetti").assertExists()
        // Recompose while the second attempt is animating; its original deadline must hold.
        compose.mainClock.advanceTimeBy(1000)
        compose.runOnIdle { revision++ }
        compose.mainClock.advanceTimeBy(1200)
        compose.onNodeWithTag("result-1").assertExists()
        compose.onNodeWithTag("quiz-result-confetti").assertDoesNotExist()
        compose.onNodeWithText("8 / 10").assertExists()
        compose.onNodeWithText("+100 XP").assertExists()

        compose.runOnIdle { revision++ }
        compose.mainClock.advanceTimeBy(100)
        compose.onNodeWithTag("result-2").assertExists()
        compose.onNodeWithTag("quiz-result-confetti").assertDoesNotExist()
    }
}
