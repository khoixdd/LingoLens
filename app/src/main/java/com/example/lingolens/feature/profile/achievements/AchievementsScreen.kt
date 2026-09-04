package com.example.lingolens.feature.profile.achievements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.lingolens.ui.components.LingoLensCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(state: AchievementsUiState, onAction: (AchievementsAction) -> Unit) {
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Achievements") },
            navigationIcon = {
                IconButton({ onAction(AchievementsAction.Back) }) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back")
                }
            },
        )
    }) { insets ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(insets), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                Modifier.fillMaxSize().padding(insets),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    val unlocked = state.achievements.count { it.isUnlocked }
                    LingoLensCard(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                        Row(Modifier.fillMaxWidth()) {
                            Text("Your progress", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                            Text("$unlocked / ${state.achievements.size}", color = MaterialTheme.colorScheme.primary)
                        }
                        LinearProgressIndicator(
                            progress = { if (state.achievements.isEmpty()) 0f else unlocked.toFloat() / state.achievements.size },
                            modifier = Modifier.fillMaxWidth().padding(top = 10.dp).height(6.dp).clip(CircleShape),
                        )
                    }
                }
                items(state.achievements.size) { index ->
                    val item = state.achievements[index]
                    LingoLensCard(
                        containerColor = if (item.isUnlocked) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant,
                        contentPadding = PaddingValues(16.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = if (item.isUnlocked) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant,
                            ) {
                                Icon(
                                    if (item.isUnlocked) Icons.Outlined.EmojiEvents else Icons.Outlined.Lock,
                                    contentDescription = null,
                                    modifier = Modifier.padding(10.dp).size(22.dp),
                                    tint = if (item.isUnlocked) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Column(Modifier.fillMaxWidth().padding(start = 14.dp)) {
                                Text(item.definition.name, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    item.definition.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
