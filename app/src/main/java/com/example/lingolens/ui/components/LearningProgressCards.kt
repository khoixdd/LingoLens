package com.example.lingolens.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun DailyGoalCard(
    completed: Int,
    target: Int,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    action: (@Composable () -> Unit)? = null,
) {
    val progress = (completed.toFloat() / target.coerceAtLeast(1)).coerceIn(0f, 1f)
    LingoLensCard(modifier, PaddingValues(14.dp), containerColor = containerColor, elevation = 0.dp) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Daily Goal", Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
            Text("${(progress * 100).roundToInt()}%", style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary)
        }
        Text("$completed / $target words", Modifier.padding(top = 6.dp, bottom = 10.dp),
            style = MaterialTheme.typography.titleLarge)
        LinearProgressIndicator(progress = { progress },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
            trackColor = lerp(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.primaryContainer, 0.6f),
            drawStopIndicator = {})
        action?.let {
            Box(Modifier.fillMaxWidth().padding(top = 2.dp), contentAlignment = Alignment.CenterEnd) { it() }
        }
    }
}

private data class MasteryItem(val label: String, val count: Int, val color: Color)

/** Displays existing mastery counts; never assigns or changes mastery. */
@Composable
fun MasteryOverview(
    title: String,
    totalWords: Int,
    newWords: Int,
    learningWords: Int,
    familiarWords: Int,
    masteredWords: Int,
    modifier: Modifier = Modifier,
    showTotal: Boolean = false,
) {
    val items = listOf(
        MasteryItem("New", newWords, Color(0xFF879C89)),
        MasteryItem("Learning", learningWords, Color(0xFF4FA85C)),
        MasteryItem("Familiar", familiarWords, Color(0xFF328B7B)),
        MasteryItem("Mastered", masteredWords, Color(0xFF176237)),
    )
    val distributionTotal = items.sumOf { it.count.toLong().coerceAtLeast(0) }
    LingoLensCard(modifier, PaddingValues(14.dp), elevation = 0.dp) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(title, Modifier.weight(1f), style = MaterialTheme.typography.titleMedium)
            if (showTotal) Text("$totalWords saved", style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Box(Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 12.dp).height(9.dp)
            .clip(CircleShape).background(MaterialTheme.colorScheme.outlineVariant)
            .semantics {
                contentDescription = if (totalWords == 0 || distributionTotal == 0L) "No vocabulary distribution yet"
                else items.joinToString { "${it.label}: ${it.count}" }
            }) {
            if (totalWords > 0 && distributionTotal > 0) {
                Row(Modifier.fillMaxSize()) {
                    items.filter { it.count > 0 }.forEach { item ->
                        Box(Modifier.weight((item.count.toDouble() / distributionTotal).toFloat())
                            .fillMaxHeight().background(item.color))
                    }
                }
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items.chunked(2).forEach { pair ->
                Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    pair.forEach { item ->
                        Surface(Modifier.weight(1f).fillMaxHeight(), shape = MaterialTheme.shapes.medium,
                            color = lerp(MaterialTheme.colorScheme.surface, item.color, 0.07f)) {
                            Column(Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(Modifier.size(7.dp).clip(CircleShape).background(item.color))
                                    Text(item.label, Modifier.padding(start = 6.dp),
                                        style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(item.count.toString(), Modifier.padding(top = 4.dp),
                                    style = MaterialTheme.typography.headlineSmall)
                            }
                        }
                    }
                }
            }
        }
        if (totalWords == 0) {
            Text("Your saved words will appear here.", Modifier.padding(top = 10.dp),
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
