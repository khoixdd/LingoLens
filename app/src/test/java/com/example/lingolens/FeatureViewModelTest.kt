package com.example.lingolens

import com.example.lingolens.core.common.TextToSpeechHelper
import com.example.lingolens.domain.model.AuthUser
import com.example.lingolens.domain.model.MasteryLevel
import com.example.lingolens.domain.model.UserProfile
import com.example.lingolens.domain.model.Vocabulary
import com.example.lingolens.domain.repository.AuthRepository
import com.example.lingolens.domain.repository.LocationRepository
import com.example.lingolens.domain.repository.UserLocation
import com.example.lingolens.domain.repository.UserRepository
import com.example.lingolens.domain.repository.VocabularyRepository
import com.example.lingolens.feature.community.CommunityAction
import com.example.lingolens.feature.community.CommunityViewModel
import com.example.lingolens.feature.community.LeaderboardPeriod
import com.example.lingolens.feature.home.HomeViewModel
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
import com.example.lingolens.feature.scan.ScanAction
import com.example.lingolens.feature.scan.ScanViewModel
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

class FakeAuthRepository : AuthRepository {
    private val userFlow = MutableStateFlow<AuthUser?>(
        AuthUser("uid_1", "Alex", "alex@example.com"),
    )

    override fun getCurrentUser(): AuthUser? = userFlow.value
    override fun observeAuthState(): Flow<AuthUser?> = userFlow
    override suspend fun loginWithEmail(email: String, password: String): Result<AuthUser> = Result.success(userFlow.value!!)
    override suspend fun registerWithEmail(username: String, email: String, password: String): Result<AuthUser> = Result.success(userFlow.value!!)
    override suspend fun loginWithGoogle(idToken: String): Result<AuthUser> = Result.success(userFlow.value!!)
    override suspend fun logout() { userFlow.value = null }
}

class FakeUserRepository : UserRepository {
    private val profileFlow = MutableStateFlow<UserProfile?>(
        UserProfile("uid_1", "Alex", "alex@example.com", level = 7, streakDays = 12, xp = 1560),
    )

    override fun observeUserProfile(uid: String): Flow<UserProfile?> = profileFlow
    override fun observeLeaderboard(): Flow<List<UserProfile>> = profileFlow.map { listOfNotNull(it) }
    override fun observeNearbyLearners(): Flow<List<UserProfile>> = profileFlow.map { listOfNotNull(it) }
    override suspend fun getUserProfile(uid: String): UserProfile? = profileFlow.value
    override suspend fun syncUserProfileOnLogin(user: AuthUser) {}
    override suspend fun addXp(uid: String, xpAmount: Int) {
        profileFlow.update {
            it?.copy(
                xp = it.xp + xpAmount,
                level = (it.xp + xpAmount) / 200 + 1,
            )
        }
    }
    override suspend fun syncTotalWords(uid: String, totalWords: Int) {
        profileFlow.update { it?.copy(totalWords = totalWords) }
    }
    override suspend fun updateUserLocation(uid: String, lat: Double, lng: Double, isSharing: Boolean) {
        profileFlow.update { it?.copy(latitude = lat, longitude = lng, isSharingLocation = isSharing) }
    }
}

class FakeLocationRepository : LocationRepository {
    override suspend fun getCurrentLocation(): UserLocation = UserLocation(10.762622, 106.682221)
    override fun hasLocationPermission(): Boolean = true
}

@OptIn(ExperimentalCoroutinesApi::class)
class FeatureViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val ttsHelper = mock(TextToSpeechHelper::class.java)
    private val authRepository: AuthRepository = FakeAuthRepository()
    private val userRepository: UserRepository = FakeUserRepository()
    private val locationRepository: LocationRepository = FakeLocationRepository()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun homeViewModelExposesLiveStatsFromRepositories() = runTest(testDispatcher) {
        val fakeVocabRepo: VocabularyRepository = FakeVocabularyRepository()
        val viewModel = HomeViewModel(
            authRepository,
            userRepository,
            fakeVocabRepo,
        )
        backgroundScope.launch { viewModel.uiState.collect {} }

        assertEquals("Alex", viewModel.uiState.value.name)
        assertEquals(12, viewModel.uiState.value.streakDays)
        assertEquals(7, viewModel.uiState.value.level)
        assertEquals(1, viewModel.uiState.value.reviewWordsDue)
    }

    @Test
    fun communityViewModelExposesLeaderboardState() = runTest(testDispatcher) {
        val viewModel = CommunityViewModel(
            authRepository,
            userRepository,
        )
        backgroundScope.launch { viewModel.uiState.collect {} }

        assertEquals(LeaderboardPeriod.ThisWeek, viewModel.uiState.value.selectedPeriod)
        assertEquals("Alex", viewModel.uiState.value.leaderboard.first { it.isCurrentUser }.name)

        viewModel.onAction(CommunityAction.SelectPeriod(LeaderboardPeriod.AllTime))
        assertEquals(LeaderboardPeriod.AllTime, viewModel.uiState.value.selectedPeriod)
    }

    @Test
    fun scanInteractionsOnlyUpdateLocalPlaceholderState() {
        val fakeVocabRepo = FakeVocabularyRepository()
        val viewModel = ScanViewModel(fakeVocabRepo)
        viewModel.onAction(ScanAction.ToggleFlash)
        assertTrue(viewModel.uiState.value.isFlashEnabled)

        viewModel.onAction(ScanAction.Capture)
        assertNotNull(viewModel.uiState.value.feedbackMessage)

        viewModel.onAction(ScanAction.DismissFeedback)
        assertFalse(viewModel.uiState.value.feedbackMessage != null)
    }

    @Test
    fun notebookSearchExposesNoResultsState() = runTest(testDispatcher) {
        val fakeVocabRepo: VocabularyRepository = FakeVocabularyRepository()
        val viewModel = NotebookViewModel(
            fakeVocabRepo,
            ttsHelper,
        )
        backgroundScope.launch {
            viewModel.uiState.collect {}
        }

        viewModel.onAction(NotebookAction.SearchChanged("not in notebook"))

        assertTrue(viewModel.uiState.value.content is NotebookContentState.NoSearchResults)
    }

    @Test
    fun reviewRevealAndRatingAdvanceTheCard() = runTest(testDispatcher) {
        val fakeVocabRepo: VocabularyRepository = FakeVocabularyRepository()
        val viewModel = ReviewViewModel(
            fakeVocabRepo,
            ttsHelper,
        )

        viewModel.onAction(ReviewAction.Reveal)
        assertTrue(viewModel.uiState.value.isRevealed)

        viewModel.onAction(ReviewAction.Rate(ReviewRating.Good))
        assertTrue(viewModel.uiState.value.isCompleted)

        val updatedWord = fakeVocabRepo.getWordByText("ubiquitous")
        assertNotNull(updatedWord)
        assertEquals(MasteryLevel.Familiar, updatedWord?.masteryLevel)
        assertTrue((updatedWord?.nextReviewAt ?: 0) > System.currentTimeMillis())
    }

    @Test
    fun duplicateWordPreventionUpdatesExistingWord() = runTest(testDispatcher) {
        val fakeVocabRepo: VocabularyRepository = FakeVocabularyRepository()
        assertTrue(fakeVocabRepo.isWordDuplicate("UBIQUITOUS"))

        val duplicateInput = Vocabulary(
            id = "new_id",
            word = "ubiquitous",
            meaning = "phổ biến ở khắp mọi nơi",
        )
        fakeVocabRepo.addVocabulary(duplicateInput)

        val words = fakeVocabRepo.getAllVocabulary()
        var list: List<Vocabulary> = emptyList()
        val job = backgroundScope.launch { words.collect { list = it } }

        assertEquals(1, list.size)
        assertEquals("phổ biến ở khắp mọi nơi", list.first().meaning)
        job.cancel()
    }

    @Test
    fun quizShowsCorrectFeedbackBeforeMovingNext() = runTest(testDispatcher) {
        val fakeVocabRepo: VocabularyRepository = FakeVocabularyRepository()
        val viewModel = QuizViewModel(
            fakeVocabRepo,
            authRepository,
            userRepository,
        )

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
        val viewModel = PrivacySettingsViewModel(locationRepository, authRepository, userRepository)
        viewModel.onAction(PrivacySettingsAction.ShareLocationChanged(true))
        assertTrue(viewModel.uiState.value.shareLocation)
        assertEquals("Granted", viewModel.uiState.value.locationPermission)
    }
}
