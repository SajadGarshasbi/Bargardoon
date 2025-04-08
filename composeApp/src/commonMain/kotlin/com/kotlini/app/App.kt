package com.kotlini.app

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kotlini.app.localization.LocalizationManager
import com.kotlini.app.openUrl
import com.kotlini.app.settings.Settings
import com.kotlini.app.theme.BargardoonTheme
import com.kotlini.app.viewmodel.DifficultySelectionViewModel
import com.kotlini.app.viewmodel.GameDifficulty
import com.kotlini.app.viewmodel.GameViewModel
import com.kotlini.app.viewmodel.Language
import com.kotlini.app.localization.StringKey
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlini.composeapp.generated.resources.Res
import kotlini.composeapp.generated.resources.ic_baseline_open_in_new_24
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.unit.LayoutDirection

@Composable
@Preview
fun App() {
    // Initialize settings to load saved preferences
    Settings.initialize()

    // Get the current language from LocalizationManager
    val localizationManager = LocalizationManager.getInstance()
    val currentLanguage by localizationManager.currentLanguage

    // Set layout direction based on language
    val layoutDirection = if (currentLanguage == Language.PERSIAN) {
        LayoutDirection.Rtl
    } else {
        LayoutDirection.Ltr
    }

    // Provide the layout direction to the entire app
    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        BargardoonTheme {
            Box(Modifier.fillMaxSize().padding(16.dp)) {
                MyNavHost()
            }
        }
    }
}

@Composable
fun MyNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "difficulty",
    ) {
        composable("difficulty") {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                DifficultySelectionScreen(
                    onDifficultySelected = { difficulty ->
                        // Navigate to game screen with the selected difficulty
                        when (difficulty) {
                            GameDifficulty.EASY -> navController.navigate("game_easy")
                            GameDifficulty.MEDIUM -> navController.navigate("game_medium")
                            GameDifficulty.HARD -> navController.navigate("game_hard")
                        }
                    }
                )
            }
        }

        composable("game_easy") {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                GameScreen(
                    difficulty = GameDifficulty.EASY,
                    onBackClicked = {
                        navController.popBackStack()
                    }
                )
            }
        }

        composable("game_medium") {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                GameScreen(
                    difficulty = GameDifficulty.MEDIUM,
                    onBackClicked = {
                        navController.popBackStack()
                    }
                )
            }
        }

        composable("game_hard") {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                GameScreen(
                    difficulty = GameDifficulty.HARD,
                    onBackClicked = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
fun DifficultySelectionScreen(
    onDifficultySelected: (GameDifficulty) -> Unit
) {
    // Initialize the ViewModel inside the composable
    val difficultyViewModel: DifficultySelectionViewModel = viewModel { DifficultySelectionViewModel() }

    // Observe the current language
    val currentLanguage by difficultyViewModel.currentLanguage

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Language selection button at the top
        var showLanguageDialog by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { showLanguageDialog = true },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                // Use a settings icon to represent language selection
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = difficultyViewModel.getString(StringKey.SELECT_LANGUAGE),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Language selection dialog
        if (showLanguageDialog) {
            AlertDialog(
                onDismissRequest = { showLanguageDialog = false },
                title = {
                    Text(
                        text = difficultyViewModel.getString(StringKey.SELECT_LANGUAGE),
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                text = {
                    Column {
                        TextButton(
                            onClick = {
                                difficultyViewModel.setLanguage(Language.ENGLISH)
                                showLanguageDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = difficultyViewModel.getNativeLanguageName(Language.ENGLISH),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        TextButton(
                            onClick = {
                                difficultyViewModel.setLanguage(Language.PERSIAN)
                                showLanguageDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = difficultyViewModel.getNativeLanguageName(Language.PERSIAN),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                },
                confirmButton = { }
            )
        }

        Text(
            text = difficultyViewModel.getString(StringKey.APP_TITLE),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = difficultyViewModel.getString(StringKey.SELECT_DIFFICULTY),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
        )

        Button(
            onClick = { onDifficultySelected(GameDifficulty.EASY) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text(
                text = difficultyViewModel.getString(StringKey.EASY),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Button(
            onClick = { onDifficultySelected(GameDifficulty.MEDIUM) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text(
                text = difficultyViewModel.getString(StringKey.MEDIUM),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Button(
            onClick = { onDifficultySelected(GameDifficulty.HARD) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text(
                text = difficultyViewModel.getString(StringKey.HARD),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.padding(vertical = 12.dp))

        Text(
            text = difficultyViewModel.getString(StringKey.CREATED_BY),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.padding(vertical = 8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { openUrl("https://www.linkedin.com/in/sajad-garshasbi-a0b2b395/") },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = difficultyViewModel.getString(StringKey.ME),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Icon(
                        painter = painterResource(Res.drawable.ic_baseline_open_in_new_24),
                        contentDescription = "Link",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Button(
                onClick = { openUrl("https://github.com/SajadGarshasbi/Bargardoon") },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = difficultyViewModel.getString(StringKey.GITHUB),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Icon(
                        painter = painterResource(Res.drawable.ic_baseline_open_in_new_24),
                        contentDescription = "Link",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun GameScreen(
    difficulty: GameDifficulty,
    onBackClicked: () -> Unit
) {
    // Initialize the ViewModel inside the composable
    val gameViewModel: GameViewModel = viewModel { GameViewModel() }

    // Initialize the game with the provided difficulty
    LaunchedEffect(difficulty) {
        gameViewModel.initializeGame(difficulty)
    }

    // Create a wrapped onBackClicked that resets the game state before navigating back
    val wrappedOnBackClicked = {
        gameViewModel.resetGameAndDifficulty()
        onBackClicked()
    }

    // Collect the card states
    val cardStates by gameViewModel.cardStates.collectAsState()
    // Collect the game state at the top level
    val isGameWon by gameViewModel.isGameWon.collectAsState()
    val currentlyAnimatingCardId by gameViewModel.currentlyAnimatingCardId.collectAsState()
    val hintCardIds by gameViewModel.hintCardIds.collectAsState()

    // Use a Box to position the confetti and toast over everything else
    Box(modifier = Modifier.fillMaxSize()) {
        // Show confetti when the game is won

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top row with back button on left and hint button on right
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back button on the left
                Button(onClick = wrappedOnBackClicked) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = gameViewModel.getString(StringKey.BACK)
                    )
                }

                // Hint button on the right - only show when not in preview mode
                val isInPreviewMode by gameViewModel.isInPreviewMode.collectAsState()
                val isHintUsed by gameViewModel.isHintUsed.collectAsState()
                val gameTimeSeconds by gameViewModel.gameTimeSeconds.collectAsState()
                val nextHintAvailableTime by gameViewModel.nextHintAvailableTime.collectAsState()

                // Calculate if hint is available based on cooldown
                val isHintAvailable = !isHintUsed && !isGameWon && gameTimeSeconds >= nextHintAvailableTime

                if (!isInPreviewMode) {
                    Button(
                        onClick = { gameViewModel.showHint() },
                        enabled = isHintAvailable
                    ) {
                        Text(
                            text = gameViewModel.getString(StringKey.HINT),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Set the number of columns to 4 for all difficulty levels
            val columns = 4

            // Collect the preview mode state
            val isInPreviewMode by gameViewModel.isInPreviewMode.collectAsState()

            // Cards grid without weight to ensure it only takes the space it needs
            LazyVerticalGrid(
                GridCells.Fixed(columns),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(cardStates.size) { index ->
                    val cardState = cardStates[index]
                    // Check if this card is a hint card
                    val isHintCard = hintCardIds.contains(cardState.id)

                    // Animation values for hint cards
                    val infiniteTransition = rememberInfiniteTransition(label = "hintCardAnimation")

                    // Scale animation for hint cards
                    val scale by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.2f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(500, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "hintScale"
                    )

                    // Rotation animation for hint cards
                    val rotation by infiniteTransition.animateFloat(
                        initialValue = -5f,
                        targetValue = 5f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(500, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "hintRotation"
                    )

                    // Bounce animation for hint cards
                    val offsetY by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 5f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(300, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "hintBounce"
                    )

                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .graphicsLayer {
                                // Apply animations only to hint cards
                                if (isHintCard) {
                                    scaleX = scale
                                    scaleY = scale
                                    rotationZ = rotation
                                    translationY = offsetY
                                }
                            }
                    ) {
                        FlipCard(
                            index = cardState.id,
                            flowerIndex = cardState.flowerIndex,
                            isFlipped = cardState.isFlipped,
                            isSelected = cardState.isSelected,
                            isAnimatingWin = cardState.id == currentlyAnimatingCardId,
                            isInPreviewMode = isInPreviewMode,
                            isWrongGuess = cardState.isWrongGuess,
                            onFlip = { gameViewModel.toggleCardFlip(cardState.id) }
                        )
                    }
                }
            }

            // Bottom section with game status and controls
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp), // Add padding for visual separation
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Collect the preview mode and game state
                val isInPreviewMode by gameViewModel.isInPreviewMode.collectAsState()
                val countdownSeconds by gameViewModel.countdownSeconds.collectAsState()
                val guessAttempts by gameViewModel.guessAttempts.collectAsState()

                // Display countdown timer when in preview mode
                if (isInPreviewMode) {
                    // State to track whether we're scaling up or down
                    var isScaledUp by remember { mutableStateOf(false) }

                    // Animate scale based on isScaledUp state
                    val scale by animateFloatAsState(
                        targetValue = if (isScaledUp) 1.5f else 1.0f, // Animate between normal and 1.5x size
                        animationSpec = tween(durationMillis = 200), // Animation duration (faster for more responsive feel)
                        label = "countdownScale"
                    )

                    // Toggle scale state when countdown changes
                    LaunchedEffect(countdownSeconds) {
                        isScaledUp = true // Start with scaling up
                        kotlinx.coroutines.delay(100) // Wait briefly
                        isScaledUp = false // Then scale back down
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Memorize message that can be multiline
                        Text(
                            text = gameViewModel.getString(StringKey.MEMORIZE_CARDS),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            lineHeight = 24.sp // Ensures proper spacing for multiline text
                        )

                        // Timer on a separate line
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = gameViewModel.getString(StringKey.GAME_STARTS_IN),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )

                            // Animated countdown number
                            Text(
                                text = gameViewModel.convertToPersianDigits(countdownSeconds),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.graphicsLayer {
                                    // Apply the animated scale
                                    scaleX = scale
                                    scaleY = scale
                                },
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = gameViewModel.getString(StringKey.SECONDS),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Display win message when all cards are matched
                if (isGameWon) {
                    Text(
                        text = gameViewModel.getFormattedStringWithInt(StringKey.CONGRATULATIONS, guessAttempts),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }

                // Row with attempts counter and Hint button - only show when not in preview mode
                if (!isInPreviewMode) {
                    // Collect game state
                    val isHintUsed by gameViewModel.isHintUsed.collectAsState()
                    val hintCounter by gameViewModel.hintCounter.collectAsState()
                    val gameTimeSeconds by gameViewModel.gameTimeSeconds.collectAsState()
                    val nextHintAvailableTime by gameViewModel.nextHintAvailableTime.collectAsState()

                    // Format the game time as minutes:seconds with Persian digits if needed
                    val formattedTime = gameViewModel.formatTimeWithPersianDigits(gameTimeSeconds)

                    // Calculate cooldown time remaining
                    val cooldownRemaining = (nextHintAvailableTime - gameTimeSeconds).coerceAtLeast(0)

                    // Game stats row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Game stats column
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                        ) {
                            // Display game timer
                            Text(
                                text = gameViewModel.getFormattedStringWithString(StringKey.TIME, formattedTime),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            // Display guess attempts counter
                            Text(
                                text = gameViewModel.getFormattedStringWithInt(StringKey.ATTEMPTS, guessAttempts),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            // Display hint counter and cooldown
                            Text(
                                text = gameViewModel.getFormattedStringWithInt(StringKey.HINTS, hintCounter) + 
                                       if (cooldownRemaining > 0) gameViewModel.getFormattedStringWithInt(StringKey.NEXT, cooldownRemaining) else "",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                    }
                }
            }
        }

        if (isGameWon) {
            EndlessConfetti()
        }

    }
}
