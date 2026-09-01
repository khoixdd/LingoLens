package com.example.lingolens.feature.profile.privacy

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lingolens.ui.components.LingoLensCard
import com.example.lingolens.ui.components.ProfileMenuItem
import com.example.lingolens.ui.components.SectionHeader
import com.example.lingolens.ui.components.SettingToggleRow
import com.example.lingolens.ui.theme.LingoLensTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySettingsScreen(
    state: PrivacySettingsUiState,
    onAction: (PrivacySettingsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Location & Privacy") },
                navigationIcon = {
                    IconButton({ onAction(PrivacySettingsAction.Back) }) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back")
                    }
                },
            )
        },
    ) { insets ->
        LazyColumn(
            Modifier.fillMaxSize().padding(insets),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                LingoLensCard(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)) {
                    SettingToggleRow(
                        title = "Share my location",
                        checked = state.shareLocation,
                        description = "Allow nearby learners to see your approximate location.",
                        showDivider = false,
                    ) { onAction(PrivacySettingsAction.ShareLocationChanged(it)) }
                }
            }
            item {
                LingoLensCard(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Outlined.Info, null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            "Your location is approximate and visible only to nearby learners. You can turn this off anytime.",
                            modifier = Modifier.padding(start = 10.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
            item { SectionHeader("Privacy controls") }
            item {
                LingoLensCard(contentPadding = PaddingValues(vertical = 4.dp)) {
                    ProfileMenuItem(
                        "Who can see me: ${state.visibility}",
                        Icons.Outlined.Visibility,
                        { onAction(PrivacySettingsAction.ChangeVisibility) },
                    )
                    ProfileMenuItem(
                        "Location permission: ${state.locationPermission}",
                        Icons.Outlined.LocationOn,
                        { onAction(PrivacySettingsAction.OpenPermission) },
                        showDivider = false,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PrivacyPreview() { LingoLensTheme(darkTheme = false) { PrivacySettingsScreen(PrivacySettingsUiState(), {}) } }
