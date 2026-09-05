package com.example.lingolens.feature.celebration

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lingolens.domain.gamification.AchievementDefinitions
import com.example.lingolens.domain.model.AchievementUnlock
import com.example.lingolens.ui.components.CelebrationOverlay
import com.example.lingolens.ui.components.achievementVisual
import kotlinx.coroutines.delay

@Composable
fun CelebrationRoute(viewModel: CelebrationViewModel = hiltViewModel()) {
    val events by viewModel.uiState.collectAsStateWithLifecycle()
    events.firstOrNull()?.let { event ->
        key(event) { AchievementCelebration(event, onFinished = { viewModel.dismiss(event) }) }
    }
}

/** Pure presentation, with no input-consuming backdrop. */
@Composable
fun AchievementCelebration(event: AchievementUnlock, onFinished: () -> Unit) {
    val definition = AchievementDefinitions.all.firstOrNull { it.id == event.achievementId }
    val latestFinished by rememberUpdatedState(onFinished)
    var presented by rememberSaveable(event.userId, event.achievementId) { mutableStateOf(false) }
    val show = remember { !presented }
    LaunchedEffect(event) {
        if (show) {
            presented = true
            delay(2400)
        }
        latestFinished()
    }
    if (show && definition != null) {
        val visual = achievementVisual(definition.id)
        Box(Modifier.fillMaxSize()) {
            CelebrationOverlay(visible = true)
            Surface(Modifier.align(Alignment.TopCenter).statusBarsPadding().padding(16.dp)
                .semantics { liveRegion = LiveRegionMode.Polite },
                shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.primaryContainer,
                shadowElevation = 4.dp) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(visual.icon, null, Modifier.size(34.dp), tint = visual.accent)
                    Column {
                        Text("Achievement Unlocked!", style = MaterialTheme.typography.labelLarge)
                        Text(definition.name, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}
