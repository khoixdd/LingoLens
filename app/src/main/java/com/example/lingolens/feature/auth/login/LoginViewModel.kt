package com.example.lingolens.feature.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lingolens.domain.repository.AuthRepository
import com.example.lingolens.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.EmailChanged -> _uiState.update {
                it.copy(email = action.email, emailError = null, errorMessage = null)
            }
            is LoginAction.PasswordChanged -> _uiState.update {
                it.copy(password = action.password, passwordError = null, errorMessage = null)
            }
            LoginAction.TogglePasswordVisibility -> _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            LoginAction.ClearError -> _uiState.update { it.copy(errorMessage = null) }
            LoginAction.SubmitLogin -> performLogin()
            LoginAction.GoogleSignInStarted -> {
                if (!_uiState.value.isLoading) {
                    _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                }
            }
            is LoginAction.GoogleSignInSuccess -> performGoogleLogin(action.idToken)
            is LoginAction.GoogleSignInError -> _uiState.update { it.copy(errorMessage = action.error, isLoading = false) }
            LoginAction.ForgotPassword -> {
                _uiState.update { it.copy(errorMessage = "Password reset link sent to your email if registered.") }
            }
            LoginAction.NavigateToRegister -> Unit
        }
    }

    private fun performLogin() {
        val state = _uiState.value
        if (state.isLoading) return

        val email = state.email.trim()
        val emailError = when {
            email.isBlank() -> "Email is required"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Enter a valid email address"
            else -> null
        }
        val passwordError = if (state.password.isBlank()) "Password is required" else null
        if (emailError != null || passwordError != null) {
            _uiState.update {
                it.copy(
                    emailError = emailError,
                    passwordError = passwordError,
                    errorMessage = null,
                    isLoading = false,
                )
            }
            return
        }

        _uiState.update {
            it.copy(isLoading = true, emailError = null, passwordError = null, errorMessage = null)
        }
        viewModelScope.launch {
            val result = authRepository.loginWithEmail(email, state.password)
            result.fold(
                onSuccess = { user ->
                    userRepository.syncUserProfileOnLogin(user)
                    _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.toLoginMessage()) }
                },
            )
        }
    }

    private fun performGoogleLogin(idToken: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = authRepository.loginWithGoogle(idToken)
            result.fold(
                onSuccess = { user ->
                    userRepository.syncUserProfileOnLogin(user)
                    _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.toGoogleLoginMessage()) }
                },
            )
        }
    }

    private fun Throwable.toLoginMessage(): String {
        val details = message.orEmpty().lowercase()
        return when {
            "network" in details -> "Network error. Check your connection and try again."
            "too many" in details -> "Too many login attempts. Please try again later."
            "password" in details || "credential" in details || "user" in details ->
                "Incorrect email or password."
            else -> "Login failed. Please try again."
        }
    }

    private fun Throwable.toGoogleLoginMessage(): String {
        val details = message.orEmpty().lowercase()
        return when {
            "network" in details -> "Network error. Check your connection and try again."
            "credential" in details -> "Google authentication could not be verified. Please try again."
            else -> "Google sign-in failed. Please try again."
        }
    }
}
