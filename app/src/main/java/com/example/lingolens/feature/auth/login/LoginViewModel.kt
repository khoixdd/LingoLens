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

    init {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
            _uiState.update { it.copy(isLoggedIn = true) }
            viewModelScope.launch {
                userRepository.syncUserProfileOnLogin(currentUser)
            }
        }
    }

    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.EmailChanged -> _uiState.update { it.copy(email = action.email, errorMessage = null) }
            is LoginAction.PasswordChanged -> _uiState.update { it.copy(password = action.password, errorMessage = null) }
            LoginAction.TogglePasswordVisibility -> _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            LoginAction.ClearError -> _uiState.update { it.copy(errorMessage = null) }
            LoginAction.SubmitLogin -> performLogin()
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
        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please fill in all fields.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = authRepository.loginWithEmail(state.email, state.password)
            result.fold(
                onSuccess = { user ->
                    userRepository.syncUserProfileOnLogin(user)
                    _uiState.update { it.copy(isLoading = false, isLoggedIn = true) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.localizedMessage ?: "Login failed. Please check credentials.") }
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
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.localizedMessage ?: "Google sign in failed.") }
                },
            )
        }
    }
}
