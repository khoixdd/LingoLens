package com.example.lingolens.data.model

data class WordEntry(
    val word: String,
    val phonetic: String?,
    val meanings: List<Meaning>
)

data class Meaning(
    val partOfSpeech: String,
    val definitions: List<Definition>
)

data class Definition(
    val definition: String,
    val example: String?
)