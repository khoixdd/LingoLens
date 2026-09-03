package com.example.lingolens.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

import com.example.lingolens.data.model.WordEntry

interface DictionaryApiService {
    @GET("api/v2/entries/en/{word}")
    suspend fun getWordData(@Path("word") word: String): Response<List<WordEntry>>
}