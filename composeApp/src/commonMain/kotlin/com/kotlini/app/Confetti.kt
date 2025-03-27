package com.kotlini.app

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun EndlessConfetti() {
    val particles = remember { mutableStateListOf<ConfettiParticle>() }
    val colors = remember {
        listOf(
            Color(0xFFE68F8F), // Light Red
            Color(0xFFE6C38F), // Light Orange
            Color(0xFFE6E68F), // Light Yellow
            Color(0xFF8FE68F), // Light Green
            Color(0xFF8FE6E6), // Light Cyan
            Color(0xFF8F8FE6), // Light Blue
            Color(0xFFE68FE6)  // Light Purple
        )
    }

    // Generate new particles continuously
    LaunchedEffect(Unit) {
        while (true) {
            // Add new particles
            repeat(3) { // Add 3 particles at a time
                particles.add(
                    ConfettiParticle(
                        x = Random.nextFloat() * 1000,
                        y = -50f, // Start above the screen
                        velocityX = Random.nextFloat() * 6 - 3, // Random horizontal velocity
                        velocityY = Random.nextFloat() * 3 + 2, // Downward velocity
                        rotation = Random.nextFloat() * 360,
                        rotationSpeed = Random.nextFloat() * 10 - 5,
                        size = Random.nextFloat() * 15 + 5,
                        color = colors.random()
                    )
                )
            }

            // Remove particles that are off-screen
            if (particles.size > 300) { // Limit the number of particles
                particles.removeAt(0)
            }

            delay(25) // Wait before adding more particles
        }
    }

    // Update particle positions
    LaunchedEffect(Unit) {
        while (true) {
            particles.forEachIndexed { index, particle ->
                particles[index] = particle.copy(
                    x = particle.x + particle.velocityX,
                    y = particle.y + particle.velocityY,
                    rotation = (particle.rotation + particle.rotationSpeed) % 360
                )
            }
            delay(16) // Update at approximately 60fps
        }
    }

    // Draw the particles
    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            drawConfetti(particle)
        }
    }
}

private fun DrawScope.drawConfetti(particle: ConfettiParticle) {
    rotate(particle.rotation, Offset(particle.x, particle.y)) {
        drawRect(
            color = particle.color,
            topLeft = Offset(particle.x - particle.size / 2, particle.y - particle.size / 2),
            size = androidx.compose.ui.geometry.Size(particle.size, particle.size)
        )
    }
}

data class ConfettiParticle(
    val x: Float,
    val y: Float,
    val velocityX: Float,
    val velocityY: Float,
    val rotation: Float,
    val rotationSpeed: Float,
    val size: Float,
    val color: Color
)
