package com.example.lingolens.feature.learn.review

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lingolens.ui.components.EmptyState
import com.example.lingolens.ui.components.LingoLensCard
import com.example.lingolens.ui.theme.LingoLensTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    state: ReviewUiState,
    onAction: (ReviewAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Review") },
                navigationIcon = {
                    IconButton(onClick = { onAction(ReviewAction.Back) }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back")
                    }
                },
            )
        },
    ) { insets ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(insets),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
            state.isEmpty -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(insets),
                    contentAlignment = Alignment.Center,
                ) {
                    EmptyState(
                        title = "No words to review",
                        message = "Add more words in your Notebook to start reviewing!",
                    )
                }
            }
            state.isCompleted -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(insets)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        Icons.Outlined.EmojiEvents,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                    )
                    Spacer(Modifier.height(14.dp))
                    Text("Excellent!", style = MaterialTheme.typography.headlineLarge)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "You've completed your review session.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(32.dp))
                    Button(
                        onClick = { onAction(ReviewAction.Back) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Back to Learn")
                    }
                }
            }
            else -> {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(insets)
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            "${state.currentIndex + 1} / ${state.total}",
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text("Keep going!", color = MaterialTheme.colorScheme.primary)
                    }
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = {
                            if (state.total > 0) (state.currentIndex + 1) / state.total.toFloat() else 0f
                        },
                        modifier = Modifier.fillMaxWidth().height(7.dp).clip(CircleShape),
                    )
                    Spacer(Modifier.height(24.dp))
                    LingoLensCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clickable(enabled = !state.isRevealed) {
                                onAction(ReviewAction.Reveal)
                            },
                        contentPadding = PaddingValues(20.dp),
                    ) {
                        Column(
                            Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                state.word,
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                            )
                            if (state.pronunciation.isNotBlank()) {
                                Text(
                                    state.pronunciation,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                            IconButton(onClick = { onAction(ReviewAction.PlayPronunciation) }) {
                                Icon(Icons.AutoMirrored.Outlined.VolumeUp, "Pronounce")
                            }
                            if (state.isRevealed) {
                                Spacer(Modifier.height(24.dp))
                                Text(
                                    state.meaning,
                                    style = MaterialTheme.typography.titleMedium,
                                    textAlign = TextAlign.Center,
                                )
                                if (state.example.isNotBlank()) {
                                    Spacer(Modifier.height(16.dp))
                                    Text(
                                        state.example,
                                        fontStyle = FontStyle.Italic,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center,
                                    )
                                }
                            } else {
                                Spacer(Modifier.height(24.dp))
                                Text(
                                    "Tap to reveal",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(18.dp))
                    if (state.isRevealed) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            ReviewRating.entries.forEach { rating ->
                                val containerColor = when (rating) {
                                    ReviewRating.Again -> MaterialTheme.colorScheme.error
                                    ReviewRating.Hard -> MaterialTheme.colorScheme.tertiary
                                    ReviewRating.Good -> MaterialTheme.colorScheme.primary
                                    ReviewRating.Easy -> MaterialTheme.colorScheme.secondary
                                }
                                val contentColor = when (rating) {
                                    ReviewRating.Again -> MaterialTheme.colorScheme.onError
                                    ReviewRating.Hard -> MaterialTheme.colorScheme.onTertiary
                                    ReviewRating.Good -> MaterialTheme.colorScheme.onPrimary
                                    ReviewRating.Easy -> MaterialTheme.colorScheme.onSecondary
                                }
                                Button(
                                    onClick = { onAction(ReviewAction.Rate(rating)) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = containerColor,
                                        contentColor = contentColor,
                                    ),
                                    contentPadding = PaddingValues(horizontal = 3.dp, vertical = 9.dp),
                                ) {
                                    Text(rating.name, style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }
                    } else {
                        Button(
                            onClick = { onAction(ReviewAction.Reveal) },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text("Reveal answer")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReviewPreview() {
    LingoLensTheme(darkTheme = false) {
        ReviewScreen(
            ReviewUiState(
                isLoading = false,
                word = "ubiquitous",
                pronunciation = "/juːˈbɪkwɪtəs/",
                meaning = "phổ biến",
                isRevealed = true,
                total = 5,
            ),
            {},
        )
    }
}
