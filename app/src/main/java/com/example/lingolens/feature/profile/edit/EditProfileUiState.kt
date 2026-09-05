package com.example.lingolens.feature.profile.edit

data class EditProfileUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val name: String = "",
    val avatarId: String = "leaf",
    val nameError: String? = null,
    val error: String? = null,
    val isSaved: Boolean = false,
    val canEdit: Boolean = false,
)

sealed interface EditProfileAction {
    data object Back : EditProfileAction
    data object Retry : EditProfileAction
    data object Save : EditProfileAction
    data class NameChanged(val name: String) : EditProfileAction
    data class AvatarSelected(val id: String) : EditProfileAction
}
