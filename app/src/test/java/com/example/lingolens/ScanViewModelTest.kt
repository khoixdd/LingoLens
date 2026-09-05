package com.example.lingolens

import com.example.lingolens.domain.repository.VocabularyRepository
import com.example.lingolens.feature.scan.ScanAction
import com.example.lingolens.feature.scan.ScanEvent
import com.example.lingolens.feature.scan.ScanViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mockito.*

@OptIn(ExperimentalCoroutinesApi::class)
class ScanViewModelTest {
    @Before fun setup() { Dispatchers.setMain(UnconfinedTestDispatcher()) }
    @After fun teardown() { Dispatchers.resetMain() }

    @Test fun captureUpdatesBusyStateAndFeedbackTogether() {
        val vm = ScanViewModel(mock(VocabularyRepository::class.java))
        vm.onAction(ScanAction.Capture)
        assertTrue(vm.uiState.value.isScanning)
        assertEquals("Capturing image and extracting text...", vm.uiState.value.feedbackMessage)
    }

    @Test fun successfulCaptureClearsBusyStateAndKeepsNavigation() = runTest {
        val repository = mock(VocabularyRepository::class.java)
        `when`(repository.isWordDuplicate("adapt")).thenReturn(true)
        val vm = ScanViewModel(repository)
        val navigation = async { vm.events.first() }
        vm.onAction(ScanAction.Capture)
        vm.onAction(ScanAction.CaptureText(listOf("adapt")))
        assertFalse(vm.uiState.value.isScanning)
        assertEquals(ScanEvent.NavigateToLearn, navigation.await())
    }

    @Test fun rejectedTextClearsBusyStateForEveryValidationBranch() {
        val vm = ScanViewModel(mock(VocabularyRepository::class.java))
        listOf(emptyList(), List(11) { "word" }, listOf("a"), listOf("word1")).forEach { words ->
            vm.onAction(ScanAction.Capture)
            vm.onAction(ScanAction.CaptureText(words))
            assertFalse(vm.uiState.value.isScanning)
            assertNotNull(vm.uiState.value.feedbackMessage)
        }
    }

    @Test fun captureFailureClearsBusyStateAndShowsError() {
        val vm = ScanViewModel(mock(VocabularyRepository::class.java))
        vm.onAction(ScanAction.Capture)
        vm.onAction(ScanAction.ErrorOccurred("Capture failed"))
        assertFalse(vm.uiState.value.isScanning)
        assertEquals("Capture failed", vm.uiState.value.feedbackMessage)
    }
}
