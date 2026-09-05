package com.example.lingolens

import com.example.lingolens.domain.model.*
import com.example.lingolens.domain.repository.*
import com.example.lingolens.feature.profile.edit.*
import com.example.lingolens.feature.celebration.CelebrationViewModel
import com.example.lingolens.feature.learn.quiz.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*
import org.mockito.Mockito.*

@OptIn(ExperimentalCoroutinesApi::class)
class GamificationPolishTest {
    private val dispatcher = UnconfinedTestDispatcher()
    @Before fun setup() { Dispatchers.setMain(dispatcher) }
    @After fun teardown() { Dispatchers.resetMain() }

    private val user = AuthUser("user", "Original", "test@example.com")
    private fun auth() = mock(AuthRepository::class.java).also {
        `when`(it.getCurrentUser()).thenReturn(user)
        `when`(it.observeAuthState()).thenReturn(flowOf(user))
    }

    @Test fun oldAndUnknownAvatarsUseLeaf() {
        assertEquals("leaf", UserProfile().avatarId)
        assertEquals("leaf", ProfilePersonalization.avatarOrDefault(null))
        assertEquals("leaf", ProfilePersonalization.avatarOrDefault("missing"))
        assertEquals("rocket", ProfilePersonalization.avatarOrDefault("rocket"))
    }

    @Test fun profileRejectsBlankAndOverlongNames() = runTest {
        val users = mock(UserRepository::class.java)
        `when`(users.getUserProfile(user.uid)).thenReturn(UserProfile(username = "Original"))
        val vm = EditProfileViewModel(auth(), users)
        vm.onAction(EditProfileAction.NameChanged("   "))
        vm.onAction(EditProfileAction.Save)
        assertEquals("Enter a display name.", vm.uiState.value.nameError)
        vm.onAction(EditProfileAction.NameChanged("x".repeat(41)))
        vm.onAction(EditProfileAction.Save)
        assertNotNull(vm.uiState.value.nameError)
        verify(users, never()).updatePersonalization(anyString(), anyString(), anyString())
    }

    @Test fun savesNameAndAvatarOnceAndDoesNotInvokeProgressWrites() = runTest {
        val users = mock(UserRepository::class.java)
        `when`(users.getUserProfile(user.uid)).thenReturn(UserProfile(username = "Original", xp = 420, streakDays = 4))
        val vm = EditProfileViewModel(auth(), users)
        vm.onAction(EditProfileAction.NameChanged("  New Name  "))
        vm.onAction(EditProfileAction.AvatarSelected("planet"))
        vm.onAction(EditProfileAction.Save)
        vm.onAction(EditProfileAction.Save)
        assertTrue(vm.uiState.value.isSaved)
        verify(users, times(1)).updatePersonalization(user.uid, "New Name", "planet")
        verify(users, never()).addXp(anyString(), anyInt())
        verify(users, never()).syncTotalWords(anyString(), anyInt())
    }

    @Test fun saveFailureKeepsDraftAndAllowsRetry() = runTest {
        val users = mock(UserRepository::class.java)
        `when`(users.getUserProfile(user.uid)).thenReturn(UserProfile(username = "Original"))
        doThrow(IllegalStateException("offline")).doAnswer { null }
            .`when`(users).updatePersonalization(user.uid, "New Name", "leaf")
        val vm = EditProfileViewModel(auth(), users)
        vm.onAction(EditProfileAction.NameChanged("New Name"))
        vm.onAction(EditProfileAction.Save)
        assertNotNull(vm.uiState.value.error)
        assertEquals("New Name", vm.uiState.value.name)
        assertFalse(vm.uiState.value.isSaving)
        vm.onAction(EditProfileAction.Save)
        assertTrue(vm.uiState.value.isSaved)
    }

    @Test fun changedAccountCannotSaveDraft() = runTest {
        val auth = auth()
        val users = mock(UserRepository::class.java)
        `when`(users.getUserProfile(user.uid)).thenReturn(UserProfile(username = "Original"))
        val vm = EditProfileViewModel(auth, users)
        `when`(auth.getCurrentUser()).thenReturn(null)
        vm.onAction(EditProfileAction.Save)
        assertFalse(vm.uiState.value.isSaved)
        verify(users, never()).updatePersonalization(anyString(), anyString(), anyString())
    }

    @Test fun achievementQueueIgnoresDuplicatesAndOtherAccounts() = runTest {
        val events = MutableSharedFlow<AchievementUnlock>()
        val progress = mock(LearningProgressRepository::class.java)
        `when`(progress.achievementUnlocks).thenReturn(events)
        val vm = CelebrationViewModel(progress, auth())
        val event = AchievementUnlock(user.uid, "rising_star")
        events.emit(event)
        events.emit(event)
        events.emit(AchievementUnlock("other", "on_fire"))
        assertEquals(listOf(event), vm.uiState.value)
        vm.dismiss(event)
        events.emit(event)
        assertTrue(vm.uiState.value.isEmpty())
    }

    @Test fun quizCompletionStillAwardsExistingXpExactlyOnce() = runTest {
        val vocabulary = mock(VocabularyRepository::class.java)
        `when`(vocabulary.getAllVocabulary()).thenReturn(flowOf(listOf(Vocabulary("word", "adapt", "Change to suit conditions"))))
        val progress = mock(LearningProgressRepository::class.java)
        val vm = QuizViewModel(vocabulary, progress)
        vm.onAction(QuizAction.SelectAnswer(vm.uiState.value.correctIndex))
        vm.onAction(QuizAction.CheckAnswer)
        val completion = vm.onAction(QuizAction.Next)
        assertEquals(1, completion?.score)
        assertNull(vm.onAction(QuizAction.Next))
        verify(progress, times(1)).awardQuizXp(30)
    }

    @Test fun repositoryEmitsOnlyCommittedNewUnlocksEvenWithNotificationsDisabled() = runTest {
        val vocabulary = mock(VocabularyRepository::class.java)
        `when`(vocabulary.getAllVocabulary()).thenReturn(flowOf(emptyList()))
        val users = mock(UserRepository::class.java)
        val daily = mock(DailyActivityRepository::class.java)
        `when`(daily.uniqueWords(anyLong())).thenReturn(0)
        val settings = mock(NotificationSettingsRepository::class.java)
        `when`(settings.getSettings()).thenReturn(NotificationSettings(achievementNotificationsEnabled = false))
        val notifications = mock(com.example.lingolens.notification.NotificationHelper::class.java)
        val unlocked = setOf("rising_star")
        val first = GamificationUpdate(100, 1, 0, null, unlocked, unlocked)
        `when`(users.updateGamification(user.uid, null, 0, 0, 0)).thenReturn(first, first.copy(newlyUnlockedAchievementIds = emptySet()))
        val repository = com.example.lingolens.data.repository.LearningProgressRepositoryImpl(
            auth(), daily, vocabulary, users, settings, notifications)
        val events = mutableListOf<AchievementUnlock>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { repository.achievementUnlocks.toList(events) }
        repository.evaluateCurrentAchievements()
        repository.evaluateCurrentAchievements()
        assertEquals(listOf(AchievementUnlock(user.uid, "rising_star")), events)
        verifyNoInteractions(notifications)
    }
}
