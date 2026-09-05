package com.example.lingolens.feature.profile.edit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.lingolens.domain.model.ProfilePersonalization
import com.example.lingolens.ui.components.UserAvatar
import com.example.lingolens.ui.components.avatarVisual

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(state: EditProfileUiState, onAction: (EditProfileAction) -> Unit) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("Edit Profile") }, navigationIcon = {
            IconButton(onClick = { onAction(EditProfileAction.Back) }) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back")
            }
        })
    }) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            Column(Modifier.fillMaxSize().padding(padding).imePadding().verticalScroll(rememberScrollState()).padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)) {
                UserAvatar(state.avatarId, Modifier.align(Alignment.CenterHorizontally), size = 88.dp)
                Text("Choose Avatar", style = MaterialTheme.typography.titleMedium)
                ProfilePersonalization.avatarIds.chunked(4).forEach { row ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        row.forEach { id ->
                            val chosen = id == state.avatarId
                            Surface(onClick = { onAction(EditProfileAction.AvatarSelected(id)) },
                                enabled = state.canEdit && !state.isSaving,
                                modifier = Modifier.weight(1f).semantics { selected = chosen },
                                shape = RoundedCornerShape(16.dp),
                                color = if (chosen) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                                border = BorderStroke(if (chosen) 2.dp else 1.dp,
                                    if (chosen) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant)) {
                                Column(Modifier.padding(vertical = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    UserAvatar(id, size = 42.dp)
                                    Text(avatarVisual(id).label, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
                OutlinedTextField(value = state.name, onValueChange = { onAction(EditProfileAction.NameChanged(it)) },
                    modifier = Modifier.fillMaxWidth(), label = { Text("Display Name") }, singleLine = true,
                    enabled = state.canEdit && !state.isSaving, isError = state.nameError != null,
                    supportingText = { state.nameError?.let { Text(it) } })
                state.error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                    if (!state.canEdit) TextButton(onClick = { onAction(EditProfileAction.Retry) }) { Text("Retry") }
                }
                Button(onClick = { onAction(EditProfileAction.Save) }, modifier = Modifier.fillMaxWidth(),
                    enabled = state.canEdit && !state.isSaving) {
                    if (state.isSaving) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                    Text(if (state.isSaving) "Saving…" else "Save Changes", Modifier.padding(horizontal = 8.dp))
                }
            }
        }
    }
}
