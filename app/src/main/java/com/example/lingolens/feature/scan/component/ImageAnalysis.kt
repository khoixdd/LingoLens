package com.example.lingolens.feature.scan.component

import android.annotation.SuppressLint
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

@SuppressLint("UnsafeOptInUsageError")
class TextRecognitionAnalyzer(
    private val onWordsDetected: (List<String>) -> Unit,
    private val onError: (Exception) -> Unit
) : ImageAnalysis.Analyzer {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val words = visionText.textBlocks
                        .flatMap { it.lines }
                        .flatMap { it.elements }
                        .map { it.text }
                    
                    if (words.isNotEmpty()) {
                        onWordsDetected(words)
                    }
                }
                .addOnFailureListener { e ->
                    onError(e)
                }
                .addOnCompleteListener {
                    // Critical: Close the proxy to receive the next frame
                    imageProxy.close() 
                }
        } else {
            imageProxy.close()
        }
    }
}
