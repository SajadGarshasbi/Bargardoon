package com.kotlini.app.viewmodel

import androidx.lifecycle.ViewModel
import com.kotlini.app.FLOWERS
import com.kotlini.app.getHapticFeedback
import com.kotlini.app.isMobileDevice
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
 * ViewModel for the main screen (Screen Main).
 * Manages the state and logic for the main screen, including the list of cards and their flipped state.
 * This implementation is platform-independent and works across all platforms, including Wasm.
 */
class MainViewModel : ViewModel() {

    // Create a custom CoroutineScope for this ViewModel
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // State for the list of cards
    private val _cardStates = MutableStateFlow<List<CardState>>(emptyList())
    val cardStates: StateFlow<List<CardState>> = _cardStates.asStateFlow()

    // Preview mode state
    private val _isInPreviewMode = MutableStateFlow(true)
    val isInPreviewMode: StateFlow<Boolean> = _isInPreviewMode.asStateFlow()

    // Countdown timer state
    private val _countdownSeconds = MutableStateFlow(8)
    val countdownSeconds: StateFlow<Int> = _countdownSeconds.asStateFlow()

    // Guess attempts counter
    private val _guessAttempts = MutableStateFlow(0)
    val guessAttempts: StateFlow<Int> = _guessAttempts.asStateFlow()

    // Game won state
    private val _isGameWon = MutableStateFlow(false)
    val isGameWon: StateFlow<Boolean> = _isGameWon.asStateFlow()

    // Difficulty level state
    private val _selectedDifficulty = MutableStateFlow<GameDifficulty?>(null)
    val selectedDifficulty: StateFlow<GameDifficulty?> = _selectedDifficulty.asStateFlow()

    // Win animation state
    private val _isWinAnimationInProgress = MutableStateFlow(false)
    val isWinAnimationInProgress: StateFlow<Boolean> = _isWinAnimationInProgress.asStateFlow()

    // Currently animating card ID (-1 means no card is being animated)
    private val _currentlyAnimatingCardId = MutableStateFlow(-1)
    val currentlyAnimatingCardId: StateFlow<Int> = _currentlyAnimatingCardId.asStateFlow()

    // Game state variables
    private var firstSelectedCard: CardState? = null
    private var isProcessingMatch = false
    private val matchedCardIds = mutableSetOf<Int>()

    init {
        // Don't initialize cards yet, wait for difficulty selection
    }

    /**
     * Sets the difficulty level and initializes the game.
     * @param difficulty The difficulty level to set.
     */
    fun setDifficulty(difficulty: GameDifficulty) {
        _selectedDifficulty.value = difficulty
        initializeCards()
    }

    /**
     * Initializes the list of cards with their default state based on the selected difficulty.
     * Selects random flowers from the list of 21, duplicates each selected flower,
     * and shuffles the cards before displaying them.
     * In preview mode, all cards start flipped (face up) for 8 seconds, regardless of difficulty level.
     */
    private fun initializeCards() {
        val difficulty = _selectedDifficulty.value ?: return

        viewModelScope.launch {
            // Reset game state
            _isInPreviewMode.value = true
            // Set countdown to 8 seconds for all difficulty levels
            _countdownSeconds.value = 8
            _guessAttempts.value = 0
            _isGameWon.value = false
            _isWinAnimationInProgress.value = false
            _currentlyAnimatingCardId.value = -1
            firstSelectedCard = null
            isProcessingMatch = false
            matchedCardIds.clear()

            // Get the number of pairs for the selected difficulty
            val pairCount = difficulty.pairCount

            // Select random flowers from the list of 21
            val selectedFlowerIndices = FLOWERS.indices.shuffled().take(pairCount)

            // Duplicate each selected flower to create pairs
            val duplicatedFlowerIndices = selectedFlowerIndices + selectedFlowerIndices

            // Shuffle the cards
            val shuffledFlowerIndices = duplicatedFlowerIndices.shuffled()

            // Create the cards with the shuffled flower indices
            // In preview mode, all cards start flipped (face up)
            val totalCards = pairCount * 2
            val cards = List(totalCards) { index ->
                CardState(
                    id = index,
                    flowerIndex = shuffledFlowerIndices[index],
                    isFlipped = true,
                    isSelected = false
                )
            }
            _cardStates.value = cards

            // Start the countdown timer
            startPreviewCountdown()
        }
    }

    /**
     * Starts an 8-second countdown timer for the preview mode.
     * After the countdown, flips all cards face down and starts the game.
     */
    private fun startPreviewCountdown() {
        viewModelScope.launch {
            // Count down from the initial value to 1
            val initialCountdown = _countdownSeconds.value
            for (i in initialCountdown downTo 1) {
                _countdownSeconds.value = i
                delay(1000) // Wait for 1 second
            }

            // Flip all cards face down
            val updatedCards = _cardStates.value.map { it.copy(isFlipped = false) }
            _cardStates.value = updatedCards

            // End preview mode
            _isInPreviewMode.value = false
        }
    }

    /**
     * Handles the card selection logic for the memory game.
     * User must select 2 cards at a time. If they match, they stay flipped.
     * If they don't match, both cards are reset.
     * @param cardId The ID of the card to toggle.
     */
    fun toggleCardFlip(cardId: Int) {
        // If we're in preview mode, currently processing a match, or the card is already matched, ignore the click
        if (_isInPreviewMode.value || isProcessingMatch || matchedCardIds.contains(cardId)) {
            return
        }

        val currentCards = _cardStates.value.toMutableList()
        val cardIndex = currentCards.indexOfFirst { it.id == cardId }

        if (cardIndex != -1) {
            val card = currentCards[cardIndex]

            // If the card is already flipped, ignore the click
            if (card.isFlipped) {
                return
            }

            // Flip the card and mark it as selected (to show animated border)
            currentCards[cardIndex] = card.copy(isFlipped = true, isSelected = true)
            _cardStates.value = currentCards

            // If this is the first card being selected
            if (firstSelectedCard == null) {
                firstSelectedCard = card
            } else {
                // This is the second card, check for a match
                val firstCard = firstSelectedCard!!

                // Increment guess attempts counter
                _guessAttempts.value = _guessAttempts.value + 1

                // Prevent further card selection while processing
                isProcessingMatch = true

                if (firstCard.flowerIndex == card.flowerIndex) {
                    // Cards match - add them to matched set
                    matchedCardIds.add(firstCard.id)
                    matchedCardIds.add(card.id)

                    // Remove the animated border from matched cards
                    val updatedCards = _cardStates.value.toMutableList()
                    val firstCardIndex = updatedCards.indexOfFirst { it.id == firstCard.id }
                    val secondCardIndex = updatedCards.indexOfFirst { it.id == card.id }

                    if (firstCardIndex != -1) {
                        updatedCards[firstCardIndex] = updatedCards[firstCardIndex].copy(isSelected = false)
                    }

                    if (secondCardIndex != -1) {
                        updatedCards[secondCardIndex] = updatedCards[secondCardIndex].copy(isSelected = false)
                    }

                    _cardStates.value = updatedCards

                    // Check if all cards are matched (game won)
                    val totalCards = _selectedDifficulty.value?.pairCount?.times(2) ?: 0
                    if (matchedCardIds.size == totalCards) {
                        _isGameWon.value = true
                        // Start the win animation
                        startWinAnimation()
                    }

                    // Reset for next selection
                    firstSelectedCard = null
                    isProcessingMatch = false
                } else {
                    // Cards don't match - flip them back after a delay
                    viewModelScope.launch {
                        delay(1000) // 1 second delay to let user see the cards

                        // Vibrate if on a mobile device before flipping cards back
                        if (isMobileDevice()) {
                            getHapticFeedback().vibrate(20)
                        }

                        // Flip both cards back
                        val updatedCards = _cardStates.value.toMutableList()
                        val firstCardIndex = updatedCards.indexOfFirst { it.id == firstCard.id }
                        val secondCardIndex = updatedCards.indexOfFirst { it.id == card.id }

                        if (firstCardIndex != -1) {
                            updatedCards[firstCardIndex] = updatedCards[firstCardIndex].copy(isFlipped = false, isSelected = false)
                        }

                        if (secondCardIndex != -1) {
                            updatedCards[secondCardIndex] = updatedCards[secondCardIndex].copy(isFlipped = false, isSelected = false)
                        }

                        _cardStates.value = updatedCards

                        // Reset for next selection
                        firstSelectedCard = null
                        isProcessingMatch = false
                    }
                }
            }
        }
    }

    /**
     * Resets all unmatched cards to their default state (not flipped).
     * Matched cards remain flipped.
     */
    fun resetCards() {
        val currentCards = _cardStates.value.map { card ->
            if (matchedCardIds.contains(card.id)) {
                // Keep matched cards flipped
                card
            } else {
                // Reset unmatched cards
                card.copy(isFlipped = false, isSelected = false)
            }
        }
        _cardStates.value = currentCards

        // Reset the first selected card if there is one
        firstSelectedCard = null
        isProcessingMatch = false
    }

    /**
     * Resets the entire game, including matched cards.
     * Use this to start a new game.
     */
    fun resetGame() {
        matchedCardIds.clear()
        firstSelectedCard = null
        isProcessingMatch = false
        _isWinAnimationInProgress.value = false
        _currentlyAnimatingCardId.value = -1

        // If a difficulty is already selected, initialize cards with that difficulty
        // Otherwise, return to difficulty selection screen
        if (_selectedDifficulty.value != null) {
            initializeCards()
        } else {
            // Clear the card states if no difficulty is selected
            _cardStates.value = emptyList()
        }
    }

    /**
     * Resets the game and returns to the difficulty selection screen.
     * Use this to change the difficulty level.
     */
    fun resetGameAndDifficulty() {
        matchedCardIds.clear()
        firstSelectedCard = null
        isProcessingMatch = false
        _isGameWon.value = false
        _guessAttempts.value = 0
        _selectedDifficulty.value = null
        _cardStates.value = emptyList()
        _isWinAnimationInProgress.value = false
        _currentlyAnimatingCardId.value = -1
    }

    /**
     * Starts the win animation sequence.
     * Animates all cards to scale in and out from first card to last card then back.
     * Each card animation finishes before the next card animation starts.
     * Animation duration is 200ms per card.
     * The animation loops continuously until the game is reset.
     */
    private fun startWinAnimation() {
        viewModelScope.launch {
            _isWinAnimationInProgress.value = true

            val cards = _cardStates.value
            if (cards.isEmpty()) return@launch

            // Loop the animation until the game is reset
            while (_isWinAnimationInProgress.value) {
                // First, animate from first card to last card
                for (card in cards) {
                    animateCard(card.id)
                    delay(200) // Wait for the animation to complete
                    // If animation was stopped, exit the loop
                    if (!_isWinAnimationInProgress.value) break
                }

                // If animation was stopped, exit the loop
                if (!_isWinAnimationInProgress.value) break

                // Then, animate from last card to first card
                for (card in cards.reversed()) {
                    animateCard(card.id)
                    delay(200) // Wait for the animation to complete
                    // If animation was stopped, exit the loop
                    if (!_isWinAnimationInProgress.value) break
                }
            }

            // Reset the currently animating card ID when the animation stops
            _currentlyAnimatingCardId.value = -1
        }
    }

    /**
     * Animates a single card by setting its ID as the currently animating card.
     * The actual animation is handled in the FlipCard composable.
     * @param cardId The ID of the card to animate.
     */
    private fun animateCard(cardId: Int) {
        _currentlyAnimatingCardId.value = cardId
    }
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
    val isAnimatingWin: Boolean = false
)
