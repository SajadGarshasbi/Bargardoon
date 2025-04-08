package com.kotlini.app.viewmodel

import androidx.lifecycle.ViewModel
import com.kotlini.app.FLOWERS
import com.kotlini.app.getHapticFeedback
import com.kotlini.app.isMobileDevice
import com.kotlini.app.localization.LocalizationManager
import com.kotlini.app.localization.StringKey
import androidx.compose.runtime.State
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the game screen.
 * Manages the state and logic for the game, including the list of cards and their flipped state.
 * This implementation is platform-independent and works across all platforms, including Wasm.
 */
class GameViewModel : ViewModel() {

    // Create a custom CoroutineScope for this ViewModel
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // Localization manager for handling translations
    private val localizationManager = LocalizationManager.getInstance()

    // Expose the current language as a State
    val currentLanguage: State<Language> = localizationManager.currentLanguage

    /**
     * Gets a localized string for the given key.
     * @param key The key of the string to translate.
     * @return The translated string based on the current language.
     */
    fun getString(key: StringKey): String {
        return localizationManager.getString(key)
    }

    /**
     * Gets a formatted localized string for the given key with an integer argument.
     * @param key The key of the string to translate.
     * @param value The integer value to insert into the string.
     * @return The translated and formatted string based on the current language.
     */
    fun getFormattedStringWithInt(key: StringKey, value: Int): String {
        val template = localizationManager.getString(key)
        val valueStr = localizationManager.convertToPersianDigits(value.toString())
        return template.replace("%d", valueStr)
    }

    /**
     * Gets a formatted localized string for the given key with a string argument.
     * @param key The key of the string to translate.
     * @param value The string value to insert into the string.
     * @return The translated and formatted string based on the current language.
     */
    fun getFormattedStringWithString(key: StringKey, value: String): String {
        val template = localizationManager.getString(key)
        val valueStr = localizationManager.convertToPersianDigits(value)
        return template.replace("%s", valueStr)
    }

    /**
     * Converts a number to a string with Persian digits if the current language is Persian.
     * @param number The number to convert.
     * @return The number as a string with Persian digits if needed.
     */
    fun convertToPersianDigits(number: Int): String {
        return localizationManager.convertToPersianDigits(number.toString())
    }

    /**
     * Formats time as minutes:seconds with Persian digits if the current language is Persian.
     * @param totalSeconds The total time in seconds.
     * @return The formatted time string with Persian digits if needed.
     */
    fun formatTimeWithPersianDigits(totalSeconds: Int): String {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        val formattedTime = "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
        return localizationManager.convertToPersianDigits(formattedTime)
    }

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

    // Selected difficulty
    private val _selectedDifficulty = MutableStateFlow<GameDifficulty?>(null)
    val selectedDifficulty: StateFlow<GameDifficulty?> = _selectedDifficulty.asStateFlow()

    // Win animation state
    private val _isWinAnimationInProgress = MutableStateFlow(false)
    val isWinAnimationInProgress: StateFlow<Boolean> = _isWinAnimationInProgress.asStateFlow()

    // Currently animating card ID (-1 means no card is being animated)
    private val _currentlyAnimatingCardId = MutableStateFlow(-1)
    val currentlyAnimatingCardId: StateFlow<Int> = _currentlyAnimatingCardId.asStateFlow()

    // Hint state - tracks whether the hint is currently being shown
    private val _isHintUsed = MutableStateFlow(false)
    val isHintUsed: StateFlow<Boolean> = _isHintUsed.asStateFlow()

    // Hint cards - stores the IDs of cards being shown as hints
    private val _hintCardIds = MutableStateFlow<List<Int>>(emptyList())
    val hintCardIds: StateFlow<List<Int>> = _hintCardIds.asStateFlow()

    // Hint counter - tracks how many hints have been used in the current game
    private val _hintCounter = MutableStateFlow(0)
    val hintCounter: StateFlow<Int> = _hintCounter.asStateFlow()

    // Game timer - tracks elapsed time in seconds since the game started
    private val _gameTimeSeconds = MutableStateFlow(0)
    val gameTimeSeconds: StateFlow<Int> = _gameTimeSeconds.asStateFlow()

    // Next hint available time - tracks when the next hint will be available (in game seconds)
    private val _nextHintAvailableTime = MutableStateFlow(0)
    val nextHintAvailableTime: StateFlow<Int> = _nextHintAvailableTime.asStateFlow()

    // Game state variables
    private var firstSelectedCard: CardState? = null
    private var isProcessingMatch = false
    private val matchedCardIds = mutableSetOf<Int>()

    /**
     * Initializes the game with the selected difficulty.
     * @param difficulty The difficulty level to set.
     */
    fun initializeGame(difficulty: GameDifficulty) {
        _selectedDifficulty.value = difficulty
        initializeCards()
    }

    /**
     * Initializes the list of cards with their default state based on the selected difficulty.
     */
    private fun initializeCards() {
        val difficulty = _selectedDifficulty.value ?: return

        viewModelScope.launch {
            // Reset game state
            _isInPreviewMode.value = true
            _countdownSeconds.value = 8
            _guessAttempts.value = 0
            _isGameWon.value = false
            _isWinAnimationInProgress.value = false
            _currentlyAnimatingCardId.value = -1
            _isHintUsed.value = false
            _hintCardIds.value = emptyList()
            _hintCounter.value = 0
            _gameTimeSeconds.value = 0
            _nextHintAvailableTime.value = 0
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
     */
    private fun startPreviewCountdown() {
        viewModelScope.launch {
            val initialCountdown = _countdownSeconds.value
            for (i in initialCountdown downTo 1) {
                _countdownSeconds.value = i
                delay(1000)
            }

            // Flip all cards face down
            val updatedCards = _cardStates.value.map { it.copy(isFlipped = false) }
            _cardStates.value = updatedCards

            // End preview mode
            _isInPreviewMode.value = false

            // Start the game timer
            startGameTimer()
        }
    }

    /**
     * Starts the game timer.
     */
    private fun startGameTimer() {
        viewModelScope.launch {
            _gameTimeSeconds.value = 0
            while (!_isGameWon.value && !_isInPreviewMode.value) {
                delay(1000)
                _gameTimeSeconds.value = _gameTimeSeconds.value + 1
            }
        }
    }

    /**
     * Handles the card selection logic for the memory game.
     * @param cardId The ID of the card to toggle.
     */
    fun toggleCardFlip(cardId: Int) {
        if (_isInPreviewMode.value || isProcessingMatch || matchedCardIds.contains(cardId)) {
            return
        }

        val currentCards = _cardStates.value.toMutableList()
        val cardIndex = currentCards.indexOfFirst { it.id == cardId }

        if (cardIndex != -1) {
            val card = currentCards[cardIndex]
            if (card.isFlipped) return

            currentCards[cardIndex] = card.copy(isFlipped = true, isSelected = true)
            _cardStates.value = currentCards

            if (firstSelectedCard == null) {
                firstSelectedCard = card
            } else {
                val firstCard = firstSelectedCard!!
                _guessAttempts.value = _guessAttempts.value + 1
                isProcessingMatch = true

                if (firstCard.flowerIndex == card.flowerIndex) {
                    // Cards match
                    matchedCardIds.add(firstCard.id)
                    matchedCardIds.add(card.id)

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

                    // Check if all cards are matched
                    val totalCards = _selectedDifficulty.value?.pairCount?.times(2) ?: 0
                    if (matchedCardIds.size == totalCards) {
                        _isGameWon.value = true
                        startWinAnimation()
                    }

                    firstSelectedCard = null
                    isProcessingMatch = false
                } else {
                    // Cards don't match
                    viewModelScope.launch {
                        // First set the wrong guess flag to trigger the animation
                        val wrongGuessCards = _cardStates.value.toMutableList()
                        val firstCardIndex = wrongGuessCards.indexOfFirst { it.id == firstCard.id }
                        val secondCardIndex = wrongGuessCards.indexOfFirst { it.id == card.id }

                        if (firstCardIndex != -1) {
                            wrongGuessCards[firstCardIndex] = wrongGuessCards[firstCardIndex].copy(isWrongGuess = true)
                        }
                        if (secondCardIndex != -1) {
                            wrongGuessCards[secondCardIndex] = wrongGuessCards[secondCardIndex].copy(isWrongGuess = true)
                        }
                        _cardStates.value = wrongGuessCards

                        // Wait for the animation to complete (1 second)
                        delay(1000)

                        if (isMobileDevice()) {
                            getHapticFeedback().vibrate(20)
                        }

                        // Reset the cards
                        val updatedCards = _cardStates.value.toMutableList()
                        val resetFirstCardIndex = updatedCards.indexOfFirst { it.id == firstCard.id }
                        val resetSecondCardIndex = updatedCards.indexOfFirst { it.id == card.id }

                        if (resetFirstCardIndex != -1) {
                            updatedCards[resetFirstCardIndex] = updatedCards[resetFirstCardIndex].copy(isFlipped = false, isSelected = false, isWrongGuess = false)
                        }
                        if (resetSecondCardIndex != -1) {
                            updatedCards[resetSecondCardIndex] = updatedCards[resetSecondCardIndex].copy(isFlipped = false, isSelected = false, isWrongGuess = false)
                        }

                        _cardStates.value = updatedCards
                        firstSelectedCard = null
                        isProcessingMatch = false
                    }
                }
            }
        }
    }

    /**
     * Resets the entire game with the same difficulty.
     */
    fun resetGame() {
        matchedCardIds.clear()
        firstSelectedCard = null
        isProcessingMatch = false
        _isWinAnimationInProgress.value = false
        _currentlyAnimatingCardId.value = -1
        _isHintUsed.value = false
        _hintCardIds.value = emptyList()
        _hintCounter.value = 0
        _gameTimeSeconds.value = 0
        _nextHintAvailableTime.value = 0

        if (_selectedDifficulty.value != null) {
            initializeCards()
        } else {
            _cardStates.value = emptyList()
        }
    }

    /**
     * Resets the game and clears the selected difficulty.
     * Use this when returning to the difficulty selection screen.
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
        _isHintUsed.value = false
        _hintCardIds.value = emptyList()
        _hintCounter.value = 0
        _gameTimeSeconds.value = 0
        _nextHintAvailableTime.value = 0
    }

    /**
     * Starts the win animation sequence.
     */
    private fun startWinAnimation() {
        viewModelScope.launch {
            _isWinAnimationInProgress.value = true
            val cards = _cardStates.value
            if (cards.isEmpty()) return@launch

            while (_isWinAnimationInProgress.value) {
                for (card in cards) {
                    _currentlyAnimatingCardId.value = card.id
                    delay(200)
                    if (!_isWinAnimationInProgress.value) break
                }
                if (!_isWinAnimationInProgress.value) break
                for (card in cards.reversed()) {
                    _currentlyAnimatingCardId.value = card.id
                    delay(200)
                    if (!_isWinAnimationInProgress.value) break
                }
            }
            _currentlyAnimatingCardId.value = -1
        }
    }

    /**
     * Shows a hint by temporarily revealing one pair of unmatched cards.
     */
    fun showHint() {
        if (_isHintUsed.value || _isInPreviewMode.value || _isGameWon.value) {
            return
        }

        if (_gameTimeSeconds.value < _nextHintAvailableTime.value) {
            return
        }

        val currentCards = _cardStates.value
        val unmatchedPairs = mutableListOf<Pair<CardState, CardState>>()
        val cardsByFlower = currentCards.filterNot { matchedCardIds.contains(it.id) }
            .groupBy { it.flowerIndex }

        for ((_, cards) in cardsByFlower) {
            if (cards.size == 2) {
                unmatchedPairs.add(Pair(cards[0], cards[1]))
            }
        }

        if (unmatchedPairs.size >= 1) {
            val selectedPair = unmatchedPairs.random()
            val hintCards = listOf(selectedPair.first, selectedPair.second)
            val hintCardIds = hintCards.map { it.id }
            _hintCardIds.value = hintCardIds

            val updatedCards = currentCards.map { card ->
                if (hintCardIds.contains(card.id)) {
                    card.copy(isFlipped = true)
                } else {
                    card
                }
            }
            _cardStates.value = updatedCards

            _hintCounter.value = _hintCounter.value + 1
            val cooldownTime = 15 // seconds
            _nextHintAvailableTime.value = _gameTimeSeconds.value + cooldownTime
            _isHintUsed.value = true

            viewModelScope.launch {
                // Hint duration is 1/10 of the cooldown time
                val hintDuration = cooldownTime * 1000 / 10 // convert to milliseconds and divide by 10
                delay(hintDuration.toLong())
                val revertedCards = _cardStates.value.map { card ->
                    if (hintCardIds.contains(card.id) && !matchedCardIds.contains(card.id)) {
                        card.copy(isFlipped = false)
                    } else {
                        card
                    }
                }
                _cardStates.value = revertedCards
                _hintCardIds.value = emptyList()
                _isHintUsed.value = false
            }
        }
    }
}
