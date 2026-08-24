package com.example.lingolens.feature.learn.review

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lingolens.ui.components.LingoLensCard
import com.example.lingolens.ui.theme.LingoLensTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(state: ReviewUiState, onAction: (ReviewAction) -> Unit, modifier: Modifier = Modifier) {
    Scaffold(modifier, topBar = { TopAppBar(title = { Text("Review") }, navigationIcon = { IconButton({ onAction(ReviewAction.Back) }) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back") } }) }) { insets ->
        Column(Modifier.fillMaxSize().padding(insets).padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("${state.currentIndex + 1} / ${state.total}", fontWeight = FontWeight.SemiBold); Text("Keep going!", color = MaterialTheme.colorScheme.primary) }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(progress = { (state.currentIndex + 1) / state.total.toFloat() }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(32.dp))
            LingoLensCard(
                modifier = Modifier.fillMaxWidth().weight(1f).clickable(enabled = !state.isRevealed) { onAction(ReviewAction.Reveal) },
            ) {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text(state.word, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text(state.pronunciation, color = MaterialTheme.colorScheme.primary)
                    IconButton({ onAction(ReviewAction.PlayPronunciation) }) { Icon(Icons.AutoMirrored.Outlined.VolumeUp, "Pronounce") }
                    if (state.isRevealed) {
                        Spacer(Modifier.height(24.dp)); Text(state.meaning, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center); Spacer(Modifier.height(16.dp)); Text(state.example, fontStyle = FontStyle.Italic, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                    } else { Spacer(Modifier.height(24.dp)); Text("Tap to reveal", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                }
            }
            Spacer(Modifier.height(24.dp))
            if (state.isRevealed) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ReviewRating.entries.forEach { rating ->
                        val emphasized = rating == ReviewRating.Good
                        if (emphasized) Button({ onAction(ReviewAction.Rate(rating)) }, Modifier.weight(1f)) { Text(rating.name) }
                        else OutlinedButton({ onAction(ReviewAction.Rate(rating)) }, Modifier.weight(1f), contentPadding = ButtonDefaults.ContentPadding) { Text(rating.name) }
                    }
                }
            } else Button({ onAction(ReviewAction.Reveal) }, Modifier.fillMaxWidth()) { Text("Reveal answer") }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ReviewPreview() { LingoLensTheme(darkTheme = false) { ReviewScreen(ReviewUiState(isRevealed = true), {}) } }
