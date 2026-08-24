package com.example.lingolens.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.lingolens.feature.learn.LearnRoute
import com.example.lingolens.feature.learn.detail.VocabularyDetailRoute
import com.example.lingolens.feature.learn.notebook.NotebookRoute
import com.example.lingolens.feature.learn.quiz.QuizResultScreen
import com.example.lingolens.feature.learn.quiz.QuizRoute
import com.example.lingolens.feature.learn.review.ReviewRoute
import com.example.lingolens.feature.profile.ProfileRoute
import com.example.lingolens.feature.profile.notification.NotificationSettingsRoute
import com.example.lingolens.feature.profile.privacy.PrivacySettingsRoute

@Composable
fun LingoLensApp() {
    val backStack = rememberNavBackStack(Learn)
    val current = backStack.lastOrNull()
    val showBottomBar = current == Learn || current == Profile

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = current == Learn,
                        onClick = { backStack.openRoot(Learn) },
                        icon = { Icon(Icons.Outlined.School, contentDescription = null) },
                        label = { Text("Learn") },
                    )
                    NavigationBarItem(
                        selected = current == Profile,
                        onClick = { backStack.openRoot(Profile) },
                        icon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                        label = { Text("Profile") },
                    )
                }
            }
        },
    ) { padding ->
        NavDisplay(
            modifier = Modifier.padding(padding),
            backStack = backStack,
            onBack = { if (backStack.size > 1) backStack.removeLastOrNull() },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = entryProvider {
                entry<Learn> {
                    LearnRoute(
                        onOpenNotebook = { backStack.add(Notebook) },
                        onStartReview = { backStack.add(Review) },
                        onStartQuiz = { backStack.add(Quiz) },
                    )
                }
                entry<Notebook> {
                    NotebookRoute(
                        onBack = { backStack.removeLastOrNull() },
                        onOpenWord = { wordId -> backStack.add(VocabularyDetail(wordId)) },
                    )
                }
                entry<VocabularyDetail> { key ->
                    VocabularyDetailRoute(wordId = key.wordId, onBack = { backStack.removeLastOrNull() })
                }
                entry<Review> {
                    ReviewRoute(onBack = { backStack.removeLastOrNull() })
                }
                entry<Quiz> {
                    QuizRoute(
                        onBack = { backStack.removeLastOrNull() },
                        onFinished = { result -> backStack.add(QuizResult(result.score, result.total)) },
                    )
                }
                entry<QuizResult> { key ->
                    QuizResultScreen(
                        score = key.score,
                        total = key.total,
                        onReviewAnswers = { backStack.removeLastOrNull() },
                        onBackToLearn = { backStack.openRoot(Learn) },
                    )
                }
                entry<Profile> {
                    ProfileRoute(
                        onOpenMyWords = { backStack.add(Notebook) },
                        onOpenNotifications = { backStack.add(NotificationSettings) },
                        onOpenPrivacy = { backStack.add(PrivacySettings) },
                    )
                }
                entry<NotificationSettings> {
                    NotificationSettingsRoute(onBack = { backStack.removeLastOrNull() })
                }
                entry<PrivacySettings> {
                    PrivacySettingsRoute(onBack = { backStack.removeLastOrNull() })
                }
            },
        )
    }
}

private fun NavBackStack<androidx.navigation3.runtime.NavKey>.openRoot(destination: LingoLensDestination) {
    if (lastOrNull() == destination && size == 1) return
    clear()
    add(destination)
}
