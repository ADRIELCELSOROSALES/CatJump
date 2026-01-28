package com.example.catjump.presentation.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.catjump.domain.model.CatSkin
import com.example.catjump.domain.model.CatSkins
import com.example.catjump.presentation.components.CatSprite
import com.example.catjump.presentation.components.GameBackground
import com.example.catjump.presentation.components.GameButton

@Composable
fun MenuScreen(
    highScore: Int,
    onPlayClick: () -> Unit,
    onSkinsClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedSkin: CatSkin = CatSkins.ORANGE
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cat_bounce")
    val catBounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bounce"
    )

    // Animation for the skins button cat (dancing effect)
    val skinsCatBounce by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(300),
            repeatMode = RepeatMode.Reverse
        ),
        label = "skins_bounce"
    )

    Box(modifier = modifier.fillMaxSize()) {
        // Animated background
        GameBackground(cameraY = 0f)

        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title
            Text(
                text = "CAT",
                color = Color(0xFFFF9800),
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .shadow(8.dp, RoundedCornerShape(8.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1a237e).copy(alpha = 0.8f),
                                Color(0xFF3949ab).copy(alpha = 0.8f)
                            )
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            )
            Text(
                text = "JUMP",
                color = Color(0xFF4CAF50),
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .shadow(8.dp, RoundedCornerShape(8.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1a237e).copy(alpha = 0.8f),
                                Color(0xFF3949ab).copy(alpha = 0.8f)
                            )
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Animated cat with selected skin
            Box(
                modifier = Modifier
                    .offset(y = -catBounce.dp)
                    .size(120.dp),
                contentAlignment = Alignment.Center
            ) {
                CatSprite(
                    facingRight = true,
                    isJumping = catBounce > 7f,
                    modifier = Modifier.size(120.dp),
                    skin = selectedSkin
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // High Score display
            if (highScore > 0) {
                Box(
                    modifier = Modifier
                        .background(
                            color = Color.Black.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "BEST SCORE",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = highScore.toString(),
                            color = Color(0xFFFFD700),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            // Play button
            GameButton(
                text = "PLAY",
                onClick = onPlayClick
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Instructions
            Box(
                modifier = Modifier
                    .background(
                        color = Color.Black.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "HOW TO PLAY",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap left or right side to move",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Jump on platforms to go higher",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Avoid obstacles and don't fall!",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Skins button - bottom left corner with dancing cat
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF7B1FA2).copy(alpha = 0.9f),
                            Color(0xFF4A148C).copy(alpha = 0.9f)
                        )
                    )
                )
                .clickable(onClick = onSkinsClick)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Dancing cat
                Box(
                    modifier = Modifier
                        .offset(x = skinsCatBounce.dp, y = (-skinsCatBounce / 2).dp)
                        .size(50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CatSprite(
                        facingRight = skinsCatBounce > 0,
                        isJumping = true,
                        modifier = Modifier.size(50.dp),
                        skin = selectedSkin
                    )
                }

                Text(
                    text = "SKINS",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
