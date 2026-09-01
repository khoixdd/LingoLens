package com.example.lingolens.feature.learn.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lingolens.domain.model.MasteryLevel
import com.example.lingolens.ui.theme.LingoLensTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun VocabularyDetailScreen(
    state: VocabularyDetailUiState,
    onAction: (VocabularyDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(state.isDeleted) {
        if (state.isDeleted) {
            onAction(VocabularyDetailAction.Back)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Word details") },
                navigationIcon = {
                    IconButton(onClick = { onAction(VocabularyDetailAction.Back) }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onAction(VocabularyDetailAction.Edit) }) {
                        Icon(Icons.Outlined.Edit, "Edit")
                    }
                    IconButton(onClick = { onAction(VocabularyDetailAction.Delete) }) {
                        Icon(Icons.Outlined.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                    IconButton(onClick = { onAction(VocabularyDetailAction.ToggleFavorite) }) {
                        Icon(
                            if (state.isFavorite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                            "Favorite",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
            )
        },
    ) { insets ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(insets),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(state.word, style = MaterialTheme.typography.headlineLarge)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (state.pronunciation.isNotBlank()) {
                            Text(
                                state.pronunciation,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f),
                            )
                        } else {
                            Spacer(Modifier.weight(1f))
                        }
                        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                            IconButton(onClick = { onAction(VocabularyDetailAction.PlayPronunciation) }) {
                                Icon(
                                    Icons.AutoMirrored.Outlined.VolumeUp,
                                    "Pronounce",
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }
                    if (state.partOfSpeech.isNotBlank()) {
                        AssistChip(onClick = {}, label = { Text(state.partOfSpeech) })
                    }
                }
            }
            item {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(Modifier.height(14.dp))
                Text(
                    "Vietnamese meaning",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.height(6.dp))
                Text(state.meaning, style = MaterialTheme.typography.titleLarge)
            }
            if (state.example.isNotBlank()) {
                item {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(Modifier.height(14.dp))
                    Text(
                        "Example",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        state.example,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (state.tags.isNotEmpty()) {
                item {
                    Text(
                        "Tags",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.tags.forEach { tag ->
                            AssistChip(onClick = {}, label = { Text(tag) })
                        }
                    }
                }
            }
            item {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Spacer(Modifier.height(14.dp))
                Text("Mastery", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    MasteryLevel.entries.forEach { level ->
                        Text(
                            level.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (level.ordinal <= state.mastery.ordinal) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            fontWeight = if (level == state.mastery) FontWeight.Bold else FontWeight.Normal,
                        )
                    }
                }
                LinearProgressIndicator(
                    progress = { (state.mastery.ordinal + 1) / MasteryLevel.entries.size.toFloat() },
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp).height(7.dp).clip(CircleShape),
                )
            }
        }
    }

    if (state.showEditDialog) {
        EditWordDialog(
            initialMeaning = state.meaning,
            initialExample = state.example,
            initialPronunciation = state.pronunciation,
            initialPartOfSpeech = state.partOfSpeech,
            onDismiss = { onAction(VocabularyDetailAction.ShowEditDialog(false)) },
            onSave = { meaning, example, pronunciation, partOfSpeech ->
                onAction(
                    VocabularyDetailAction.SaveEdit(
                        meaning = meaning,
                        example = example,
                        pronunciation = pronunciation,
                        partOfSpeech = partOfSpeech,
                    ),
                )
            },
        )
    }
}

@Composable
private fun EditWordDialog(
    initialMeaning: String,
    initialExample: String,
    initialPronunciation: String,
    initialPartOfSpeech: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit,
) {
    var meaning by remember { mutableStateOf(initialMeaning) }
    var example by remember { mutableStateOf(initialExample) }
    var pronunciation by remember { mutableStateOf(initialPronunciation) }
    var partOfSpeech by remember { mutableStateOf(initialPartOfSpeech) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Word") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = meaning,
                    onValueChange = { meaning = it },
                    label = { Text("Meaning *") },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = example,
                    onValueChange = { example = it },
                    label = { Text("Example sentence") },
                )
                OutlinedTextField(
                    value = pronunciation,
                    onValueChange = { pronunciation = it },
                    label = { Text("Pronunciation") },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = partOfSpeech,
                    onValueChange = { partOfSpeech = it },
                    label = { Text("Part of speech") },
                    singleLine = true,
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (meaning.isNotBlank()) {
                        onSave(meaning, example, pronunciation, partOfSpeech)
                    }
                },
                enabled = meaning.isNotBlank(),
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
private fun VocabularyDetailPreview() {
    LingoLensTheme(darkTheme = false) {
        VocabularyDetailScreen(
            VocabularyDetailUiState(
                word = "ubiquitous",
                pronunciation = "/juːˈbɪkwɪtəs/",
                partOfSpeech = "adjective",
                meaning = "phổ biến",
                example = "Smartphones have become ubiquitous.",
            ),
            {},
        )
    }
}
