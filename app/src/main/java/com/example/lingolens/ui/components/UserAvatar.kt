package com.example.lingolens.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.lingolens.domain.model.ProfilePersonalization

data class AvatarVisual(val label: String, val icon: ImageVector, val accent: Color)

fun avatarVisual(id: String?): AvatarVisual = when (ProfilePersonalization.avatarOrDefault(id)) {
    "book" -> AvatarVisual("Book", Icons.Outlined.AutoStories, Color(0xFF2872A3))
    "camera" -> AvatarVisual("Camera", Icons.Outlined.CameraAlt, Color(0xFF7752AA))
    "paw" -> AvatarVisual("Paw", Icons.Outlined.Pets, Color(0xFFAD5B35))
    "planet" -> AvatarVisual("Planet", Icons.Outlined.Public, Color(0xFF278786))
    "trophy" -> AvatarVisual("Trophy", Icons.Outlined.EmojiEvents, Color(0xFF997000))
    "music" -> AvatarVisual("Music", Icons.Outlined.MusicNote, Color(0xFFB54C7B))
    "rocket" -> AvatarVisual("Rocket", Icons.Outlined.RocketLaunch, Color(0xFF526BC0))
    else -> AvatarVisual("Leaf", Icons.Outlined.Eco, Color(0xFF087326))
}

@Composable
fun UserAvatar(avatarId: String?, modifier: Modifier = Modifier, size: Dp = 48.dp) {
    val visual = avatarVisual(avatarId)
    Surface(modifier.size(size), shape = CircleShape, color = visual.accent.copy(alpha = 0.12f)) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Icon(visual.icon, "${visual.label} avatar", Modifier.size(size * 0.55f), tint = visual.accent)
        }
    }
}
