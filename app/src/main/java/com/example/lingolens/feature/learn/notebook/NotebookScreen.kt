package com.example.lingolens.feature.learn.notebook

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lingolens.ui.components.EmptyState
import com.example.lingolens.ui.components.LingoLensCard
import com.example.lingolens.ui.theme.LingoLensTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotebookScreen(state: NotebookUiState, onAction: (NotebookAction) -> Unit, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text("Notebook") }, navigationIcon = { IconButton(onClick = { onAction(NotebookAction.Back) }) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back") } }) },
    ) { insets ->
        Column(Modifier.fillMaxSize().padding(insets)) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { onAction(NotebookAction.SearchChanged(it)) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                placeholder = { Text("Search words or meanings") },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                singleLine = true,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            )
            LazyRow(contentPadding = PaddingValues(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(NotebookFilter.entries) { filter -> FilterChip(selected = state.selectedFilter == filter, onClick = { onAction(NotebookAction.FilterSelected(filter)) }, label = { Text(filter.label) }) }
            }
            when (val content = state.content) {
                NotebookContentState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                NotebookContentState.Empty -> EmptyState("Your notebook is empty", "Saved words will appear here.")
                NotebookContentState.NoSearchResults -> EmptyState("No results", "Try another search or filter.")
                is NotebookContentState.Content -> LazyColumn(contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(content.words, key = { it.id }) { word -> VocabularyCard(word, { onAction(NotebookAction.WordSelected(word.id)) }, { onAction(NotebookAction.FavoriteToggled(word.id)) }) }
                }
            }
        }
    }
}

@Composable
private fun VocabularyCard(word: VocabularyItem, onClick: () -> Unit, onFavorite: () -> Unit) {
    androidx.compose.material3.Card(onClick = onClick, shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)) {
        Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(word.word, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(word.pronunciation, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                Text(word.meaning, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${word.partOfSpeech} • ${word.mastery.label}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onFavorite) { Icon(if (word.isFavorite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder, "Favorite", tint = MaterialTheme.colorScheme.primary) }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotebookScreenPreview() { LingoLensTheme(darkTheme = false) { NotebookScreen(NotebookUiState(content = NotebookContentState.Content(sampleVocabulary)), {}) } }
