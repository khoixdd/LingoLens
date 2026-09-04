package com.example.lingolens.feature.auth.register

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
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onAction(action: RegisterAction) {
        when (action) {
            is RegisterAction.UsernameChanged -> _uiState.update {
                it.copy(username = action.username, usernameError = null, errorMessage = null)
            }
            is RegisterAction.EmailChanged -> _uiState.update {
                it.copy(email = action.email, emailError = null, errorMessage = null)
            }
            is RegisterAction.PasswordChanged -> _uiState.update {
                it.copy(password = action.password, passwordError = null, confirmPasswordError = null, errorMessage = null)
            }
            is RegisterAction.ConfirmPasswordChanged -> _uiState.update {
                it.copy(confirmPassword = action.password, confirmPasswordError = null, errorMessage = null)
            }
            RegisterAction.TogglePasswordVisibility -> _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            RegisterAction.ToggleConfirmPasswordVisibility -> _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
            is RegisterAction.TermsToggled -> _uiState.update { it.copy(termsAccepted = action.accepted) }
            RegisterAction.ClearError -> _uiState.update { it.copy(errorMessage = null) }
            RegisterAction.SubmitRegister -> performRegister()
            RegisterAction.NavigateToLogin -> Unit
        }
    }

    private fun performRegister() {
        val state = _uiState.value
        if (state.isLoading) return

        val username = state.username.trim()
        val email = state.email.trim()
        val usernameError = if (username.isBlank()) "Username is required" else null
        val emailError = when {
            email.isBlank() -> "Email is required"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Enter a valid email address"
            else -> null
        }
        val passwordError = when {
            state.password.isBlank() -> "Password is required"
            state.password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }
        val confirmPasswordError = when {
            state.confirmPassword.isBlank() -> "Password confirmation is required"
            state.password != state.confirmPassword -> "Passwords do not match"
            else -> null
        }
        if (usernameError != null || emailError != null || passwordError != null || confirmPasswordError != null) {
            _uiState.update {
                it.copy(
                    usernameError = usernameError,
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmPasswordError,
                    errorMessage = null,
                    isLoading = false,
                )
            }
            return
        }

        if (!state.termsAccepted) {
            _uiState.update { it.copy(errorMessage = "Please agree to the Terms of Service.") }
            return
        }

        _uiState.update {
            it.copy(
                isLoading = true,
                usernameError = null,
                emailError = null,
                passwordError = null,
                confirmPasswordError = null,
                errorMessage = null,
            )
        }
        viewModelScope.launch {
            val result = authRepository.registerWithEmail(username, email, state.password)
            result.fold(
                onSuccess = { user ->
                    userRepository.syncUserProfileOnLogin(user)
                    _uiState.update { it.copy(isLoading = false, isRegistered = true) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.toRegistrationMessage()) }
                },
            )
        }
    }

    private fun Throwable.toRegistrationMessage(): String {
        val details = message.orEmpty().lowercase()
        return when {
            "already in use" in details || "already exists" in details ->
                "An account already exists for this email."
            "network" in details -> "Network error. Check your connection and try again."
            "weak password" in details -> "Password is too weak."
            "email" in details && "format" in details -> "Enter a valid email address."
            else -> "Registration failed. Please try again."
        }
    }
}
