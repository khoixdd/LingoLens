package com.example.lingolens.feature.scan

sealed interface ScanAction {
    data object Close : ScanAction
    data object ToggleFlash : ScanAction
    data object Capture : ScanAction
    data object OpenGallery : ScanAction
    data object DismissFeedback : ScanAction
}
