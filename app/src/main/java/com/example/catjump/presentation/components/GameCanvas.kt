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
import androidx.compose.ui.graphics.drawscope.Stroke
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
        ObstacleType.MOUSE -> drawMouse(obstacle.x, screenY, obstacle.width, obstacle.height)
        ObstacleType.DOG -> drawDog(obstacle.x, screenY, obstacle.width, obstacle.height)
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

private fun DrawScope.drawMouse(x: Float, y: Float, width: Float, height: Float) {
    val bodyColor = Color(0xFF9E9E9E)      // Gray body
    val darkColor = Color(0xFF616161)       // Dark gray
    val earColor = Color(0xFFFFCDD2)        // Pink ears
    val noseColor = Color(0xFFFF8A80)       // Pink nose
    val tailColor = Color(0xFFE0A0A0)       // Pink tail

    // Body (oval)
    drawOval(
        color = bodyColor,
        topLeft = Offset(x + width * 0.2f, y + height * 0.35f),
        size = Size(width * 0.55f, height * 0.45f)
    )

    // Head
    drawCircle(
        color = bodyColor,
        radius = width * 0.2f,
        center = Offset(x + width * 0.7f, y + height * 0.45f)
    )

    // Left ear
    drawCircle(
        color = bodyColor,
        radius = width * 0.12f,
        center = Offset(x + width * 0.62f, y + height * 0.28f)
    )
    drawCircle(
        color = earColor,
        radius = width * 0.07f,
        center = Offset(x + width * 0.62f, y + height * 0.28f)
    )

    // Right ear
    drawCircle(
        color = bodyColor,
        radius = width * 0.12f,
        center = Offset(x + width * 0.78f, y + height * 0.28f)
    )
    drawCircle(
        color = earColor,
        radius = width * 0.07f,
        center = Offset(x + width * 0.78f, y + height * 0.28f)
    )

    // Eyes
    drawCircle(
        color = Color.Black,
        radius = width * 0.04f,
        center = Offset(x + width * 0.65f, y + height * 0.42f)
    )
    drawCircle(
        color = Color.Black,
        radius = width * 0.04f,
        center = Offset(x + width * 0.75f, y + height * 0.42f)
    )

    // Eye shine
    drawCircle(
        color = Color.White,
        radius = width * 0.015f,
        center = Offset(x + width * 0.64f, y + height * 0.40f)
    )
    drawCircle(
        color = Color.White,
        radius = width * 0.015f,
        center = Offset(x + width * 0.74f, y + height * 0.40f)
    )

    // Nose
    drawCircle(
        color = noseColor,
        radius = width * 0.04f,
        center = Offset(x + width * 0.85f, y + height * 0.48f)
    )

    // Whiskers
    val whiskerColor = Color(0xFF424242)
    drawLine(whiskerColor, Offset(x + width * 0.85f, y + height * 0.46f), Offset(x + width * 0.98f, y + height * 0.40f), strokeWidth = 1f)
    drawLine(whiskerColor, Offset(x + width * 0.85f, y + height * 0.48f), Offset(x + width * 0.99f, y + height * 0.48f), strokeWidth = 1f)
    drawLine(whiskerColor, Offset(x + width * 0.85f, y + height * 0.50f), Offset(x + width * 0.98f, y + height * 0.56f), strokeWidth = 1f)

    // Tail (curvy)
    drawPath(
        path = Path().apply {
            moveTo(x + width * 0.2f, y + height * 0.55f)
            quadraticBezierTo(x, y + height * 0.3f, x + width * 0.1f, y + height * 0.2f)
        },
        color = tailColor,
        style = Stroke(width = width * 0.06f)
    )

    // Legs
    drawRoundRect(
        color = darkColor,
        topLeft = Offset(x + width * 0.28f, y + height * 0.7f),
        size = Size(width * 0.1f, height * 0.2f),
        cornerRadius = CornerRadius(width * 0.05f)
    )
    drawRoundRect(
        color = darkColor,
        topLeft = Offset(x + width * 0.55f, y + height * 0.7f),
        size = Size(width * 0.1f, height * 0.2f),
        cornerRadius = CornerRadius(width * 0.05f)
    )
}

private fun DrawScope.drawDog(x: Float, y: Float, width: Float, height: Float) {
    val bodyColor = Color(0xFF8B4513)       // Brown body
    val darkColor = Color(0xFF5D3A1A)       // Dark brown
    val lightColor = Color(0xFFD2A679)      // Light tan
    val noseColor = Color(0xFF2D2D2D)       // Black nose
    val tongueColor = Color(0xFFFF6B8A)     // Pink tongue

    // Body (oval)
    drawOval(
        color = bodyColor,
        topLeft = Offset(x + width * 0.1f, y + height * 0.35f),
        size = Size(width * 0.6f, height * 0.4f)
    )

    // Head
    drawCircle(
        color = bodyColor,
        radius = width * 0.22f,
        center = Offset(x + width * 0.75f, y + height * 0.4f)
    )

    // Snout
    drawOval(
        color = lightColor,
        topLeft = Offset(x + width * 0.8f, y + height * 0.38f),
        size = Size(width * 0.18f, height * 0.18f)
    )

    // Left ear (floppy)
    drawPath(
        path = Path().apply {
            moveTo(x + width * 0.6f, y + height * 0.3f)
            quadraticBezierTo(x + width * 0.5f, y + height * 0.15f, x + width * 0.55f, y + height * 0.45f)
            lineTo(x + width * 0.65f, y + height * 0.35f)
            close()
        },
        color = darkColor
    )

    // Right ear (floppy)
    drawPath(
        path = Path().apply {
            moveTo(x + width * 0.85f, y + height * 0.22f)
            quadraticBezierTo(x + width * 0.95f, y + height * 0.1f, x + width * 0.98f, y + height * 0.35f)
            lineTo(x + width * 0.88f, y + height * 0.28f)
            close()
        },
        color = darkColor
    )

    // Eyes
    drawCircle(
        color = Color.White,
        radius = width * 0.06f,
        center = Offset(x + width * 0.68f, y + height * 0.35f)
    )
    drawCircle(
        color = Color.White,
        radius = width * 0.06f,
        center = Offset(x + width * 0.82f, y + height * 0.35f)
    )
    // Pupils (angry looking)
    drawCircle(
        color = Color.Black,
        radius = width * 0.035f,
        center = Offset(x + width * 0.69f, y + height * 0.36f)
    )
    drawCircle(
        color = Color.Black,
        radius = width * 0.035f,
        center = Offset(x + width * 0.83f, y + height * 0.36f)
    )

    // Eyebrows (angry)
    drawLine(
        color = darkColor,
        start = Offset(x + width * 0.62f, y + height * 0.28f),
        end = Offset(x + width * 0.72f, y + height * 0.25f),
        strokeWidth = width * 0.03f
    )
    drawLine(
        color = darkColor,
        start = Offset(x + width * 0.88f, y + height * 0.25f),
        end = Offset(x + width * 0.78f, y + height * 0.28f),
        strokeWidth = width * 0.03f
    )

    // Nose
    drawCircle(
        color = noseColor,
        radius = width * 0.045f,
        center = Offset(x + width * 0.92f, y + height * 0.45f)
    )

    // Tongue (sticking out, menacing)
    drawOval(
        color = tongueColor,
        topLeft = Offset(x + width * 0.85f, y + height * 0.52f),
        size = Size(width * 0.08f, height * 0.12f)
    )

    // Teeth
    drawPath(
        path = Path().apply {
            moveTo(x + width * 0.82f, y + height * 0.5f)
            lineTo(x + width * 0.84f, y + height * 0.56f)
            lineTo(x + width * 0.86f, y + height * 0.5f)
        },
        color = Color.White
    )
    drawPath(
        path = Path().apply {
            moveTo(x + width * 0.9f, y + height * 0.5f)
            lineTo(x + width * 0.92f, y + height * 0.56f)
            lineTo(x + width * 0.94f, y + height * 0.5f)
        },
        color = Color.White
    )

    // Tail (wagging up)
    drawPath(
        path = Path().apply {
            moveTo(x + width * 0.1f, y + height * 0.45f)
            quadraticBezierTo(x - width * 0.05f, y + height * 0.2f, x + width * 0.05f, y + height * 0.15f)
        },
        color = bodyColor,
        style = Stroke(width = width * 0.08f)
    )

    // Legs
    drawRoundRect(
        color = darkColor,
        topLeft = Offset(x + width * 0.18f, y + height * 0.65f),
        size = Size(width * 0.12f, height * 0.25f),
        cornerRadius = CornerRadius(width * 0.06f)
    )
    drawRoundRect(
        color = darkColor,
        topLeft = Offset(x + width * 0.5f, y + height * 0.65f),
        size = Size(width * 0.12f, height * 0.25f),
        cornerRadius = CornerRadius(width * 0.06f)
    )

    // Collar
    drawRoundRect(
        color = Color(0xFFE53935),
        topLeft = Offset(x + width * 0.58f, y + height * 0.48f),
        size = Size(width * 0.2f, height * 0.06f),
        cornerRadius = CornerRadius(width * 0.02f)
    )
}

private fun DrawScope.drawCat(cat: Cat, cameraOffset: Float, skin: CatSkin = CatSkins.ORANGE) {
    // Blink effect when invincible (skip drawing every other few frames)
    if (cat.isInvincible && (cat.invincibilityFrames / 5) % 2 == 0) {
        return // Don't draw this frame (blinking effect)
    }

    val screenY = cat.y - cameraOffset
    val s = cat.width / 60f

    drawCatWithSkin(
        skin = skin,
        facingRight = cat.facingRight,
        isJumping = cat.isJumping,
        x = cat.x,
        y = screenY,
        scale = s,
        fatness = cat.fatness
    )
}
