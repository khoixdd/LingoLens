package com.example.lingolens

import com.example.lingolens.core.common.TextToSpeechHelper
import com.example.lingolens.domain.model.MasteryLevel
import com.example.lingolens.domain.model.Vocabulary
import com.example.lingolens.domain.repository.VocabularyRepository
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class FakeVocabularyRepository : VocabularyRepository {
    private val items = MutableStateFlow<List<Vocabulary>>(
        listOf(
            Vocabulary(
                id = "ubiquitous",
                word = "ubiquitous",
                meaning = "phổ biến",
                tags = listOf("Technology"),
                isFavorite = true,
                masteryLevel = MasteryLevel.Learning,
            ),
        ),
    )

    override fun getAllVocabulary(): Flow<List<Vocabulary>> = items
    override fun getVocabularyById(id: String): Flow<Vocabulary?> = items.map { list -> list.firstOrNull { it.id == id } }
    override suspend fun getWordByText(word: String): Vocabulary? = items.value.firstOrNull { it.word.equals(word, ignoreCase = true) }
    override suspend fun isWordDuplicate(word: String): Boolean = items.value.any { it.word.equals(word, ignoreCase = true) }

    override suspend fun addVocabulary(vocabulary: Vocabulary) {
        val existing = items.value.firstOrNull { it.word.equals(vocabulary.word, ignoreCase = true) }
        if (existing != null) {
            updateVocabulary(vocabulary.copy(id = existing.id))
        } else {
            items.update { it + vocabulary }
        }
    }

    override suspend fun updateVocabulary(vocabulary: Vocabulary) {
        items.update { list -> list.map { if (it.id == vocabulary.id) vocabulary else it } }
    }

    override suspend fun toggleFavorite(id: String) {
        items.update { list -> list.map { if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it } }
    }

    override suspend fun deleteVocabulary(id: String) {
        items.update { list -> list.filterNot { it.id == id } }
    }

    override suspend fun seedSampleDataIfEmpty() {}
}

@OptIn(ExperimentalCoroutinesApi::class)
class FeatureViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val ttsHelper = mock(TextToSpeechHelper::class.java)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun notebookSearchExposesNoResultsState() = runTest(testDispatcher) {
        val repository = FakeVocabularyRepository()
        val viewModel = NotebookViewModel(repository, ttsHelper)
        backgroundScope.launch {
            viewModel.uiState.collect {}
        }

        viewModel.onAction(NotebookAction.SearchChanged("not in notebook"))

        assertTrue(viewModel.uiState.value.content is NotebookContentState.NoSearchResults)
    }

    @Test
    fun reviewRevealAndRatingAdvanceTheCard() = runTest(testDispatcher) {
        val repository = FakeVocabularyRepository()
        val viewModel = ReviewViewModel(repository, ttsHelper)

        viewModel.onAction(ReviewAction.Reveal)
        assertTrue(viewModel.uiState.value.isRevealed)

        viewModel.onAction(ReviewAction.Rate(ReviewRating.Good))
        assertTrue(viewModel.uiState.value.isCompleted)

        val updatedWord = repository.getWordByText("ubiquitous")
        assertNotNull(updatedWord)
        assertEquals(MasteryLevel.Familiar, updatedWord?.masteryLevel)
        assertTrue((updatedWord?.nextReviewAt ?: 0) > System.currentTimeMillis())
    }

    @Test
    fun duplicateWordPreventionUpdatesExistingWord() = runTest(testDispatcher) {
        val repository = FakeVocabularyRepository()
        assertTrue(repository.isWordDuplicate("UBIQUITOUS"))

        val duplicateInput = Vocabulary(
            id = "new_id",
            word = "ubiquitous",
            meaning = "phổ biến ở khắp mọi nơi",
        )
        repository.addVocabulary(duplicateInput)

        val words = repository.getAllVocabulary()
        var list: List<Vocabulary> = emptyList()
        val job = backgroundScope.launch { words.collect { list = it } }

        assertEquals(1, list.size)
        assertEquals("phổ biến ở khắp mọi nơi", list.first().meaning)
        job.cancel()
    }

    @Test
    fun quizShowsCorrectFeedbackBeforeMovingNext() = runTest(testDispatcher) {
        val repository = FakeVocabularyRepository()
        val viewModel = QuizViewModel(repository)

        val correctIdx = viewModel.uiState.value.correctIndex
        viewModel.onAction(QuizAction.SelectAnswer(correctIdx))
        assertEquals(QuizAnswerState.Selected, viewModel.uiState.value.answerState)

        viewModel.onAction(QuizAction.CheckAnswer)
        assertEquals(QuizAnswerState.Correct, viewModel.uiState.value.answerState)

        val completion = viewModel.onAction(QuizAction.Next)
        assertEquals(1, completion?.score)
    }

    @Test
    fun notificationToggleUpdatesLocalState() {
        val viewModel = NotificationSettingsViewModel()
        viewModel.onAction(NotificationSettingsAction.Toggle(NotificationSetting.DailyReminder, false))
        assertFalse(viewModel.uiState.value.dailyReminder)
    }

    @Test
    fun privacyToggleUpdatesLocalStateWithoutRequestingPermission() {
        val viewModel = PrivacySettingsViewModel()
        viewModel.onAction(PrivacySettingsAction.ShareLocationChanged(true))
        assertTrue(viewModel.uiState.value.shareLocation)
        assertEquals("Required", viewModel.uiState.value.locationPermission)
    }
}
