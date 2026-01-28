package com.example.catjump.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import com.example.catjump.domain.model.Cat
import com.example.catjump.domain.model.CatSkin
import com.example.catjump.domain.model.CatSkins
import com.example.catjump.domain.model.GameState
import com.example.catjump.domain.model.Obstacle
import com.example.catjump.domain.model.ObstacleType
import com.example.catjump.domain.model.Platform
import com.example.catjump.domain.model.PlatformType

@Composable
fun GameCanvas(
    gameState: GameState,
    modifier: Modifier = Modifier,
    catSkin: CatSkin = CatSkins.ORANGE
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val cameraOffset = gameState.cameraY

        // Draw platforms
        gameState.platforms.forEach { platform ->
            if (platform.isActive) {
                drawPlatform(platform, cameraOffset)
            }
        }

        // Draw obstacles
        gameState.obstacles.forEach { obstacle ->
            drawObstacle(obstacle, cameraOffset)
        }

        // Draw cat with skin
        drawCat(gameState.cat, cameraOffset, catSkin)
    }
}

private fun DrawScope.drawPlatform(platform: Platform, cameraOffset: Float) {
    val screenY = platform.y - cameraOffset

    when (platform.type) {
        PlatformType.NORMAL -> drawNormalPlatform(platform.x, screenY, platform.width, platform.height)
        PlatformType.MOVING -> drawMovingPlatform(platform.x, screenY, platform.width, platform.height)
        PlatformType.FRAGILE -> drawFragilePlatform(platform.x, screenY, platform.width, platform.height)
        PlatformType.SPRING -> drawSpringPlatform(platform.x, screenY, platform.width, platform.height)
    }
}

private fun DrawScope.drawNormalPlatform(x: Float, y: Float, width: Float, height: Float) {
    val grassColor = Color(0xFF4CAF50)
    val grassDarkColor = Color(0xFF388E3C)
    val dirtColor = Color(0xFF8D6E63)
    val dirtDarkColor = Color(0xFF6D4C41)

    // Dirt layer
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(dirtColor, dirtDarkColor),
            startY = y + height * 0.3f,
            endY = y + height
        ),
        topLeft = Offset(x, y + height * 0.3f),
        size = Size(width, height * 0.7f),
        cornerRadius = CornerRadius(4f)
    )

    // Grass layer
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(grassColor, grassDarkColor),
            startY = y,
            endY = y + height * 0.5f
        ),
        topLeft = Offset(x, y),
        size = Size(width, height * 0.5f),
        cornerRadius = CornerRadius(4f)
    )
}

private fun DrawScope.drawMovingPlatform(x: Float, y: Float, width: Float, height: Float) {
    val cloudColor = Color(0xFFE3F2FD)
    val cloudShadowColor = Color(0xFFBBDEFB)

    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(cloudColor, cloudShadowColor),
            startY = y,
            endY = y + height
        ),
        topLeft = Offset(x, y),
        size = Size(width, height),
        cornerRadius = CornerRadius(height / 2)
    )

    // Cloud puffs
    drawCircle(cloudColor, radius = height * 0.6f, center = Offset(x + width * 0.2f, y + height * 0.3f))
    drawCircle(cloudColor, radius = height * 0.5f, center = Offset(x + width * 0.5f, y + height * 0.2f))
    drawCircle(cloudColor, radius = height * 0.6f, center = Offset(x + width * 0.8f, y + height * 0.3f))
}

private fun DrawScope.drawFragilePlatform(x: Float, y: Float, width: Float, height: Float) {
    val woodColor = Color(0xFFBCAAA4)
    val woodDarkColor = Color(0xFF8D6E63)
    val crackColor = Color(0xFF5D4037)

    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(woodColor, woodDarkColor),
            startY = y,
            endY = y + height
        ),
        topLeft = Offset(x, y),
        size = Size(width, height),
        cornerRadius = CornerRadius(3f)
    )

    // Cracks
    drawLine(crackColor, Offset(x + width * 0.3f, y), Offset(x + width * 0.25f, y + height), strokeWidth = 1.5f)
    drawLine(crackColor, Offset(x + width * 0.7f, y), Offset(x + width * 0.75f, y + height), strokeWidth = 1.5f)
}

private fun DrawScope.drawSpringPlatform(x: Float, y: Float, width: Float, height: Float) {
    val metalColor = Color(0xFF78909C)
    val metalLightColor = Color(0xFFB0BEC5)
    val springColor = Color(0xFFFF5722)

    // Metal base
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(metalLightColor, metalColor),
            startY = y + height * 0.5f,
            endY = y + height
        ),
        topLeft = Offset(x, y + height * 0.5f),
        size = Size(width, height * 0.5f),
        cornerRadius = CornerRadius(3f)
    )

    // Spring
    val springWidth = width * 0.3f
    val springLeft = x + (width - springWidth) / 2
    drawRoundRect(
        color = springColor,
        topLeft = Offset(springLeft, y),
        size = Size(springWidth, height * 0.6f),
        cornerRadius = CornerRadius(springWidth / 2)
    )

    // Arrow up
    drawPath(
        path = Path().apply {
            moveTo(x + width / 2, y - 8f)
            lineTo(x + width / 2 - 10f, y + 5f)
            lineTo(x + width / 2 + 10f, y + 5f)
            close()
        },
        color = springColor
    )
}

private fun DrawScope.drawObstacle(obstacle: Obstacle, cameraOffset: Float) {
    val screenY = obstacle.y - cameraOffset

    when (obstacle.type) {
        ObstacleType.SPIKE -> drawSpikes(obstacle.x, screenY, obstacle.width, obstacle.height)
        ObstacleType.BIRD -> drawBird(obstacle.x, screenY, obstacle.width, obstacle.height)
        ObstacleType.BAT -> drawBat(obstacle.x, screenY, obstacle.width, obstacle.height)
    }
}

private fun DrawScope.drawSpikes(x: Float, y: Float, width: Float, height: Float) {
    val spikeColor = Color(0xFF455A64)
    val spikeLightColor = Color(0xFF78909C)
    val spikeCount = 3
    val spikeWidth = width / spikeCount

    for (i in 0 until spikeCount) {
        val left = x + i * spikeWidth
        val center = left + spikeWidth / 2

        drawPath(
            path = Path().apply {
                moveTo(left, y + height)
                lineTo(center, y)
                lineTo(left + spikeWidth, y + height)
                close()
            },
            color = spikeColor
        )
        drawPath(
            path = Path().apply {
                moveTo(left + spikeWidth * 0.3f, y + height)
                lineTo(center, y)
                lineTo(center, y + height * 0.3f)
                close()
            },
            color = spikeLightColor
        )
    }
}

private fun DrawScope.drawBird(x: Float, y: Float, width: Float, height: Float) {
    val bodyColor = Color(0xFFE53935)
    val wingColor = Color(0xFFB71C1C)
    val beakColor = Color(0xFFFFB300)

    // Body
    drawOval(bodyColor, Offset(x + width * 0.2f, y + height * 0.3f), Size(width * 0.6f, height * 0.5f))
    // Head
    drawCircle(bodyColor, radius = width * 0.2f, center = Offset(x + width * 0.7f, y + height * 0.35f))
    // Wing
    drawPath(
        path = Path().apply {
            moveTo(x + width * 0.3f, y + height * 0.5f)
            quadraticBezierTo(x + width * 0.4f, y + height * 0.1f, x + width * 0.6f, y + height * 0.4f)
            close()
        },
        color = wingColor
    )
    // Beak
    drawPath(
        path = Path().apply {
            moveTo(x + width * 0.85f, y + height * 0.35f)
            lineTo(x + width, y + height * 0.4f)
            lineTo(x + width * 0.85f, y + height * 0.45f)
            close()
        },
        color = beakColor
    )
    // Eye
    drawCircle(Color.White, radius = width * 0.06f, center = Offset(x + width * 0.75f, y + height * 0.32f))
    drawCircle(Color.Black, radius = width * 0.03f, center = Offset(x + width * 0.76f, y + height * 0.32f))
}

private fun DrawScope.drawBat(x: Float, y: Float, width: Float, height: Float) {
    val bodyColor = Color(0xFF37474F)
    val wingColor = Color(0xFF263238)
    val eyeColor = Color(0xFFFF1744)

    // Body
    drawOval(bodyColor, Offset(x + width * 0.35f, y + height * 0.3f), Size(width * 0.3f, height * 0.5f))
    // Head
    drawCircle(bodyColor, radius = width * 0.15f, center = Offset(x + width * 0.5f, y + height * 0.25f))
    // Wings
    drawPath(
        path = Path().apply {
            moveTo(x + width * 0.35f, y + height * 0.4f)
            quadraticBezierTo(x, y + height * 0.2f, x, y + height * 0.7f)
            quadraticBezierTo(x + width * 0.15f, y + height * 0.9f, x + width * 0.35f, y + height * 0.6f)
            close()
        },
        color = wingColor
    )
    drawPath(
        path = Path().apply {
            moveTo(x + width * 0.65f, y + height * 0.4f)
            quadraticBezierTo(x + width, y + height * 0.2f, x + width, y + height * 0.7f)
            quadraticBezierTo(x + width * 0.85f, y + height * 0.9f, x + width * 0.65f, y + height * 0.6f)
            close()
        },
        color = wingColor
    )
    // Eyes
    drawCircle(eyeColor, radius = width * 0.04f, center = Offset(x + width * 0.45f, y + height * 0.25f))
    drawCircle(eyeColor, radius = width * 0.04f, center = Offset(x + width * 0.55f, y + height * 0.25f))
}

private fun DrawScope.drawCat(cat: Cat, cameraOffset: Float, skin: CatSkin = CatSkins.ORANGE) {
    val screenY = cat.y - cameraOffset
    val s = cat.width / 60f

    drawCatWithSkin(
        skin = skin,
        facingRight = cat.facingRight,
        isJumping = cat.isJumping,
        x = cat.x,
        y = screenY,
        scale = s
    )
}
