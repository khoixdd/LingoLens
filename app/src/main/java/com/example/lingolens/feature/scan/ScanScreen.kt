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
import androidx.compose.material.icons.filled.NoPhotography
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import android.content.Context
import java.util.concurrent.Executor
import com.example.lingolens.ui.theme.LingoLensTheme
import com.example.lingolens.feature.scan.component.CameraPreview
import com.example.lingolens.feature.scan.component.captureAndExtractText
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun ScanScreen(
    state: ScanUiState,
    onAction: (ScanAction) -> Unit,
    events: Flow<ScanEvent> = emptyFlow(),
    onNavigateToLearn: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val previewColor = MaterialTheme.colorScheme.inverseSurface
    val previewContentColor = MaterialTheme.colorScheme.inverseOnSurface

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, 
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasCameraPermission = isGranted }
    )

    val imageCapture = remember { 
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build() 
    }

    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                is ScanEvent.NavigateToLearn -> onNavigateToLearn()
                else -> {}
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(
        modifier = modifier.fillMaxSize().background(previewColor),
    ) {

        if (hasCameraPermission) {
            CameraPreview(
                imageCapture = imageCapture,
                isFlashEnabled = state.isFlashEnabled,
                // onTextDetected = { words -> onAction(ScanAction.TextDetected(words)) },
                onError = { e -> onAction(ScanAction.ErrorOccurred(e.message ?: "Unknown error")) },
                modifier = Modifier.fillMaxSize()
            )
        }

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
                if (hasCameraPermission) {
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
                } else {
                    PermissionDeniedPrompt(
                        onRequestPermission = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                        onOpenSettings = {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                            context.startActivity(intent)
                        }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.weight(1f))
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Surface(
                        onClick = { 
                            if (!state.isScanning && hasCameraPermission) {
                                onAction(ScanAction.Capture)
                                captureAndExtractText(
                                    imageCapture = imageCapture,
                                    context = context,
                                    onSuccess = { words -> 
                                        onAction(ScanAction.CaptureText(words)) 
                                    },
                                    onError = { error -> 
                                        onAction(ScanAction.ErrorOccurred(error.message ?: "Capture failed")) 
                                    }
                                )
                                
                            }
                        },
                        modifier = Modifier.size(76.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        border = BorderStroke(5.dp, previewContentColor),
                        shadowElevation = 8.dp,
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            // Show the standard camera icon
                            Icon(
                                Icons.Outlined.CameraAlt,
                                contentDescription = "Capture text",
                                tint = if (hasCameraPermission) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
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

        if (state.isScanning) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.4f)) // Dim the background
                    // Intercept and discard all clicks so they don't reach the buttons below
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {} 
                    ),
                contentAlignment = Alignment.Center
            ) {
                // The loading spinner in the center of the screen
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp),
                    strokeWidth = 6.dp
                )
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

@Composable
private fun PermissionDeniedPrompt(
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(0.85f),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.NoPhotography,
                contentDescription = "No Photography",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = "Camera Access Denied",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "To scan scene text, this app needs access to your camera. You can grant this in your device settings.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(onClick = onRequestPermission) {
                    Text("Retry")
                }
                Button(onClick = onOpenSettings) {
                    Text("Open Settings")
                }
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
