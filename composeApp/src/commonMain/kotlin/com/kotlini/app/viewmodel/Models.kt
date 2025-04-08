package com.kotlini.app.viewmodel

/**
 * Enum class representing the difficulty levels of the game.
 * Each difficulty level has a different number of cards.
 */
enum class GameDifficulty(val pairCount: Int) {
    EASY(4),    // 8 cards (4 pairs)
    MEDIUM(8),  // 16 cards (8 pairs)
    HARD(12)    // 24 cards (12 pairs)
}

/**
 * Enum class representing the supported languages in the app.
 */
enum class Language {
    ENGLISH,
    PERSIAN
}

/**
 * Data class representing the state of a card.
 * @param id The unique identifier of the card.
 * @param flowerIndex The index of the flower to display on the back of the card.
 * @param isFlipped Whether the card is flipped or not.
 * @param isSelected Whether the card is selected and should show an animated border.
 */
data class CardState(
    val id: Int,
    val flowerIndex: Int,
    val isFlipped: Boolean,
    val isSelected: Boolean = false,
    val isAnimatingWin: Boolean = false,
    val isWrongGuess: Boolean = false
)
