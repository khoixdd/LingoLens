package com.example.lingolens.feature.auth.register

sealed interface RegisterAction {
    data class UsernameChanged(val username: String) : RegisterAction
    data class EmailChanged(val email: String) : RegisterAction
    data class PasswordChanged(val password: String) : RegisterAction
    data class ConfirmPasswordChanged(val password: String) : RegisterAction
    data object TogglePasswordVisibility : RegisterAction
    data object ToggleConfirmPasswordVisibility : RegisterAction
    data class TermsToggled(val accepted: Boolean) : RegisterAction
    data object SubmitRegister : RegisterAction
    data object NavigateToLogin : RegisterAction
    data object ClearError : RegisterAction
}
