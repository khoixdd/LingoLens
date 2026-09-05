package com.example.lingolens.core.mlkit

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

enum class TranslationPair(
    val sourceLanguage: String,
    val targetLanguage: String,
    val label: String,
) {
    EnglishToVietnamese("en", "vi", "English → Vietnamese"),
    VietnameseToEnglish("vi", "en", "Vietnamese → English"),
}

sealed interface TranslationModelState {
    data object NotDownloaded : TranslationModelState
    data object Downloading : TranslationModelState
    data object Downloaded : TranslationModelState
    data class Error(val message: String?) : TranslationModelState
}

@Singleton
class TranslationManager @Inject constructor() {

    private val translators: Map<TranslationPair, Translator> = TranslationPair.entries.associateWith { pair ->
        Translation.getClient(
            TranslatorOptions.Builder()
                .setSourceLanguage(pair.sourceLanguage)
                .setTargetLanguage(pair.targetLanguage)
                .build()
        )
    }

    private val modelStates: Map<TranslationPair, MutableStateFlow<TranslationModelState>> =
        TranslationPair.entries.associateWith { MutableStateFlow<TranslationModelState>(TranslationModelState.NotDownloaded) }

    fun modelState(pair: TranslationPair): StateFlow<TranslationModelState> = requireNotNull(modelStates[pair])

    suspend fun ensureModelDownloaded(pair: TranslationPair) {
        val state = modelStates.getValue(pair)
        when (state.value) {
            is TranslationModelState.Downloaded -> return
            is TranslationModelState.Downloading -> {
                val outcome = state.first { it !is TranslationModelState.Downloading }
                if (outcome is TranslationModelState.Error) {
                    throw IllegalStateException(outcome.message ?: "Translation model download failed.")
                }
                return
            }
            else -> {}
        }

        state.value = TranslationModelState.Downloading
        val conditions = DownloadConditions.Builder().build()
        suspendCancellableCoroutine { continuation ->
            translators.getValue(pair).downloadModelIfNeeded(conditions)
                .addOnSuccessListener {
                    state.value = TranslationModelState.Downloaded
                    if (continuation.isActive) continuation.resume(Unit)
                }
                .addOnFailureListener { error ->
                    state.value = TranslationModelState.Error(error.message)
                    if (continuation.isActive) continuation.resumeWithException(error)
                }
        }
    }

    suspend fun translate(pair: TranslationPair, text: String): String {
        ensureModelDownloaded(pair)
        return suspendCancellableCoroutine { continuation ->
            translators.getValue(pair).translate(text)
                .addOnSuccessListener { translatedText ->
                    if (continuation.isActive) continuation.resume(translatedText)
                }
                .addOnFailureListener { error ->
                    if (continuation.isActive) continuation.resumeWithException(error)
                }
        }
    }
}