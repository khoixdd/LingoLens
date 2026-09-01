package com.example.lingolens.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = LingoGreen,
    onPrimary = LingoSurface,
    primaryContainer = LingoMint,
    onPrimaryContainer = LingoGreenDeep,
    secondary = LingoGreenDark,
    onSecondary = LingoSurface,
    secondaryContainer = LingoMintSoft,
    onSecondaryContainer = LingoGreenDeep,
    tertiary = LingoAmber,
    onTertiary = LingoGreenDeep,
    background = LingoBackground,
    onBackground = LingoText,
    surface = LingoSurface,
    onSurface = LingoText,
    surfaceVariant = LingoMintSoft,
    onSurfaceVariant = LingoMutedText,
    outline = LingoOutline,
    outlineVariant = LingoOutlineSoft,
    error = LingoError,
    errorContainer = LingoErrorContainer,
    onErrorContainer = LingoError,
    inverseSurface = LingoCameraSurface,
    inverseOnSurface = LingoCameraContent,
)

@Composable
fun LingoLensTheme(
    @Suppress("UNUSED_PARAMETER") darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        // The approved LingoLens proposal is intentionally light-only for now.
        colorScheme = LightColorScheme,
        typography = Typography,
        shapes = LingoLensShapes,
        content = content
    )
}
