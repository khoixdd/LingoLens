package com.example.lingolens.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.example.lingolens.ui.theme.LingoAmber
import com.example.lingolens.ui.theme.LingoGreen
import kotlin.random.Random

private data class ConfettiParticle(val vx: Float, val vy: Float, val width: Float, val spin: Float, val color: Color)

/** Finite, decorative Canvas: no pointer handling, timers after completion, or business effects. */
@Composable
fun CelebrationOverlay(visible: Boolean, modifier: Modifier = Modifier, onFinished: () -> Unit = {}) {
    if (!visible) return
    val latestFinished by rememberUpdatedState(onFinished)
    val progress = remember { Animatable(0f) }
    var finished by remember { mutableStateOf(false) }
    val particles = remember {
        val random = Random(42)
        val colors = listOf(LingoGreen, LingoAmber, Color(0xFF75D4B0), Color(0xFF7897E0), Color(0xFFE999B5))
        List(64) {
            ConfettiParticle(random.nextFloat() * 1.6f - 0.8f, -0.25f - random.nextFloat() * 0.55f,
                4f + random.nextFloat() * 5f, random.nextFloat() * 720f - 360f, colors[it % colors.size])
        }
    }
    LaunchedEffect(Unit) {
        progress.animateTo(1f, tween(2100, easing = LinearEasing))
        finished = true
        latestFinished()
    }
    if (!finished) Canvas(modifier.fillMaxSize()) {
        val t = progress.value
        particles.forEachIndexed { index, particle ->
            val x = size.width * (0.5f + particle.vx * t)
            val y = size.height * (0.28f + particle.vy * t + 1.2f * t * t)
            val width = particle.width.dp.toPx()
            val center = Offset(x, y)
            val alpha = ((1f - t) * 4f).coerceIn(0f, 1f)
            rotate(particle.spin * t, center) {
                if (index % 3 == 0) drawCircle(particle.color, width / 2, center, alpha = alpha)
                else drawRect(particle.color, center, Size(width, width * 0.45f), alpha = alpha)
            }
        }
    }
}
