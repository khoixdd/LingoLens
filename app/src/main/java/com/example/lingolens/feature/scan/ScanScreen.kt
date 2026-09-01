package com.example.lingolens.feature.scan

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FlashOff
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lingolens.ui.theme.LingoLensTheme

@Composable
fun ScanScreen(
    state: ScanUiState,
    onAction: (ScanAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val previewColor = MaterialTheme.colorScheme.inverseSurface
    val previewContentColor = MaterialTheme.colorScheme.inverseOnSurface

    Box(
        modifier = modifier.fillMaxSize().background(previewColor),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                IconButton(onClick = { onAction(ScanAction.Close) }) {
                    Icon(Icons.Outlined.Close, contentDescription = "Close scan", tint = previewContentColor)
                }
                IconButton(onClick = { onAction(ScanAction.ToggleFlash) }) {
                    Icon(
                        imageVector = if (state.isFlashEnabled) Icons.Outlined.FlashOn else Icons.Outlined.FlashOff,
                        contentDescription = "Toggle flash",
                        tint = if (state.isFlashEnabled) MaterialTheme.colorScheme.primary else previewContentColor,
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ScanFrame(
                    modifier = Modifier.fillMaxWidth().aspectRatio(0.82f),
                    accentColor = MaterialTheme.colorScheme.primary,
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(0.72f).aspectRatio(0.78f),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        shadowElevation = 6.dp,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(18.dp),
                        ) {
                            Text(
                                text = "Point at the text",
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.titleMedium,
                            )
                            Text(
                                text = "Keep the words inside the frame",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 6.dp),
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.weight(1f))
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Surface(
                        onClick = { onAction(ScanAction.Capture) },
                        modifier = Modifier.size(76.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        border = BorderStroke(5.dp, previewContentColor),
                        shadowElevation = 8.dp,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Outlined.CameraAlt,
                                contentDescription = "Capture text",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(30.dp),
                            )
                        }
                    }
                }
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = { onAction(ScanAction.OpenGallery) }) {
                            Icon(Icons.Outlined.PhotoLibrary, contentDescription = "Open gallery", tint = previewContentColor)
                        }
                        Text("Gallery", color = previewContentColor, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }

        state.feedbackMessage?.let { message ->
            Snackbar(
                modifier = Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(20.dp),
                action = {
                    TextButton(onClick = { onAction(ScanAction.DismissFeedback) }) {
                        Text("Dismiss")
                    }
                },
            ) {
                Text(message)
            }
        }
    }
}

@Composable
private fun ScanFrame(
    accentColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier.drawBehind {
            val corner = 42.dp.toPx()
            val inset = 3.dp.toPx()
            val stroke = 4.dp.toPx()
            val right = size.width - inset
            val bottom = size.height - inset
            listOf(
                Offset(inset + corner, inset) to Offset(inset, inset),
                Offset(inset, inset) to Offset(inset, inset + corner),
                Offset(right - corner, inset) to Offset(right, inset),
                Offset(right, inset) to Offset(right, inset + corner),
                Offset(inset, bottom - corner) to Offset(inset, bottom),
                Offset(inset, bottom) to Offset(inset + corner, bottom),
                Offset(right - corner, bottom) to Offset(right, bottom),
                Offset(right, bottom - corner) to Offset(right, bottom),
            ).forEach { (start, end) ->
                drawLine(accentColor, start, end, strokeWidth = stroke, cap = StrokeCap.Round)
            }
        },
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun ScanScreenPreview() {
    LingoLensTheme(darkTheme = false) {
        ScanScreen(state = ScanUiState(), onAction = {})
    }
}
