package com.example.lingolens.feature.translator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.CompareArrows
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lingolens.core.mlkit.TranslationModelState
import com.example.lingolens.core.mlkit.TranslationPair
import com.example.lingolens.ui.theme.LingoLensTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslatorScreen(
    state: TranslatorUiState,
    onAction: (TranslatorAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Translator")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { onAction(TranslatorAction.Back) }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back")
                    }
                },
            )
        },
    ) { insets ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(insets)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            DirectionSelector(
                direction = state.direction,
                onSwap = { onAction(TranslatorAction.SwapDirection) },
            )

            OutlinedTextField(
                value = state.sourceText,
                onValueChange = { onAction(TranslatorAction.UpdateInput(it)) },
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(
                        if (state.direction == TranslationPair.EnglishToVietnamese) {
                            "English text"
                        } else {
                            "Vietnamese text"
                        }
                    )
                },
                placeholder = {
                    Text(
                        if (state.direction == TranslationPair.EnglishToVietnamese) {
                            "Type English here"
                        } else {
                            "Type Vietnamese here"
                        }
                    )
                },
                minLines = 3,
                maxLines = 6,
                enabled = !state.isTranslating,
            )

            val activeModelState = when (state.direction) {
                TranslationPair.EnglishToVietnamese -> state.enViModelState
                TranslationPair.VietnameseToEnglish -> state.viEnModelState
            }
            ModelStatus(activeModelState)

            Button(
                onClick = { onAction(TranslatorAction.Translate) },
                enabled = state.sourceText.isNotBlank() && !state.isTranslating,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (state.isTranslating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Translating…")
                } else {
                    Text("Translate")
                }
            }

            state.result?.let { result ->
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(result, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
                }
            }

            state.errorMessage?.let { message ->
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 4.dp, end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            message,
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                        )
                        TextButton(onClick = { onAction(TranslatorAction.DismissError) }) {
                            Text("Dismiss")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DirectionSelector(
    direction: TranslationPair,
    onSwap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DirectionLabel(
            "EN",
            active = direction == TranslationPair.EnglishToVietnamese,
            modifier = Modifier.weight(1f),
        )
        IconButton(onClick = onSwap) {
            Icon(
                Icons.AutoMirrored.Outlined.CompareArrows,
                contentDescription = "Swap direction",
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        DirectionLabel(
            "VI",
            active = direction == TranslationPair.VietnameseToEnglish,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun DirectionLabel(
    text: String,
    active: Boolean,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = if (active) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Text(
            text,
            style = MaterialTheme.typography.titleMedium,
            color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 10.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}

@Composable
private fun ModelStatus(
    modelState: TranslationModelState,
    modifier: Modifier = Modifier,
) {
    when (modelState) {
        is TranslationModelState.Downloading -> {
            Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "Downloading translation model…",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                )
            }
        }
        is TranslationModelState.NotDownloaded -> {
            Text(
                "Translation model will be downloaded on first use.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = modifier,
            )
        }
        is TranslationModelState.Error -> {
            Text(
                modelState.message ?: "Could not download the translation model.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = modifier,
            )
        }
        is TranslationModelState.Downloaded -> {
            Spacer(modifier = modifier.height(0.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TranslatorScreenPreview() {
    LingoLensTheme(darkTheme = false) {
        TranslatorScreen(TranslatorUiState(), {})
    }
}