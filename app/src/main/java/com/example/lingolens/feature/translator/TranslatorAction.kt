package com.example.lingolens.feature.translator

sealed interface TranslatorAction {
    data class UpdateInput(val text: String) : TranslatorAction
    data object SwapDirection : TranslatorAction
    data object Translate : TranslatorAction
    data object DismissError : TranslatorAction
    data object Back : TranslatorAction
}