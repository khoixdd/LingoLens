package com.example.lingolens.feature.auth.login

sealed interface LoginAction {
    data class EmailChanged(val email: String) : LoginAction
    data class PasswordChanged(val password: String) : LoginAction
    data object TogglePasswordVisibility : LoginAction
    data object SubmitLogin : LoginAction
    data class GoogleSignInSuccess(val idToken: String) : LoginAction
    data class GoogleSignInError(val error: String) : LoginAction
    data object NavigateToRegister : LoginAction
    data object ForgotPassword : LoginAction
    data object ClearError : LoginAction
}
