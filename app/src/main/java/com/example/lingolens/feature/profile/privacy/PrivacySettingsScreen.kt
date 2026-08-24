package com.example.lingolens.feature.profile.privacy

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lingolens.ui.components.LingoLensCard
import com.example.lingolens.ui.components.ProfileMenuItem
import com.example.lingolens.ui.components.SettingToggleRow
import com.example.lingolens.ui.theme.LingoLensTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySettingsScreen(state: PrivacySettingsUiState, onAction: (PrivacySettingsAction) -> Unit, modifier: Modifier = Modifier) {
    Scaffold(modifier, topBar = { TopAppBar(title = { Text("Location & Privacy") }, navigationIcon = { IconButton({ onAction(PrivacySettingsAction.Back) }) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back") } }) }) { insets ->
        LazyColumn(Modifier.fillMaxSize().padding(insets), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item { LingoLensCard { SettingToggleRow("Share My Location", checked = state.shareLocation, description = "Allow others to see your approximate location on the map.") { onAction(PrivacySettingsAction.ShareLocationChanged(it)) } } }
            item { LingoLensCard { Icon(Icons.Outlined.Info, null, tint = MaterialTheme.colorScheme.primary); Text("Your location is approximate and visible only to nearby learners. You can turn this off anytime.", modifier = Modifier.padding(top = 8.dp), color = MaterialTheme.colorScheme.onSurfaceVariant) } }
            item { Text("Privacy controls", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold); LingoLensCard(contentPadding = PaddingValues(vertical = 4.dp), modifier = Modifier.padding(top = 8.dp)) { ProfileMenuItem("Who can see me: ${state.visibility}", Icons.Outlined.Visibility, { onAction(PrivacySettingsAction.ChangeVisibility) }); ProfileMenuItem("Location Permission: ${state.locationPermission}", Icons.Outlined.LocationOn, { onAction(PrivacySettingsAction.OpenPermission) }) } }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PrivacyPreview() { LingoLensTheme(darkTheme = false) { PrivacySettingsScreen(PrivacySettingsUiState(), {}) } }
