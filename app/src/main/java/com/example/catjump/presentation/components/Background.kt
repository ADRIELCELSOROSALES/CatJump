package com.example.catjump.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import kotlin.math.sin

@Composable
fun GameBackground(
    cameraY: Float,
    modifier: Modifier = Modifier
) {
    val skyColorTop = Color(0xFF1a237e)
    val skyColorMiddle = Color(0xFF3949ab)
    val skyColorBottom = Color(0xFF7986cb)

    Canvas(modifier = modifier.fillMaxSize()) {
        // Gradient sky background
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(skyColorTop, skyColorMiddle, skyColorBottom)
            )
        )

        // Draw stars that move with camera
        val starColor = Color.White.copy(alpha = 0.7f)
        val starPositions = listOf(
            Pair(0.1f, 0.1f), Pair(0.3f, 0.15f), Pair(0.5f, 0.05f), Pair(0.7f, 0.2f), Pair(0.9f, 0.08f),
            Pair(0.15f, 0.3f), Pair(0.4f, 0.25f), Pair(0.6f, 0.35f), Pair(0.85f, 0.28f),
            Pair(0.2f, 0.45f), Pair(0.45f, 0.5f), Pair(0.75f, 0.42f),
            Pair(0.05f, 0.6f), Pair(0.35f, 0.65f), Pair(0.55f, 0.58f), Pair(0.8f, 0.7f),
            Pair(0.25f, 0.8f), Pair(0.5f, 0.85f), Pair(0.7f, 0.75f), Pair(0.95f, 0.9f)
        )

        starPositions.forEachIndexed { index, (xRatio, yRatio) ->
            val parallaxFactor = 0.1f + (index % 3) * 0.05f
            val yOffset = (cameraY * parallaxFactor) % size.height

            val x = xRatio * size.width
            var y = (yRatio * size.height + yOffset) % size.height
            if (y < 0) y += size.height

            val twinkle = (sin(cameraY * 0.01f + index) * 0.3f + 0.7f).coerceIn(0.4f, 1f)
            val starSize = 2f + (index % 3)

            drawCircle(
                color = starColor.copy(alpha = 0.7f * twinkle),
                radius = starSize,
                center = Offset(x, y)
            )
        }

        // Draw clouds with parallax - simplified data structure
        val cloudData = listOf(
            Triple(0.1f, 0.2f, 80f),
            Triple(0.6f, 0.4f, 100f),
            Triple(0.3f, 0.7f, 70f),
            Triple(0.8f, 0.85f, 90f)
        )

        val cloudColor = Color.White.copy(alpha = 0.3f)

        cloudData.forEachIndexed { index, (xRatio, yRatio, cloudWidth) ->
            val parallaxFactor = 0.15f + (index % 2) * 0.1f
            val yOffset = (cameraY * parallaxFactor) % size.height

            val x = xRatio * size.width
            var y = (yRatio * size.height + yOffset) % size.height
            if (y < 0) y += size.height

            // Draw simple cloud shape
            drawCircle(cloudColor, radius = cloudWidth * 0.4f, center = Offset(x, y))
            drawCircle(cloudColor, radius = cloudWidth * 0.3f, center = Offset(x - cloudWidth * 0.3f, y + 10f))
            drawCircle(cloudColor, radius = cloudWidth * 0.35f, center = Offset(x + cloudWidth * 0.35f, y + 5f))
        }
    }
}
