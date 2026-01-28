package com.example.catjump.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import com.example.catjump.domain.model.CatSkin
import com.example.catjump.domain.model.CatSkins
import com.example.catjump.domain.model.GameState
import com.example.catjump.presentation.components.GameBackground
import com.example.catjump.presentation.components.GameCanvas
import com.example.catjump.presentation.components.ScoreDisplay

@Composable
fun GameScreen(
    gameState: GameState?,
    onStartGame: (Float, Float) -> Unit,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    onStopMoving: () -> Unit,
    modifier: Modifier = Modifier,
    catSkin: CatSkin = CatSkins.ORANGE
) {
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val screenWidthPx = with(density) { maxWidth.toPx() }
        val screenHeightPx = with(density) { maxHeight.toPx() }

        // Initialize game with screen dimensions when gameState is null
        LaunchedEffect(screenWidthPx, screenHeightPx) {
            if (gameState == null && screenWidthPx > 0 && screenHeightPx > 0) {
                onStartGame(screenWidthPx, screenHeightPx)
            }
        }

        if (gameState == null) {
            // Loading state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1a237e)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Loading...",
                    color = Color.White
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = { offset ->
                                // Determine if tap is on left or right side
                                if (offset.x < screenWidthPx / 2) {
                                    onMoveLeft()
                                } else {
                                    onMoveRight()
                                }

                                // Wait for release
                                tryAwaitRelease()
                                onStopMoving()
                            }
                        )
                    }
            ) {
                // Background with parallax
                GameBackground(cameraY = gameState.cameraY)

                // Game elements (platforms, obstacles, cat)
                GameCanvas(gameState = gameState, catSkin = catSkin)

                // HUD
                ScoreDisplay(
                    score = gameState.score,
                    highScore = gameState.highScore,
                    level = gameState.level,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}
