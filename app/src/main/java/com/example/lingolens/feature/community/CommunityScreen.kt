package com.example.lingolens.feature.community

import com.example.lingolens.ui.components.UserAvatar
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Navigation
import androidx.compose.material.icons.outlined.PersonPinCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lingolens.ui.components.LingoLensCard

import com.example.lingolens.ui.theme.LingoLensTheme
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun CommunityScreen(
    state: CommunityUiState,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text("Leaderboard", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
        item {
            Surface(
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.primaryContainer,
                tonalElevation = 0.dp,
                shadowElevation = 0.dp,
            ) {
                Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Outlined.EmojiEvents, null, Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                    Text("This Week", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                tonalElevation = 0.dp,
                shadowElevation = 0.dp,
            ) {
                Column(Modifier.padding(4.dp)) {
                    when {
                        state.isLeaderboardLoading -> Box(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            contentAlignment = Alignment.Center,
                        ) { CircularProgressIndicator() }
                        state.leaderboardError != null -> Text(
                            text = state.leaderboardError,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        state.leaderboard.isEmpty() -> Text(
                            text = "No learners have joined the leaderboard yet.",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        else -> state.leaderboard.forEachIndexed { index, entry ->
                            LeaderboardRow(entry)
                            if (index != state.leaderboard.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(start = 76.dp, end = 12.dp),
                                    color = lerp(MaterialTheme.colorScheme.surface, MaterialTheme.colorScheme.outlineVariant, 0.6f),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NearbyLearnersMapCard() {
    val context = LocalContext.current
    var useGoogleMapsSdk by remember { mutableStateOf(false) }
    var selectedPin by remember { mutableStateOf<String?>(null) }

    val hcmcCenter = LatLng(10.762622, 106.682221)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(hcmcCenter, 14f)
    }

    val isGooglePlayServicesAvailable = remember {
        GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
    }

    LingoLensCard(contentPadding = PaddingValues(0.dp)) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            ) {
                if (useGoogleMapsSdk && isGooglePlayServicesAvailable) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                    ) {
                        Marker(
                            state = MarkerState(position = LatLng(10.762622, 106.682221)),
                            title = "You (Learner)",
                            snippet = "Lv. 7 Explorer",
                        )
                        Marker(
                            state = MarkerState(position = LatLng(10.765100, 106.685000)),
                            title = "Minh",
                            snippet = "1.2 km away • Lv. 12",
                        )
                        Marker(
                            state = MarkerState(position = LatLng(10.760000, 106.680000)),
                            title = "An",
                            snippet = "0.8 km away • Lv. 10",
                        )
                    }
                } else {
                    // Custom Vector Study Map Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                    ) {
                        // Background Radar Grid lines
                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.SpaceEvenly,
                        ) {
                            repeat(3) {
                                HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                            }
                        }

                        // Center radar pulse icon (User Location)
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .clickable { selectedPin = "You" },
                            contentAlignment = Alignment.Center,
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                modifier = Modifier.size(56.dp),
                            ) {}
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp),
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Outlined.Navigation,
                                        contentDescription = "You",
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(18.dp),
                                    )
                                }
                            }
                        }

                        // Learner Pin 1: Minh (Top Right)
                        MapLearnerPin(
                            name = "Minh",
                            level = "Lv. 12",
                            distance = "1.2 km",
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 28.dp, end = 40.dp),
                            isSelected = selectedPin == "Minh",
                            onClick = { selectedPin = "Minh" },
                        )

                        // Learner Pin 2: An (Bottom Left)
                        MapLearnerPin(
                            name = "An",
                            level = "Lv. 10",
                            distance = "0.8 km",
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(bottom = 28.dp, start = 40.dp),
                            isSelected = selectedPin == "An",
                            onClick = { selectedPin = "An" },
                        )

                        // Top Mode Switcher Chip
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp)
                                .clickable { useGoogleMapsSdk = !useGoogleMapsSdk },
                            shadowElevation = 2.dp,
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    Icons.Outlined.PersonPinCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp),
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    if (useGoogleMapsSdk) "Google Maps Mode" else "Study Spot Radar",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }
                }
            }

            // Info Card under Map
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primaryContainer) {
                        Icon(
                            Icons.Outlined.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(10.dp),
                        )
                    }
                    Column(Modifier.padding(start = 12.dp).weight(1f)) {
                        Text("Nearby Study Spot Circle", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            "2 active learners studying vocabulary near Ho Chi Minh City campus.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }

                AnimatedVisibility(visible = selectedPin != null) {
                    val pinName = selectedPin ?: "Learner"
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column {
                                Text("Selected Pin: $pinName", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                Text("Active in Study Spot • Ready for vocabulary challenge", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Button(
                                onClick = { selectedPin = null },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            ) {
                                Text("Dismiss", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MapLearnerPin(
    name: String,
    level: String,
    distance: String,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
) {
    Column(
        modifier = modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(20.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            name.take(1),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary,
                        )
                    }
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        name,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        "$level • $distance",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        Icon(
            Icons.Outlined.LocationOn,
            contentDescription = null,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun LeaderboardRow(entry: LeaderboardEntry) {
    val colors = MaterialTheme.colorScheme
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = when {
            entry.rank == 1 -> colors.primaryContainer
            entry.isCurrentUser -> lerp(colors.surface, colors.primaryContainer, 0.45f)
            else -> colors.surface
        },
        border = if (entry.isCurrentUser) BorderStroke(1.dp, colors.outlineVariant) else null,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Surface(
                modifier = Modifier.size(28.dp),
                shape = CircleShape,
                color = if (entry.rank in 1..3) lerp(colors.surface, colors.primaryContainer, 0.7f) else colors.surface,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(entry.rank.toString(), style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (entry.rank in 1..3) FontWeight.Bold else FontWeight.Medium,
                        color = if (entry.rank in 1..3) colors.primary else colors.onSurfaceVariant)
                }
            }
            UserAvatar(entry.avatarId, size = 34.dp)
            Column(Modifier.weight(1f)) {
                Text(entry.name, style = MaterialTheme.typography.titleSmall,
                    fontWeight = if (entry.rank == 1 || entry.isCurrentUser) FontWeight.Bold else FontWeight.SemiBold,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
                if (entry.isCurrentUser) {
                    Text("(You)", style = MaterialTheme.typography.labelSmall, color = colors.primary)
                }
                Text("Lv. ${entry.level} · ${entry.streakDays} day streak",
                    style = MaterialTheme.typography.labelSmall, color = colors.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(entry.xp.toString(), style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold, color = colors.primary)
                Text("XP", style = MaterialTheme.typography.labelSmall, color = colors.onSurfaceVariant)
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
private fun CommunityScreenPreview() {
    LingoLensTheme(darkTheme = false) {
        CommunityScreen(state = CommunityUiState(isLeaderboardLoading = false))
    }
}
