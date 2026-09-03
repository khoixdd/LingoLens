package com.example.lingolens.feature.scan.component

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.IOException
import com.google.mlkit.nl.languageid.LanguageIdentification

fun extractTextFromUri(
    context: Context,
    uri: Uri,
    onSuccess: (List<String>) -> Unit,
    onError: (Exception) -> Unit
) {
    try {
        // ML Kit handles the Uri to Bitmap conversion internally
        val image = InputImage.fromFilePath(context, uri)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        val languageIdentifier = LanguageIdentification.getClient()

        recognizer.process(image)
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
    } catch (e: IOException) {
        onError(e)
    }
}