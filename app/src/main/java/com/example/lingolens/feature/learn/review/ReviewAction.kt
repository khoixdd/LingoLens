package com.example.lingolens.feature.learn.review

enum class ReviewRating { Again, Hard, Good, Easy }

sealed interface ReviewAction {
    data object Back : ReviewAction
    data object Reveal : ReviewAction
    data object PlayPronunciation : ReviewAction
    data class Rate(val rating: ReviewRating) : ReviewAction
}
