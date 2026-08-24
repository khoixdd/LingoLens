package com.example.lingolens.feature.learn.notebook

enum class NotebookFilter(val label: String) { All("All"), Favorite("Favorite"), Technology("Technology"), Travel("Travel") }

enum class MasteryLevel(val label: String) { New("New"), Learning("Learning"), Familiar("Familiar"), Mastered("Mastered") }

data class VocabularyItem(
    val id: String,
    val word: String,
    val meaning: String,
    val pronunciation: String,
    val partOfSpeech: String,
    val isFavorite: Boolean,
    val mastery: MasteryLevel,
    val tags: List<String>,
    val lastReviewed: String,
)

sealed interface NotebookContentState {
    data object Loading : NotebookContentState
    data object Empty : NotebookContentState
    data object NoSearchResults : NotebookContentState
    data class Content(val words: List<VocabularyItem>) : NotebookContentState
}

data class NotebookUiState(
    val searchQuery: String = "",
    val selectedFilter: NotebookFilter = NotebookFilter.All,
    val content: NotebookContentState = NotebookContentState.Loading,
)

val sampleVocabulary = listOf(
    VocabularyItem("ubiquitous", "ubiquitous", "phổ biến, có mặt ở khắp mọi nơi", "/juːˈbɪkwɪtəs/", "adjective", true, MasteryLevel.Learning, listOf("Technology"), "Today"),
    VocabularyItem("artificial", "artificial", "nhân tạo", "/ˌɑːrtɪˈfɪʃəl/", "adjective", false, MasteryLevel.Familiar, listOf("Technology"), "Yesterday"),
    VocabularyItem("intelligence", "intelligence", "trí thông minh", "/ɪnˈtelɪdʒəns/", "noun", true, MasteryLevel.Mastered, listOf("Technology"), "2 days ago"),
    VocabularyItem("transformative", "transformative", "có tính biến đổi mạnh", "/trænsˈfɔːrmətɪv/", "adjective", false, MasteryLevel.New, listOf("Travel"), "Not reviewed"),
)
