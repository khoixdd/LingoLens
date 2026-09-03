package com.example.lingolens.feature.scan

data class ScanUiState(
    val isFlashEnabled: Boolean = false,
    val feedbackMessage: String? = null,
    val isScanning: Boolean = false,
    val extractedText: List<String>? = emptyList(),
)
