package com.example.lingolens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.lingolens.domain.model.WeeklyActivityDay
import com.example.lingolens.feature.home.*
import com.example.lingolens.ui.theme.LingoLensTheme
import java.time.LocalDate
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class HomeLayoutTest {
    @get:Rule val compose = createComposeRule()

    private fun zeroWeek() = (6 downTo 0).map {
        WeeklyActivityDay(LocalDate.now().minusDays(it.toLong()).toEpochDay(), "", 0)
    }

    private fun scrollTo(text: String) {
        compose.onNode(hasScrollAction()).performScrollToNode(hasText(text))
        compose.onNodeWithText(text).assertIsDisplayed()
    }

    private fun assertTextFits(text: String) {
        val results = mutableListOf<TextLayoutResult>()
        compose.onNodeWithText(text).performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
        assertTrue(results.isNotEmpty())
        assertTrue("Text overflow: $text", results.none { it.hasVisualOverflow })
    }

    @Test fun smallScreenLongTextZeroStatesAndAllActionsRemainAccessible() {
        val name = "Alexandra Catherine Nguyen"
        val title = "A wonderfully persistent vocabulary explorer"
        val actions = mutableListOf<HomeAction>()
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, 1.3f)) {
                LingoLensTheme {
                    Box(Modifier.size(320.dp, 480.dp)) {
                        HomeScreen(HomeUiState(isLoading = false, name = name, title = title, weeklyActivity = zeroWeek()), actions::add)
                    }
                }
            }
        }
        assertTextFits("Hello, $name!")
        scrollTo("$title · 0 XP")
        assertTextFits("$title · 0 XP")
        scrollTo("0 / 10 words")
        scrollTo("0%")
        scrollTo("0 words due")
        scrollTo("You're all caught up!")
        for ((label, action) in listOf(
            "Notebook" to HomeAction.OpenNotebook, "Quiz" to HomeAction.OpenQuiz,
            "Statistics" to HomeAction.OpenStatistics, "Achievements" to HomeAction.OpenAchievements,
            "Continue Learning" to HomeAction.OpenLearn)) {
            scrollTo(label)
            assertTextFits(label)
            compose.onNodeWithText(label).performClick()
            compose.runOnIdle { assertEquals(action, actions.last()) }
        }
        scrollTo("0 words studied this week")
    }

    @Test fun normalPhoneCompletedGoalAndRealStateDerivedTotalsFit() {
        compose.setContent {
            LingoLensTheme {
                Box(Modifier.size(360.dp, 720.dp)) {
                    HomeScreen(HomeUiState(isLoading = false, name = "Learner", dailyWordsCompleted = 10,
                        reviewWordsDue = 4, weeklyActivity = zeroWeek().mapIndexed { index, day -> day.copy(uniqueWords = index) }), {})
                }
            }
        }
        scrollTo("10 / 10 words")
        scrollTo("100%")
        scrollTo("Goal complete! Nice work today.")
        scrollTo("4 words due")
        scrollTo("21 words studied this week")
        scrollTo("Continue Learning")
    }
}
