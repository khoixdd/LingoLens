package com.example.lingolens.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import com.example.lingolens.data.api.DictionaryApiService
import com.example.lingolens.domain.model.Vocabulary
import com.example.lingolens.data.model.WordEntry
import com.example.lingolens.data.model.Meaning
import com.example.lingolens.data.model.Definition

import android.util.Log

interface WordFetcher {
    suspend fun getVocabulary(word: String): Vocabulary?
}

@Singleton
class WordFetcherImpl @Inject constructor (
    private val api: DictionaryApiService
) : WordFetcher {

    override suspend fun getVocabulary(word: String): Vocabulary? {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getWordData(word)
                
                Log.d("API_TEST", "Response Code: ${response.code()}")
                Log.d("API_TEST", "Response Body: ${response.body()}")

                if (response.isSuccessful && !response.body().isNullOrEmpty()) {
                    val wordData = response.body()!![0]
                    
                    // Extract the primary meaning and definition
                    val primaryMeaning = wordData.meanings.firstOrNull()
                    val primaryDefinition = primaryMeaning?.definitions?.firstOrNull()

                    // Construct and return your custom object
                    Vocabulary(
                        id = UUID.randomUUID().toString(),
                        word = wordData.word,
                        meaning = primaryDefinition?.definition ?: "No meaning available.",
                        pronunciation = wordData.phonetic ?: "",
                        partOfSpeech = primaryMeaning?.partOfSpeech ?: "",
                        example = primaryDefinition?.example ?: "",
                        tags = emptyList(),
                        isFavorite = false
                    )
                } else {
                    null // 404 Not Found (Invalid word)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("API_TEST", "CRASH in getVocabulary: ${e.message}", e)
                null
            }
        }
    }
}