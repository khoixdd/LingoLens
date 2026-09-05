package com.example.lingolens.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.lingolens.domain.model.WeeklyActivityDay
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WeeklyActivityChart(activity: List<WeeklyActivityDay>) {
    val highest = activity.maxOfOrNull { it.uniqueWords }?.coerceAtLeast(1) ?: 1
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Bottom) {
        activity.forEach { item ->
            val day = LocalDate.ofEpochDay(item.epochDay).dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
            Column(Modifier.weight(1f).semantics(mergeDescendants = true) {
                contentDescription = "$day: ${item.uniqueWords} words studied"
            }, horizontalAlignment = Alignment.CenterHorizontally) {
                Text(item.uniqueWords.toString(), style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Box(Modifier.height(48.dp).padding(top = 4.dp), contentAlignment = Alignment.BottomCenter) {
                    if (item.uniqueWords > 0) {
                        Box(Modifier.width(19.dp).fillMaxHeight((item.uniqueWords.toFloat() / highest).coerceIn(0f, 1f))
                            .clip(RoundedCornerShape(6.dp)).background(MaterialTheme.colorScheme.primary))
                    } else {
                        // Zero activity is a baseline marker, never an invented positive bar.
                        Box(Modifier.width(19.dp).height(2.dp).clip(CircleShape).background(MaterialTheme.colorScheme.outlineVariant))
                    }
                }
                Text(day, style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 5.dp))
            }
        }
    }
}
