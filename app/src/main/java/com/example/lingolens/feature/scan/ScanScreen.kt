package com.example.lingolens.feature.scan

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
                Text(
                    text = "Point at the text",
                    color = previewContentColor,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "Keep the words inside the frame",
                    color = previewContentColor.copy(alpha = 0.72f),
                    modifier = Modifier.padding(top = 4.dp, bottom = 24.dp),
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.15f)
                        .border(
                            BorderStroke(3.dp, MaterialTheme.colorScheme.primary),
                            RoundedCornerShape(24.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "Camera preview",
                        color = previewContentColor.copy(alpha = 0.45f),
                        textAlign = TextAlign.Center,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = { onAction(ScanAction.OpenGallery) }) {
                            Icon(Icons.Outlined.PhotoLibrary, contentDescription = "Open gallery", tint = previewContentColor)
                        }
                        Text("Gallery", color = previewContentColor, style = MaterialTheme.typography.labelMedium)
                    }
                }
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Surface(
                        onClick = { onAction(ScanAction.Capture) },
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        shadowElevation = 8.dp,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Outlined.CameraAlt,
                                contentDescription = "Capture text",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp),
                            )
                        }
                    }
                }
                Box(Modifier.weight(1f))
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

@Preview(showBackground = true)
@Composable
private fun ScanScreenPreview() {
    LingoLensTheme(darkTheme = false) {
        ScanScreen(state = ScanUiState(), onAction = {})
    }
}
