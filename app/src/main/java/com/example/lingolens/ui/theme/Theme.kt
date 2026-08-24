package com.example.lingolens.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Mint80,
    secondary = SoftGreen,
    tertiary = WarmOrange,
    background = Color(0xFF0F1F19),
    surface = Color(0xFF142A21),
    onPrimary = DeepGreen,
    onBackground = Color(0xFFE1F3E9),
    onSurface = Color(0xFFE1F3E9)
)

private val LightColorScheme = lightColorScheme(
    primary = FreshGreen,
    secondary = SoftGreen,
    tertiary = WarmOrange,
    background = PaleMint,
    surface = Color.White,
    surfaceVariant = MintContainer,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = DeepGreen,
    onSurface = DeepGreen,
    onSurfaceVariant = Color(0xFF365A4B),
    error = SoftRed
    /* Other default colors to override
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun LingoLensTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
