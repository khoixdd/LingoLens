package com.example.lingolens.feature.learn.notebook

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lingolens.domain.model.MasteryLevel
import com.example.lingolens.domain.model.Vocabulary
import com.example.lingolens.ui.components.EmptyState
import com.example.lingolens.ui.theme.LingoLensTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotebookScreen(
    state: NotebookUiState,
    onAction: (NotebookAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Notebook") },
                navigationIcon = {
                    IconButton(onClick = { onAction(NotebookAction.Back) }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAction(NotebookAction.ShowAddDialog(true)) },
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "Add Word")
            }
        },
    ) { insets ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(insets),
        ) {
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { onAction(NotebookAction.SearchChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("Search words...") },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                singleLine = true,
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                ),
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(NotebookFilter.entries) { filter ->
                    FilterChip(
                        selected = state.selectedFilter == filter,
                        onClick = { onAction(NotebookAction.FilterSelected(filter)) },
                        label = { Text(filter.label) },
                    )
                }
            }
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = {
                    val options = NotebookSortOption.entries
                    onAction(NotebookAction.SortSelected(options[(state.selectedSort.ordinal + 1) % options.size]))
                }) { Text("Sort: ${state.selectedSort.label}", style = MaterialTheme.typography.labelMedium) }
            }
            when (val content = state.content) {
                NotebookContentState.Loading -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
                NotebookContentState.Empty -> EmptyState(
                    "Your notebook is empty",
                    "Saved words will appear here. Tap + to add one!",
                )
                NotebookContentState.NoSearchResults -> EmptyState(
                    "No results",
                    "Try another search or filter.",
                )
                is NotebookContentState.Content -> LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(content.words, key = { it.id }) { word ->
                        VocabularyCard(
                            word = word,
                            onClick = { onAction(NotebookAction.WordSelected(word.id)) },
                            onFavorite = { onAction(NotebookAction.FavoriteToggled(word.id)) },
                            onDelete = { onAction(NotebookAction.DeleteWord(word.id)) },
                            onSpeak = { onAction(NotebookAction.PlayPronunciation(word.word)) },
                        )
                    }
                }
            }
        }
    }

    if (state.showAddDialog) {
        AddWordDialog(
            onDismiss = { onAction(NotebookAction.ShowAddDialog(false)) },
            onAdd = { word, meaning, tag ->
                onAction(
                    NotebookAction.AddWord(
                        word = word,
                        meaning = meaning,
                        pronunciation = "",
                        partOfSpeech = "",
                        example = "",
                        tag = tag,
                    ),
                )
            },
        )
    }
}

@Composable
private fun VocabularyCard(
    word: Vocabulary,
    onClick: () -> Unit,
    onFavorite: () -> Unit,
    onDelete: () -> Unit,
    onSpeak: () -> Unit,
) {
    Card(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = null,
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        word.word,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    IconButton(onClick = onSpeak) {
                        Icon(
                            Icons.AutoMirrored.Outlined.VolumeUp,
                            contentDescription = "Speak",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
                if (word.pronunciation.isNotBlank()) {
                    Text(
                        word.pronunciation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Text(word.meaning, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    "${word.partOfSpeech.ifBlank { "word" }}  |  ${word.masteryLevel.label}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Row {
                IconButton(onClick = onFavorite) {
                    Icon(
                        if (word.isFavorite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                        "Favorite",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Outlined.Delete,
                        "Delete",
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}

@Composable
private fun AddWordDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, String) -> Unit,
) {
    var word by remember { mutableStateOf("") }
    var meaning by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Word") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = word,
                    onValueChange = { word = it },
                    label = { Text("Word *") },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = meaning,
                    onValueChange = { meaning = it },
                    label = { Text("Meaning *") },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = tag,
                    onValueChange = { tag = it },
                    label = { Text("Tag (optional)") },
                    placeholder = { Text("Technology, Travel...") },
                    singleLine = true,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (word.isNotBlank() && meaning.isNotBlank()) {
                        onAdd(word, meaning, tag)
                    }
                },
                enabled = word.isNotBlank() && meaning.isNotBlank(),
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun NotebookScreenPreview() {
    LingoLensTheme(darkTheme = false) {
        NotebookScreen(
            NotebookUiState(
                content = NotebookContentState.Content(
                    listOf(
                        Vocabulary(
                            id = "ubiquitous",
                            word = "ubiquitous",
                            meaning = "phổ biến",
                            pronunciation = "/juːˈbɪkwɪtəs/",
                            partOfSpeech = "adjective",
                            isFavorite = true,
                            masteryLevel = MasteryLevel.Learning,
                        ),
                    ),
                ),
            ),
            {},
        )
    }
}
