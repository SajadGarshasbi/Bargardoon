package com.kotlini.app

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlini.composeapp.generated.resources.Res
import kotlini.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
// Import the haptic feedback functions from the shared module
import com.kotlini.app.getHapticFeedback
import com.kotlini.app.isMobileDevice

@Composable
fun FlipCard(
    index: Int,
    flowerIndex: Int,
    isFlipped: Boolean,
    isSelected: Boolean = false,
    isAnimatingWin: Boolean = false,
    onFlip: () -> Unit
) {
    val cardRotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "flip"
    )

    // Scale animation for win animation
    val scale by animateFloatAsState(
        targetValue = if (isAnimatingWin) 1.2f else 1f,
        animationSpec = tween(durationMillis = 200),
        label = "scale"
    )

    // Vibrate when animations occur (only on mobile devices)
    LaunchedEffect(isFlipped, isAnimatingWin) {
        if (isMobileDevice()) {
            // Vibrate when card is flipped or during win animation
            if (isAnimatingWin) {
                getHapticFeedback().vibrate(20)
            } else {
                // Only vibrate on flip to face-up, not on flip back
                if (isFlipped) {
                    getHapticFeedback().vibrate(20)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(16.dp))
            .graphicsLayer {
                rotationY = cardRotation
                cameraDistance = 12 * density
                scaleX = scale
                scaleY = scale
            }
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 3.dp,
                        color = Color.Red,
                        shape = RoundedCornerShape(16.dp)
                    )
                } else {
                    Modifier
                }
            )
            .clickable { onFlip() }
    ) {
        if (cardRotation <= 90f) {
            FrontCard((index + 1).toString())
        } else {
            BackCard(flowerIndex)
        }
    }
}

@Composable
fun FrontCard(title: String) {
    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun BackCard(index: Int) {
    Surface(
        color = MaterialTheme.colorScheme.secondary,
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .graphicsLayer {
                rotationY = 180f
            }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painterResource(FLOWERS[index]),
                contentDescription = "",
                modifier = Modifier.fillMaxSize().padding(8.dp)
            )
        }
    }
}


val FLOWERS = listOf(
    Res.drawable.flower1,
    Res.drawable.flower2,
    Res.drawable.flower3,
    Res.drawable.flower4,
    Res.drawable.flower5,
    Res.drawable.flower6,
    Res.drawable.flower7,
    Res.drawable.flower8,
    Res.drawable.flower9,
    Res.drawable.flower10,
    Res.drawable.flower11,
    Res.drawable.flower12,
    Res.drawable.flower13,
    Res.drawable.flower14,
    Res.drawable.flower15,
    Res.drawable.flower16,
    Res.drawable.flower17,
    Res.drawable.flower18,
    Res.drawable.flower19,
    Res.drawable.flower20,
    Res.drawable.flower21,
)
