package com.example.lingolens.feature.scan.component

import android.content.Context
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.google.mlkit.nl.languageid.LanguageIdentification

fun captureAndExtractText(
    imageCapture: ImageCapture,
    context: Context,
    onSuccess: (List<String>) -> Unit,
    onError: (Exception) -> Unit
) {
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    val executor = ContextCompat.getMainExecutor(context)
    val languageIdentifier = LanguageIdentification.getClient()

    imageCapture.takePicture(
        executor,
        object : ImageCapture.OnImageCapturedCallback() {
            @OptIn(ExperimentalGetImage::class)
            override fun onCaptureSuccess(imageProxy: ImageProxy) {
                val mediaImage = imageProxy.image
                if (mediaImage != null) {
                    val inputImage = InputImage.fromMediaImage(
                        mediaImage, 
                        imageProxy.imageInfo.rotationDegrees
                    )
                    
                    recognizer.process(inputImage)
                        .addOnSuccessListener { visionText ->
                            val englishWordRegex = Regex("^[a-zA-Z]+$")

                            languageIdentifier.identifyLanguage(visionText.text)
                                .addOnSuccessListener { languageCode ->
                                    if (languageCode == "en") {
                                        val words = visionText.textBlocks
                                            .flatMap { it.lines }
                                            .flatMap { it.elements }
                                            .map { it.text }
                                            .filter { word -> englishWordRegex.matches(word) }
                                            .map { it.lowercase() }
                                        onSuccess(words)
                                    } else {
                                        onError(Exception("Detected text is not in English."))
                                    }
                                }
                        }
                        .addOnFailureListener(onError)
                        .addOnCompleteListener { imageProxy.close() }
                } else {
                    imageProxy.close()
                    onError(IllegalStateException("No image data found"))
                }
            }

            override fun onError(exception: ImageCaptureException) {
                onError(exception)
            }
        }
    )
}