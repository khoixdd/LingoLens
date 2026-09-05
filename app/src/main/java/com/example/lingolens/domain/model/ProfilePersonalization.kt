package com.example.lingolens.domain.model

object ProfilePersonalization {
    const val DEFAULT_AVATAR = "leaf"
    const val MAX_NAME_LENGTH = 40
    val avatarIds = listOf("leaf", "book", "camera", "paw", "planet", "trophy", "music", "rocket")
    fun avatarOrDefault(id: String?) = id?.takeIf { it in avatarIds } ?: DEFAULT_AVATAR
    fun validateName(name: String): String? = when {
        name.isBlank() -> "Enter a display name."
        name.trim().length > MAX_NAME_LENGTH -> "Use 40 characters or fewer."
        else -> null
    }
}
