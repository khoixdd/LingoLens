package com.example.lingolens.feature.profile.privacy

data class PrivacySettingsUiState(val shareLocation: Boolean = false, val visibility: String = "Nearby learners", val locationPermission: String = "Not granted")
