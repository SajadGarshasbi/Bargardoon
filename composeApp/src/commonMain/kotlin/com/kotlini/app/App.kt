package com.kotlini.app

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kotlini.app.theme.KotliniTheme
import com.kotlini.app.viewmodel.CardState
import com.kotlini.app.viewmodel.GameDifficulty
import com.kotlini.app.viewmodel.MainViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
// Import the FlipCard component from the new file
import com.kotlini.app.FlipCard
// Import the openUrl function for opening the LinkedIn URL
import com.kotlini.app.openUrl
import kotlini.composeapp.generated.resources.Res
import kotlini.composeapp.generated.resources.ic_baseline_open_in_new_24
import org.jetbrains.compose.resources.painterResource

@Composable
@Preview
fun App() {
    KotliniTheme {
        Box(Modifier.fillMaxSize().padding(16.dp)) {
            MyNavHost()
        }
    }
}

@Composable
fun MyNavHost() {
    val nc = rememberNavController()
    val mainViewModel: MainViewModel = viewModel { MainViewModel() }

    NavHost(
        navController = nc,
        startDestination = "difficulty",
    ) {
        composable("difficulty") {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                DifficultySelectionScreen(
                    mainViewModel = mainViewModel,
                    onDifficultySelected = { difficulty ->
                        mainViewModel.setDifficulty(difficulty)
                        nc.navigate("game")
                    }
                )
            }
        }

        composable("game") {
            val cardStates by mainViewModel.cardStates.collectAsState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                GameScreen(
                    mainViewModel = mainViewModel,
                    cardStates = cardStates,
                    onBackClicked = {
                        mainViewModel.resetGameAndDifficulty()
                        nc.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
fun DifficultySelectionScreen(
    mainViewModel: MainViewModel,
    onDifficultySelected: (GameDifficulty) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bargardoon!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "Select Difficulty Level",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
        )

        Button(
            onClick = { onDifficultySelected(GameDifficulty.EASY) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text("Easy (8 cards)")
        }

        Button(
            onClick = { onDifficultySelected(GameDifficulty.MEDIUM) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text("Medium (16 cards)")
        }

        Button(
            onClick = { onDifficultySelected(GameDifficulty.HARD) },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text("Hard (24 cards)")
        }

        Spacer(modifier = Modifier.padding(vertical = 12.dp))

        Text(
            text = "Created by Sajad Garshasbi \n Powered by JetBrains Junie",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.padding(vertical = 8.dp))

        Button(
            onClick = { openUrl("https://www.linkedin.com/in/sajad-garshasbi-a0b2b395/") },
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Sajad Garshasbi")
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Icon(
                    painter = painterResource(Res.drawable.ic_baseline_open_in_new_24),
                    contentDescription = "Link",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun GameScreen(
    mainViewModel: MainViewModel,
    cardStates: List<CardState>,
    onBackClicked: () -> Unit
) {
    // Collect the game state at the top level
    val isGameWon by mainViewModel.isGameWon.collectAsState()
    val currentlyAnimatingCardId by mainViewModel.currentlyAnimatingCardId.collectAsState()

    // Use a Box to position the confetti over everything else
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Back button at the top (keeping this at the top for navigation purposes)
            Button(onClick = onBackClicked, modifier = Modifier.padding(bottom = 8.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }

            // Set the number of columns to 4 for all difficulty levels
            val columns = 4

            // Cards grid without weight to ensure it only takes the space it needs
            LazyVerticalGrid(
                GridCells.Fixed(columns),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(cardStates.size) { index ->
                    val cardState = cardStates[index]
                    Box(modifier = Modifier.padding(4.dp)) {
                        FlipCard(
                            index = cardState.id,
                            flowerIndex = cardState.flowerIndex,
                            isFlipped = cardState.isFlipped,
                            isSelected = cardState.isSelected,
                            isAnimatingWin = cardState.id == currentlyAnimatingCardId,
                            onFlip = { mainViewModel.toggleCardFlip(cardState.id) }
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
                val isInPreviewMode by mainViewModel.isInPreviewMode.collectAsState()
                val countdownSeconds by mainViewModel.countdownSeconds.collectAsState()
                val guessAttempts by mainViewModel.guessAttempts.collectAsState()

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
                            text = "Memorize the cards!",
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
                                text = "Game starts in ",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )

                            // Animated countdown number
                            Text(
                                text = "$countdownSeconds",
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
                                text = " seconds...",
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
                        text = "Congratulations! You won in $guessAttempts attempts!",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }

                // Row with attempts counter and New Game button - only show when not in preview mode
                if (!isInPreviewMode) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Display guess attempts counter
                        Text(
                            text = "Attempts: $guessAttempts",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Button(onClick = {
                            mainViewModel.resetGame()
                        }) {
                            Text("New Game")
                        }
                    }
                }
            }
        }

    }
}
