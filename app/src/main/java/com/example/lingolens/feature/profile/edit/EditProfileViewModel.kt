package com.example.lingolens.feature.profile.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lingolens.domain.model.ProfilePersonalization
import com.example.lingolens.domain.repository.AuthRepository
import com.example.lingolens.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val auth: AuthRepository,
    private val users: UserRepository,
) : ViewModel() {
    private val state = MutableStateFlow(EditProfileUiState())
    val uiState = state.asStateFlow()
    private val uid = auth.getCurrentUser()?.uid

    init { load() }

    private fun load() {
        state.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                check(uid != null && auth.getCurrentUser()?.uid == uid) { "Sign in again to edit your profile." }
                val profile = checkNotNull(users.getUserProfile(uid)) { "Unable to load profile. Please retry." }
                state.value = EditProfileUiState(isLoading = false, name = profile.username,
                    avatarId = ProfilePersonalization.avatarOrDefault(profile.avatarId), canEdit = true)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                state.update { it.copy(isLoading = false, error = error.message ?: "Unable to load profile.") }
            }
        }
    }

    fun onAction(action: EditProfileAction) {
        if (state.value.isSaving || state.value.isLoading) return
        when (action) {
            is EditProfileAction.NameChanged -> state.update { it.copy(name = action.name, nameError = null, error = null) }
            is EditProfileAction.AvatarSelected -> if (action.id in ProfilePersonalization.avatarIds) {
                state.update { it.copy(avatarId = action.id, error = null) }
            }
            EditProfileAction.Save -> save()
            EditProfileAction.Retry -> load()
            EditProfileAction.Back -> Unit
        }
    }

    private fun save() {
        val draft = state.value
        if (!draft.canEdit || draft.isSaved) return
        val validation = ProfilePersonalization.validateName(draft.name)
        if (validation != null) {
            state.update { it.copy(nameError = validation) }
            return
        }
        state.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            try {
                check(uid != null && auth.getCurrentUser()?.uid == uid) { "Account changed. Please sign in again." }
                users.updatePersonalization(uid, draft.name.trim(), draft.avatarId)
                state.update { it.copy(isSaving = false, isSaved = true) }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                state.update { it.copy(isSaving = false, error = "Couldn't save changes. Check your connection and retry.") }
            }
        }
    }
}
