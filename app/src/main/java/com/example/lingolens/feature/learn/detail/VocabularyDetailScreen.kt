package com.example.lingolens.feature.learn.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lingolens.feature.learn.notebook.MasteryLevel
import com.example.lingolens.ui.components.LingoLensCard
import com.example.lingolens.ui.theme.LingoLensTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyDetailScreen(state: VocabularyDetailUiState, onAction: (VocabularyDetailAction) -> Unit, modifier: Modifier = Modifier) {
    Scaffold(modifier, topBar = { TopAppBar(title = { Text("Word details") }, navigationIcon = { IconButton({ onAction(VocabularyDetailAction.Back) }) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back") } }, actions = { IconButton({ onAction(VocabularyDetailAction.Edit) }) { Icon(Icons.Outlined.Edit, "Edit") }; IconButton({ onAction(VocabularyDetailAction.ToggleFavorite) }) { Icon(if (state.isFavorite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder, "Favorite", tint = MaterialTheme.colorScheme.primary) } }) }) { insets ->
        LazyColumn(Modifier.fillMaxSize().padding(insets), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) { Text(state.word, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold); Text(state.pronunciation, color = MaterialTheme.colorScheme.primary); Text(state.partOfSpeech, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    IconButton({ onAction(VocabularyDetailAction.PlayPronunciation) }) { Icon(Icons.AutoMirrored.Outlined.VolumeUp, "Pronounce") }
                }
            }
            item { LingoLensCard { Text("Vietnamese meaning", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary); Text(state.meaning, style = MaterialTheme.typography.titleMedium) } }
            item { LingoLensCard { Text("Example", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary); Text(state.example, fontStyle = FontStyle.Italic) } }
            item { Text("Tags", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold); FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) { state.tags.forEach { AssistChip(onClick = {}, label = { Text(it) }) } } }
            item {
                Text("Mastery", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { MasteryLevel.entries.forEach { level -> Text(level.label, style = MaterialTheme.typography.labelSmall, color = if (level.ordinal <= state.mastery.ordinal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = if (level == state.mastery) FontWeight.Bold else FontWeight.Normal) } }
                androidx.compose.material3.LinearProgressIndicator(progress = { (state.mastery.ordinal + 1) / MasteryLevel.entries.size.toFloat() }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VocabularyDetailPreview() { LingoLensTheme(darkTheme = false) { VocabularyDetailScreen(VocabularyDetailUiState(), {}) } }
