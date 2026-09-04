package com.example.lingolens.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.lingolens.ui.theme.LingoGreen
import com.example.lingolens.ui.theme.LingoMint

@Composable
fun BrandDecoratedBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                color = LingoMint,
                radius = size.minDimension * 0.34f,
                center = Offset(size.width * 1.02f, size.height * 0.02f),
            )
            val stroke = Stroke(width = 1.5.dp.toPx())
            fun clover(center: Offset, radius: Float) {
                drawCircle(LingoGreen, radius, center + Offset(0f, -radius), style = stroke)
                drawCircle(LingoGreen, radius, center + Offset(radius, 0f), style = stroke)
                drawCircle(LingoGreen, radius, center + Offset(0f, radius), style = stroke)
                drawCircle(LingoGreen, radius, center + Offset(-radius, 0f), style = stroke)
            }
            clover(Offset(size.width * 0.03f, size.height * 0.16f), 24.dp.toPx())
            clover(Offset(size.width * 0.78f, size.height * 0.16f), 10.dp.toPx())
            clover(Offset(size.width * 0.07f, size.height * 0.88f), 14.dp.toPx())
        }
        content()
    }
}

