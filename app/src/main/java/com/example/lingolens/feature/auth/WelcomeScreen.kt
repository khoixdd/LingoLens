package com.example.lingolens.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.ShowChart
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.lingolens.ui.components.BrandDecoratedBackground
import com.example.lingolens.ui.components.LingoLensCard
import com.example.lingolens.ui.components.LingoLensPrimaryButton
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(Unit) {
        delay(1_250)
        onFinished()
    }
    BrandDecoratedBackground(modifier) {
        SplashContent()
    }
}

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    onSignIn: () -> Unit = onGetStarted,
    modifier: Modifier = Modifier,
) {
    var page by remember { mutableIntStateOf(1) }
    BrandDecoratedBackground(modifier) {
        when (page) {
            1 -> OnboardingPage(
                title = "Learn from\nthe world\naround you",
                visual = { ScanLearningVisual() },
                description = "Scan, translate, save and master\nnew vocabulary.",
                page = page,
                buttonText = "Next",
                onNext = { page = 2 },
                onSignIn = onSignIn,
            )
            2 -> OnboardingPage(
                title = "Personalize\nyour learning\njourney",
                visual = { FeatureStack() },
                description = "",
                page = page,
                buttonText = "Next",
                onNext = { page = 3 },
                onSignIn = onSignIn,
            )
            else -> OnboardingPage(
                title = "All in one\nlearning\ncompanion",
                visual = { CompanionVisual() },
                description = "Everything you need\nto learn, practice\nand improve.",
                page = page,
                buttonText = "Get Started",
                onNext = onGetStarted,
                onSignIn = onSignIn,
            )
        }
    }
}

@Composable
private fun SplashContent() {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Lingo\nLens", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
        Text("INNOVATIVE LEARNING", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, letterSpacing = androidx.compose.ui.unit.TextUnit.Unspecified)
        Spacer(Modifier.height(64.dp))
        Surface(Modifier.size(132.dp), CircleShape, MaterialTheme.colorScheme.primary) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.Translate, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(64.dp))
            }
        }
    }
}

@Composable
private fun OnboardingPage(
    title: String,
    visual: @Composable () -> Unit,
    description: String,
    page: Int,
    buttonText: String,
    onNext: () -> Unit,
    onSignIn: () -> Unit,
) {
    Column(Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 30.dp)) {
        Text(title, style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.onBackground)
        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) { visual() }
        if (description.isNotBlank()) {
            Text(description, Modifier.fillMaxWidth(), textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(22.dp))
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            repeat(3) { index ->
                Surface(
                    Modifier.padding(horizontal = 4.dp).size(if (index == page - 1) 8.dp else 6.dp),
                    CircleShape,
                    if (index == page - 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                ) {}
            }
        }
        Spacer(Modifier.height(20.dp))
        LingoLensPrimaryButton(buttonText, onNext)
        TextButton(onClick = onSignIn, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Already a learner? Sign in", color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun ScanLearningVisual() {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
        Surface(Modifier.size(width = 96.dp, height = 176.dp), MaterialTheme.shapes.extraLarge, MaterialTheme.colorScheme.primaryContainer, shadowElevation = 3.dp) {
            Box(contentAlignment = Alignment.Center) { Icon(Icons.Outlined.CameraAlt, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(44.dp)) }
        }
        LingoLensCard(Modifier.padding(start = 8.dp).size(width = 112.dp, height = 146.dp)) {
            Icon(Icons.Outlined.Translate, null, tint = MaterialTheme.colorScheme.primary)
            Text("Hello", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            Text("xin chào", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun FeatureStack() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FeatureCard(Icons.Outlined.Psychology, "Smart review", "Spaced repetition that works")
        FeatureCard(Icons.Outlined.ShowChart, "Track progress", "Build streaks and earn XP")
        FeatureCard(Icons.Outlined.EmojiEvents, "Stay motivated", "Achievements and fun challenges")
    }
}

@Composable
private fun FeatureCard(icon: ImageVector, title: String, description: String) {
    LingoLensCard(Modifier.fillMaxWidth(), contentPadding = androidx.compose.foundation.layout.PaddingValues(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(Modifier.size(42.dp), CircleShape, MaterialTheme.colorScheme.primaryContainer) {
                Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp)) }
            }
            Column(Modifier.padding(start = 12.dp)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun CompanionVisual() {
    Box(Modifier.size(230.dp), contentAlignment = Alignment.Center) {
        Surface(Modifier.size(112.dp), CircleShape, MaterialTheme.colorScheme.primaryContainer) {
            Box(contentAlignment = Alignment.Center) { Icon(Icons.Outlined.Language, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(66.dp)) }
        }
        OrbitIcon(Icons.Outlined.CameraAlt, Alignment.TopStart)
        OrbitIcon(Icons.Outlined.AutoStories, Alignment.TopEnd)
        OrbitIcon(Icons.Outlined.EmojiEvents, Alignment.BottomStart)
        OrbitIcon(Icons.Outlined.Groups, Alignment.BottomEnd)
    }
}

@Composable
private fun BoxScope.OrbitIcon(icon: ImageVector, alignment: Alignment) {
    Surface(Modifier.align(alignment).size(52.dp), CircleShape, MaterialTheme.colorScheme.surface, shadowElevation = 2.dp) {
        Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = MaterialTheme.colorScheme.primary) }
    }
}
