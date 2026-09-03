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
            is RegisterAction.UsernameChanged -> _uiState.update { it.copy(username = action.username, errorMessage = null) }
            is RegisterAction.EmailChanged -> _uiState.update { it.copy(email = action.email, errorMessage = null) }
            is RegisterAction.PasswordChanged -> _uiState.update { it.copy(password = action.password, errorMessage = null) }
            is RegisterAction.ConfirmPasswordChanged -> _uiState.update { it.copy(confirmPassword = action.password, errorMessage = null) }
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
        if (state.username.isBlank() || state.email.isBlank() || state.password.isBlank() || state.confirmPassword.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please fill in all required fields.") }
            return
        }

        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Passwords do not match.") }
            return
        }

        if (state.password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Password must be at least 6 characters.") }
            return
        }

        if (!state.termsAccepted) {
            _uiState.update { it.copy(errorMessage = "Please agree to the Terms of Service.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = authRepository.registerWithEmail(state.username, state.email, state.password)
            result.fold(
                onSuccess = { user ->
                    userRepository.syncUserProfileOnLogin(user)
                    _uiState.update { it.copy(isLoading = false, isRegistered = true) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.localizedMessage ?: "Registration failed. Try another email.") }
                },
            )
        }
    }
}
