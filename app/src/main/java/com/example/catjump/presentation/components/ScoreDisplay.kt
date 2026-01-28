package com.example.catjump.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ScoreDisplay(
    score: Int,
    highScore: Int,
    level: Int,
    lives: Int = 3,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Lives (yarn balls) at the top center
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) { index ->
                        if (index > 0) Spacer(modifier = Modifier.width(8.dp))
                        YarnBall(
                            isActive = index < lives,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.padding(top = 8.dp))

        // Score, Level and High Score
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Score and Level
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                ScoreBox(
                    label = "SCORE",
                    value = score.toString()
                )
                ScoreBox(
                    label = "LEVEL",
                    value = level.toString(),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // High Score
            ScoreBox(
                label = "BEST",
                value = highScore.toString()
            )
        }
    }
}

@Composable
private fun YarnBall(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = size.minDimension / 2 - 2f

        if (isActive) {
            // Active yarn ball - colorful
            val yarnColor = Color(0xFFFF6B9D)  // Pink yarn
            val yarnDark = Color(0xFFE04578)
            val yarnLight = Color(0xFFFFB6C8)

            // Main ball
            drawCircle(
                color = yarnColor,
                radius = radius,
                center = Offset(centerX, centerY)
            )

            // Yarn pattern lines
            drawPath(
                path = Path().apply {
                    moveTo(centerX - radius * 0.7f, centerY - radius * 0.3f)
                    quadraticBezierTo(centerX, centerY - radius * 0.8f, centerX + radius * 0.7f, centerY - radius * 0.3f)
                },
                color = yarnDark,
                style = Stroke(width = 3f)
            )
            drawPath(
                path = Path().apply {
                    moveTo(centerX - radius * 0.6f, centerY + radius * 0.2f)
                    quadraticBezierTo(centerX, centerY - radius * 0.3f, centerX + radius * 0.6f, centerY + radius * 0.2f)
                },
                color = yarnDark,
                style = Stroke(width = 3f)
            )
            drawPath(
                path = Path().apply {
                    moveTo(centerX - radius * 0.4f, centerY + radius * 0.6f)
                    quadraticBezierTo(centerX, centerY + radius * 0.2f, centerX + radius * 0.4f, centerY + radius * 0.6f)
                },
                color = yarnDark,
                style = Stroke(width = 2f)
            )

            // Highlight
            drawCircle(
                color = yarnLight,
                radius = radius * 0.25f,
                center = Offset(centerX - radius * 0.3f, centerY - radius * 0.3f)
            )

            // Yarn string hanging
            drawPath(
                path = Path().apply {
                    moveTo(centerX + radius * 0.5f, centerY + radius * 0.8f)
                    quadraticBezierTo(centerX + radius * 1.2f, centerY + radius * 1.3f, centerX + radius * 0.8f, centerY + radius * 1.5f)
                },
                color = yarnColor,
                style = Stroke(width = 2.5f)
            )
        } else {
            // Inactive yarn ball - gray/empty
            val grayColor = Color(0xFF555555)
            val grayDark = Color(0xFF333333)

            // Main ball (empty/used)
            drawCircle(
                color = grayColor,
                radius = radius,
                center = Offset(centerX, centerY)
            )

            // X mark to show it's used
            drawLine(
                color = grayDark,
                start = Offset(centerX - radius * 0.4f, centerY - radius * 0.4f),
                end = Offset(centerX + radius * 0.4f, centerY + radius * 0.4f),
                strokeWidth = 3f
            )
            drawLine(
                color = grayDark,
                start = Offset(centerX + radius * 0.4f, centerY - radius * 0.4f),
                end = Offset(centerX - radius * 0.4f, centerY + radius * 0.4f),
                strokeWidth = 3f
            )
        }
    }
}

@Composable
private fun ScoreBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = Color.Black.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
