package com.example.lingolens.feature.profile.privacy

sealed interface PrivacySettingsAction { data object Back : PrivacySettingsAction; data class ShareLocationChanged(val enabled: Boolean) : PrivacySettingsAction; data object ChangeVisibility : PrivacySettingsAction; data object OpenPermission : PrivacySettingsAction }
