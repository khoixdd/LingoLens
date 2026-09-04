package com.example.lingolens.navigation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.lingolens.feature.auth.WelcomeScreen
import com.example.lingolens.feature.auth.SplashScreen
import com.example.lingolens.feature.auth.login.LoginRoute
import com.example.lingolens.feature.auth.register.RegisterRoute
import com.example.lingolens.feature.community.CommunityRoute
import com.example.lingolens.feature.home.HomeRoute
import com.example.lingolens.feature.learn.LearnRoute
import com.example.lingolens.feature.learn.detail.VocabularyDetailRoute
import com.example.lingolens.feature.learn.notebook.NotebookRoute
import com.example.lingolens.feature.learn.quiz.QuizResultScreen
import com.example.lingolens.feature.learn.quiz.QuizRoute
import com.example.lingolens.feature.learn.review.ReviewRoute
import com.example.lingolens.feature.profile.ProfileRoute
import com.example.lingolens.feature.profile.notification.NotificationSettingsRoute
import com.example.lingolens.feature.profile.privacy.PrivacySettingsRoute
import com.example.lingolens.feature.profile.achievements.AchievementsRoute
import com.example.lingolens.feature.progress.StatisticsRoute
import com.example.lingolens.feature.scan.ScanRoute
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LingoLensApp() {
    val context = LocalContext.current
    val onboardingPreferences = remember {
        context.getSharedPreferences("lingolens_onboarding", android.content.Context.MODE_PRIVATE)
    }
    val backStack = rememberNavBackStack(Splash)
    val current = backStack.lastOrNull()
    val showBottomBar = current == Home || current == Scan || current == Learn ||
        current == Community || current == Profile

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                val dividerColor = androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant
                val itemColors = NavigationBarItemDefaults.colors(
                    selectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                    selectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                    indicatorColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                    unselectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                )
                NavigationBar(
                    modifier = Modifier.fillMaxWidth().height(64.dp).drawBehind {
                        drawLine(
                            color = dividerColor,
                            start = Offset.Zero,
                            end = Offset(size.width, 0f),
                            strokeWidth = 1.dp.toPx(),
                        )
                    },
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp,
                ) {
                    NavigationBarItem(
                        selected = current == Home,
                        onClick = { backStack.openRoot(Home) },
                        icon = { Icon(Icons.Outlined.Home, contentDescription = null) },
                        label = { Text("Home") },
                        colors = itemColors,
                    )
                    NavigationBarItem(
                        selected = current == Scan,
                        onClick = { backStack.openRoot(Scan) },
                        icon = { Icon(Icons.Outlined.CameraAlt, contentDescription = null) },
                        label = { Text("Scan") },
                        colors = itemColors,
                    )
                    NavigationBarItem(
                        selected = current == Learn,
                        onClick = { backStack.openRoot(Learn) },
                        icon = { Icon(Icons.Outlined.School, contentDescription = null) },
                        label = { Text("Learn") },
                        colors = itemColors,
                    )
                    NavigationBarItem(
                        selected = current == Community,
                        onClick = { backStack.openRoot(Community) },
                        icon = { Icon(Icons.Outlined.Groups, contentDescription = null) },
                        label = { Text("Community") },
                        colors = itemColors,
                    )
                    NavigationBarItem(
                        selected = current == Profile,
                        onClick = { backStack.openRoot(Profile) },
                        icon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                        label = { Text("Profile") },
                        colors = itemColors,
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
                entry<Splash> {
                    SplashScreen(
                        onFinished = {
                            val onboardingCompleted = onboardingPreferences.getBoolean("completed", false)
                            val currentUser = runCatching {
                                FirebaseAuth.getInstance().currentUser
                            }.getOrNull()
                            val nextDestination = when {
                                !onboardingCompleted -> Welcome
                                currentUser != null -> Home
                                else -> Login
                            }
                            backStack.openRoot(nextDestination)
                        },
                    )
                }
                entry<Home> {
                    HomeRoute(
                        onOpenLearn = { backStack.openRoot(Learn) },
                        onOpenReview = { backStack.add(Review) },
                        onOpenNotifications = { backStack.add(NotificationSettings) },
                    )
                }
                entry<Scan> {
                    ScanRoute(onClose = { backStack.openRoot(Home) }, openLearning = { backStack.openRoot(Learn) })
                }
                entry<Learn> {
                    LearnRoute(
                        onOpenNotebook = { backStack.add(Notebook) },
                        onStartReview = { backStack.add(Review) },
                        onStartQuiz = { backStack.add(Quiz(System.currentTimeMillis())) },
                        onOpenStatistics = { backStack.add(Statistics) },
                    )
                }
                entry<Community> {
                    CommunityRoute()
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
                        onReviewAnswers = { 
                            backStack.removeLastOrNull() 
                            backStack.removeLastOrNull()
                            backStack.add(Quiz(System.currentTimeMillis()))
                        },
                        onBackToLearn = { backStack.openRoot(Learn) },
                    )
                }
                entry<Profile> {
                    ProfileRoute(
                        onOpenMyWords = { backStack.add(Notebook) },
                        onOpenNotifications = { backStack.add(NotificationSettings) },
                        onOpenAchievements = { backStack.add(Achievements) },
                        onOpenStatistics = { backStack.add(Statistics) },
                        onOpenPrivacy = { backStack.add(PrivacySettings) },
                        onLogout = {
                            runCatching { FirebaseAuth.getInstance().signOut() }
                            backStack.openRoot(Login)
                        },
                    )
                }
                entry<NotificationSettings> {
                    NotificationSettingsRoute(onBack = { backStack.removeLastOrNull() })
                }
                entry<Statistics> {
                    StatisticsRoute(onBack = { backStack.removeLastOrNull() })
                }
                entry<Achievements> {
                    AchievementsRoute(onBack = { backStack.removeLastOrNull() })
                }
                entry<PrivacySettings> {
                    PrivacySettingsRoute(onBack = { backStack.removeLastOrNull() })
                }
                entry<Welcome> {
                    WelcomeScreen(
                        onGetStarted = {
                            onboardingPreferences.edit().putBoolean("completed", true).apply()
                            backStack.openRoot(Login)
                        },
                        onSignIn = {
                            onboardingPreferences.edit().putBoolean("completed", true).apply()
                            backStack.openRoot(Login)
                        },
                    )
                }
                entry<Login> {
                    LoginRoute(
                        onLoginSuccess = { backStack.openRoot(Home) },
                        onNavigateToRegister = { backStack.add(Register) },
                    )
                }
                entry<Register> {
                    RegisterRoute(
                        onRegisterSuccess = { backStack.openRoot(Home) },
                        onNavigateToLogin = { backStack.openRoot(Login) },
                    )
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
