package com.example.lingolens.feature.profile.privacy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lingolens.domain.repository.AuthRepository
import com.example.lingolens.domain.repository.LocationRepository
import com.example.lingolens.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class PrivacySettingsViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrivacySettingsUiState())
    val uiState: StateFlow<PrivacySettingsUiState> = _uiState.asStateFlow()

    init {
        val hasPermission = locationRepository.hasLocationPermission()
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
            viewModelScope.launch {
                val profile = userRepository.getUserProfile(currentUser.uid)
                _uiState.update {
                    it.copy(
                        shareLocation = profile?.isSharingLocation ?: false,
                        locationPermission = if (hasPermission) "Granted" else "Not granted",
                    )
                }
            }
        }
    }

    fun onAction(action: PrivacySettingsAction) {
        when (action) {
            is PrivacySettingsAction.ShareLocationChanged -> {
                val enabled = action.enabled
                val hasPermission = locationRepository.hasLocationPermission()
                _uiState.update {
                    it.copy(
                        shareLocation = enabled,
                        locationPermission = if (hasPermission) "Granted" else "Permission Required",
                    )
                }

                val currentUser = authRepository.getCurrentUser()
                if (currentUser != null) {
                    viewModelScope.launch {
                        val loc = if (enabled) locationRepository.getCurrentLocation() else null
                        val lat = loc?.latitude ?: 10.762622
                        val lng = loc?.longitude ?: 106.682221
                        userRepository.updateUserLocation(currentUser.uid, lat, lng, enabled)
                    }
                }
            }
            else -> Unit
        }
    }
}
