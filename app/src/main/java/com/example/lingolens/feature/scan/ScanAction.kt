package com.example.lingolens.feature.scan

sealed interface ScanAction {
    data object Close : ScanAction
    data object ToggleFlash : ScanAction
    data object Capture : ScanAction
    data object OpenGallery : ScanAction
    data object DismissFeedback : ScanAction
    data object OpenLearning : ScanAction
    
    data class TextDetected(val words: List<String>) : ScanAction
    data class CaptureText(val words: List<String>) : ScanAction
    data class ErrorOccurred(val message: String) : ScanAction
}
