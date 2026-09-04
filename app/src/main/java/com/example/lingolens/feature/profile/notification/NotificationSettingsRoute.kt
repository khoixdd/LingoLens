package com.example.lingolens.feature.profile.notification

import android.Manifest
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun NotificationSettingsRoute(
    onBack: () -> Unit,
    viewModel: NotificationSettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var pendingToggle by remember { mutableStateOf<NotificationSettingsAction.Toggle?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        viewModel.onAction(NotificationSettingsAction.PermissionStatusChanged(granted, denied = !granted))
        if (granted) pendingToggle?.let(viewModel::onAction)
        pendingToggle = null
    }
    val permissionGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
        PackageManager.PERMISSION_GRANTED

    LaunchedEffect(permissionGranted) {
        viewModel.onAction(NotificationSettingsAction.PermissionStatusChanged(permissionGranted))
    }

    NotificationSettingsScreen(state = state, onAction = { action ->
        when (action) {
            NotificationSettingsAction.Back -> onBack()
            NotificationSettingsAction.ChangeReminderTime -> {
                TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        viewModel.onAction(NotificationSettingsAction.SetReminderTime(hour, minute))
                    },
                    state.reminderHour,
                    state.reminderMinute,
                    false,
                ).show()
            }
            is NotificationSettingsAction.Toggle -> {
                if (action.enabled && !permissionGranted) {
                    pendingToggle = action
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    viewModel.onAction(action)
                }
            }
            is NotificationSettingsAction.PermissionStatusChanged,
            is NotificationSettingsAction.SetReminderTime,
            -> viewModel.onAction(action)
        }
    })
}
