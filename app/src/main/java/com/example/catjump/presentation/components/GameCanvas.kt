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
import com.example.catjump.domain.model.PowerUp
import com.example.catjump.domain.model.PowerUpType

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

        // Draw power-ups
        gameState.powerUps.forEach { powerUp ->
            drawPowerUp(powerUp, cameraOffset)
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
    val facingRight = obstacle.velocityX >= 0  // Dirección del movimiento

    when (obstacle.type) {
        ObstacleType.CACTUS -> drawCactus(obstacle.x, screenY, obstacle.width, obstacle.height)
        ObstacleType.BIRD -> drawBird(obstacle.x, screenY, obstacle.width, obstacle.height)
        ObstacleType.BAT -> drawBat(obstacle.x, screenY, obstacle.width, obstacle.height)
        ObstacleType.MOUSE -> drawMouse(obstacle.x, screenY, obstacle.width, obstacle.height)
        ObstacleType.DOG -> drawDog(obstacle.x, screenY, obstacle.width, obstacle.height, facingRight)
    }
}

private fun DrawScope.drawCactus(x: Float, y: Float, width: Float, height: Float) {
    val cactusColor = Color(0xFF2E7D32)
    val cactusLightColor = Color(0xFF4CAF50)
    val spineColor = Color(0xFFFFEB3B)

    // Tronco principal
    drawRoundRect(
        color = cactusColor,
        topLeft = Offset(x + width * 0.35f, y + height * 0.1f),
        size = Size(width * 0.3f, height * 0.9f),
        cornerRadius = CornerRadius(width * 0.1f)
    )

    // Highlight del tronco
    drawRoundRect(
        color = cactusLightColor,
        topLeft = Offset(x + width * 0.4f, y + height * 0.15f),
        size = Size(width * 0.08f, height * 0.8f),
        cornerRadius = CornerRadius(width * 0.04f)
    )

    // Brazo izquierdo horizontal
    drawRoundRect(
        color = cactusColor,
        topLeft = Offset(x + width * 0.08f, y + height * 0.3f),
        size = Size(width * 0.27f, height * 0.15f),
        cornerRadius = CornerRadius(width * 0.08f)
    )
    // Brazo izquierdo vertical
    drawRoundRect(
        color = cactusColor,
        topLeft = Offset(x + width * 0.08f, y + height * 0.15f),
        size = Size(width * 0.12f, height * 0.25f),
        cornerRadius = CornerRadius(width * 0.06f)
    )

    // Brazo derecho horizontal
    drawRoundRect(
        color = cactusColor,
        topLeft = Offset(x + width * 0.65f, y + height * 0.4f),
        size = Size(width * 0.27f, height * 0.15f),
        cornerRadius = CornerRadius(width * 0.08f)
    )
    // Brazo derecho vertical
    drawRoundRect(
        color = cactusColor,
        topLeft = Offset(x + width * 0.8f, y + height * 0.25f),
        size = Size(width * 0.12f, height * 0.25f),
        cornerRadius = CornerRadius(width * 0.06f)
    )

    // Espinas
    val spinePositions = listOf(
        Offset(x + width * 0.5f, y + height * 0.15f),
        Offset(x + width * 0.45f, y + height * 0.35f),
        Offset(x + width * 0.55f, y + height * 0.55f),
        Offset(x + width * 0.48f, y + height * 0.75f),
        Offset(x + width * 0.14f, y + height * 0.2f),
        Offset(x + width * 0.86f, y + height * 0.32f)
    )
    spinePositions.forEach { pos ->
        drawCircle(
            color = spineColor,
            radius = width * 0.025f,
            center = pos
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

private fun DrawScope.drawDog(x: Float, y: Float, width: Float, height: Float, facingRight: Boolean = true) {
    val bodyColor = Color(0xFF8B4513)       // Brown body
    val darkColor = Color(0xFF5D3A1A)       // Dark brown
    val lightColor = Color(0xFFD2A679)      // Light tan
    val noseColor = Color(0xFF2D2D2D)       // Black nose
    val tongueColor = Color(0xFFFF6B8A)     // Pink tongue

    // Helper function to flip X coordinate when facing left
    fun flipX(xPos: Float): Float = if (facingRight) xPos else x + width - (xPos - x)
    fun flipXOffset(offset: Float): Float = if (facingRight) offset else width - offset

    // Body (oval)
    val bodyLeft = if (facingRight) x + width * 0.1f else x + width * 0.3f
    drawOval(
        color = bodyColor,
        topLeft = Offset(bodyLeft, y + height * 0.35f),
        size = Size(width * 0.6f, height * 0.4f)
    )

    // Head
    val headX = if (facingRight) x + width * 0.75f else x + width * 0.25f
    drawCircle(
        color = bodyColor,
        radius = width * 0.22f,
        center = Offset(headX, y + height * 0.4f)
    )

    // Snout
    val snoutLeft = if (facingRight) x + width * 0.8f else x + width * 0.02f
    drawOval(
        color = lightColor,
        topLeft = Offset(snoutLeft, y + height * 0.38f),
        size = Size(width * 0.18f, height * 0.18f)
    )

    // Left ear (floppy) - becomes right when flipped
    val ear1X1 = if (facingRight) x + width * 0.6f else x + width * 0.4f
    val ear1X2 = if (facingRight) x + width * 0.5f else x + width * 0.5f
    val ear1X3 = if (facingRight) x + width * 0.55f else x + width * 0.45f
    val ear1X4 = if (facingRight) x + width * 0.65f else x + width * 0.35f
    drawPath(
        path = Path().apply {
            moveTo(ear1X1, y + height * 0.3f)
            quadraticBezierTo(ear1X2, y + height * 0.15f, ear1X3, y + height * 0.45f)
            lineTo(ear1X4, y + height * 0.35f)
            close()
        },
        color = darkColor
    )

    // Right ear (floppy) - becomes left when flipped
    val ear2X1 = if (facingRight) x + width * 0.85f else x + width * 0.15f
    val ear2X2 = if (facingRight) x + width * 0.95f else x + width * 0.05f
    val ear2X3 = if (facingRight) x + width * 0.98f else x + width * 0.02f
    val ear2X4 = if (facingRight) x + width * 0.88f else x + width * 0.12f
    drawPath(
        path = Path().apply {
            moveTo(ear2X1, y + height * 0.22f)
            quadraticBezierTo(ear2X2, y + height * 0.1f, ear2X3, y + height * 0.35f)
            lineTo(ear2X4, y + height * 0.28f)
            close()
        },
        color = darkColor
    )

    // Eyes
    val eye1X = if (facingRight) x + width * 0.68f else x + width * 0.32f
    val eye2X = if (facingRight) x + width * 0.82f else x + width * 0.18f
    drawCircle(
        color = Color.White,
        radius = width * 0.06f,
        center = Offset(eye1X, y + height * 0.35f)
    )
    drawCircle(
        color = Color.White,
        radius = width * 0.06f,
        center = Offset(eye2X, y + height * 0.35f)
    )
    // Pupils (angry looking)
    val pupil1X = if (facingRight) x + width * 0.69f else x + width * 0.31f
    val pupil2X = if (facingRight) x + width * 0.83f else x + width * 0.17f
    drawCircle(
        color = Color.Black,
        radius = width * 0.035f,
        center = Offset(pupil1X, y + height * 0.36f)
    )
    drawCircle(
        color = Color.Black,
        radius = width * 0.035f,
        center = Offset(pupil2X, y + height * 0.36f)
    )

    // Eyebrows (angry)
    val brow1Start = if (facingRight) x + width * 0.62f else x + width * 0.38f
    val brow1End = if (facingRight) x + width * 0.72f else x + width * 0.28f
    val brow2Start = if (facingRight) x + width * 0.88f else x + width * 0.12f
    val brow2End = if (facingRight) x + width * 0.78f else x + width * 0.22f
    drawLine(
        color = darkColor,
        start = Offset(brow1Start, y + height * 0.28f),
        end = Offset(brow1End, y + height * 0.25f),
        strokeWidth = width * 0.03f
    )
    drawLine(
        color = darkColor,
        start = Offset(brow2Start, y + height * 0.25f),
        end = Offset(brow2End, y + height * 0.28f),
        strokeWidth = width * 0.03f
    )

    // Nose
    val noseX = if (facingRight) x + width * 0.92f else x + width * 0.08f
    drawCircle(
        color = noseColor,
        radius = width * 0.045f,
        center = Offset(noseX, y + height * 0.45f)
    )

    // Tongue (sticking out, menacing)
    val tongueLeft = if (facingRight) x + width * 0.85f else x + width * 0.07f
    drawOval(
        color = tongueColor,
        topLeft = Offset(tongueLeft, y + height * 0.52f),
        size = Size(width * 0.08f, height * 0.12f)
    )

    // Teeth
    val tooth1X1 = if (facingRight) x + width * 0.82f else x + width * 0.18f
    val tooth1X2 = if (facingRight) x + width * 0.84f else x + width * 0.16f
    val tooth1X3 = if (facingRight) x + width * 0.86f else x + width * 0.14f
    drawPath(
        path = Path().apply {
            moveTo(tooth1X1, y + height * 0.5f)
            lineTo(tooth1X2, y + height * 0.56f)
            lineTo(tooth1X3, y + height * 0.5f)
        },
        color = Color.White
    )
    val tooth2X1 = if (facingRight) x + width * 0.9f else x + width * 0.10f
    val tooth2X2 = if (facingRight) x + width * 0.92f else x + width * 0.08f
    val tooth2X3 = if (facingRight) x + width * 0.94f else x + width * 0.06f
    drawPath(
        path = Path().apply {
            moveTo(tooth2X1, y + height * 0.5f)
            lineTo(tooth2X2, y + height * 0.56f)
            lineTo(tooth2X3, y + height * 0.5f)
        },
        color = Color.White
    )

    // Tail (wagging up)
    val tailX1 = if (facingRight) x + width * 0.1f else x + width * 0.9f
    val tailX2 = if (facingRight) x - width * 0.05f else x + width * 1.05f
    val tailX3 = if (facingRight) x + width * 0.05f else x + width * 0.95f
    drawPath(
        path = Path().apply {
            moveTo(tailX1, y + height * 0.45f)
            quadraticBezierTo(tailX2, y + height * 0.2f, tailX3, y + height * 0.15f)
        },
        color = bodyColor,
        style = Stroke(width = width * 0.08f)
    )

    // Legs
    val leg1Left = if (facingRight) x + width * 0.18f else x + width * 0.7f
    val leg2Left = if (facingRight) x + width * 0.5f else x + width * 0.38f
    drawRoundRect(
        color = darkColor,
        topLeft = Offset(leg1Left, y + height * 0.65f),
        size = Size(width * 0.12f, height * 0.25f),
        cornerRadius = CornerRadius(width * 0.06f)
    )
    drawRoundRect(
        color = darkColor,
        topLeft = Offset(leg2Left, y + height * 0.65f),
        size = Size(width * 0.12f, height * 0.25f),
        cornerRadius = CornerRadius(width * 0.06f)
    )

    // Collar
    val collarLeft = if (facingRight) x + width * 0.58f else x + width * 0.22f
    drawRoundRect(
        color = Color(0xFFE53935),
        topLeft = Offset(collarLeft, y + height * 0.48f),
        size = Size(width * 0.2f, height * 0.06f),
        cornerRadius = CornerRadius(width * 0.02f)
    )
}

private fun DrawScope.drawPowerUp(powerUp: PowerUp, cameraOffset: Float) {
    val screenY = powerUp.y - cameraOffset
    val x = powerUp.x
    val width = powerUp.width
    val height = powerUp.height

    // Burbuja exterior
    val bubbleColor = Color(0x80ADD8E6)  // Celeste transparente
    val bubbleShine = Color(0xAAFFFFFF)

    drawCircle(
        color = bubbleColor,
        radius = width / 2,
        center = Offset(x + width / 2, screenY + height / 2)
    )

    // Brillo de la burbuja
    drawCircle(
        color = bubbleShine,
        radius = width * 0.15f,
        center = Offset(x + width * 0.35f, screenY + height * 0.35f)
    )

    // Contenido según tipo
    when (powerUp.type) {
        PowerUpType.JETPACK -> drawJetpackIcon(x, screenY, width, height)
        PowerUpType.KIBBLE -> drawKibbleIcon(x, screenY, width, height)
    }
}

private fun DrawScope.drawJetpackIcon(x: Float, y: Float, width: Float, height: Float) {
    val jetColor = Color(0xFF1565C0)  // Azul
    val flameColor = Color(0xFFFF5722)  // Naranja
    val flameYellow = Color(0xFFFFEB3B)

    // Cuerpo del jetpack
    drawRoundRect(
        color = jetColor,
        topLeft = Offset(x + width * 0.3f, y + height * 0.25f),
        size = Size(width * 0.4f, height * 0.4f),
        cornerRadius = CornerRadius(width * 0.08f)
    )

    // Tanques laterales
    drawRoundRect(
        color = Color(0xFF0D47A1),
        topLeft = Offset(x + width * 0.22f, y + height * 0.3f),
        size = Size(width * 0.12f, height * 0.3f),
        cornerRadius = CornerRadius(width * 0.04f)
    )
    drawRoundRect(
        color = Color(0xFF0D47A1),
        topLeft = Offset(x + width * 0.66f, y + height * 0.3f),
        size = Size(width * 0.12f, height * 0.3f),
        cornerRadius = CornerRadius(width * 0.04f)
    )

    // Llamas
    drawPath(
        path = Path().apply {
            moveTo(x + width * 0.28f, y + height * 0.6f)
            lineTo(x + width * 0.22f, y + height * 0.78f)
            lineTo(x + width * 0.34f, y + height * 0.6f)
        },
        color = flameColor
    )
    drawPath(
        path = Path().apply {
            moveTo(x + width * 0.66f, y + height * 0.6f)
            lineTo(x + width * 0.72f, y + height * 0.78f)
            lineTo(x + width * 0.78f, y + height * 0.6f)
        },
        color = flameColor
    )

    // Centro de llamas
    drawPath(
        path = Path().apply {
            moveTo(x + width * 0.26f, y + height * 0.58f)
            lineTo(x + width * 0.28f, y + height * 0.72f)
            lineTo(x + width * 0.30f, y + height * 0.58f)
        },
        color = flameYellow
    )
    drawPath(
        path = Path().apply {
            moveTo(x + width * 0.70f, y + height * 0.58f)
            lineTo(x + width * 0.72f, y + height * 0.72f)
            lineTo(x + width * 0.74f, y + height * 0.58f)
        },
        color = flameYellow
    )
}

private fun DrawScope.drawKibbleIcon(x: Float, y: Float, width: Float, height: Float) {
    val kibbleColor = Color(0xFF8B4513)  // Marrón
    val kibbleLight = Color(0xFFD2691E)
    val kibbleDark = Color(0xFF5D3A1A)

    // Croqueta principal (forma ovalada irregular)
    drawOval(
        color = kibbleColor,
        topLeft = Offset(x + width * 0.25f, y + height * 0.3f),
        size = Size(width * 0.5f, height * 0.35f)
    )

    // Highlight
    drawOval(
        color = kibbleLight,
        topLeft = Offset(x + width * 0.3f, y + height * 0.33f),
        size = Size(width * 0.2f, height * 0.12f)
    )

    // Segunda croqueta más pequeña
    drawOval(
        color = kibbleDark,
        topLeft = Offset(x + width * 0.35f, y + height * 0.55f),
        size = Size(width * 0.35f, height * 0.25f)
    )

    // Pequeña croqueta
    drawOval(
        color = kibbleColor,
        topLeft = Offset(x + width * 0.55f, y + height * 0.42f),
        size = Size(width * 0.2f, height * 0.18f)
    )

    // Estrellas de poder alrededor
    val starColor = Color(0xFFFFD700)
    drawCircle(color = starColor, radius = width * 0.04f, center = Offset(x + width * 0.2f, y + height * 0.35f))
    drawCircle(color = starColor, radius = width * 0.03f, center = Offset(x + width * 0.8f, y + height * 0.4f))
    drawCircle(color = starColor, radius = width * 0.035f, center = Offset(x + width * 0.75f, y + height * 0.7f))
}

private fun DrawScope.drawCat(cat: Cat, cameraOffset: Float, skin: CatSkin = CatSkins.ORANGE) {
    // Blink effect when invincible from damage (skip drawing every other few frames)
    // Only blink if invincible from damage, not from power-ups
    if (cat.invincibilityFrames > 0 && !cat.hasPowerUp && (cat.invincibilityFrames / 5) % 2 == 0) {
        return // Don't draw this frame (blinking effect)
    }

    val screenY = cat.y - cameraOffset
    val s = cat.width / 60f

    // Draw power-up aura/effects behind cat
    if (cat.jetpackActive) {
        // Aura azul y llamas
        drawCircle(
            color = Color(0x401565C0),
            radius = cat.width * 0.8f,
            center = Offset(cat.x + cat.width / 2, screenY + cat.height / 2)
        )
        // Llamas del jetpack
        drawPath(
            path = Path().apply {
                moveTo(cat.x + cat.width * 0.3f, screenY + cat.height)
                lineTo(cat.x + cat.width * 0.2f, screenY + cat.height + 30f)
                lineTo(cat.x + cat.width * 0.4f, screenY + cat.height)
            },
            color = Color(0xFFFF5722)
        )
        drawPath(
            path = Path().apply {
                moveTo(cat.x + cat.width * 0.6f, screenY + cat.height)
                lineTo(cat.x + cat.width * 0.7f, screenY + cat.height + 30f)
                lineTo(cat.x + cat.width * 0.8f, screenY + cat.height)
            },
            color = Color(0xFFFF5722)
        )
    }

    if (cat.superJumpActive) {
        // Aura dorada
        drawCircle(
            color = Color(0x40FFD700),
            radius = cat.width * 0.8f,
            center = Offset(cat.x + cat.width / 2, screenY + cat.height / 2)
        )
        // Estrellitas
        val starPositions = listOf(
            Offset(cat.x - 10f, screenY + cat.height * 0.3f),
            Offset(cat.x + cat.width + 10f, screenY + cat.height * 0.5f),
            Offset(cat.x + cat.width * 0.5f, screenY - 15f)
        )
        starPositions.forEach { pos ->
            drawCircle(color = Color(0xFFFFD700), radius = 5f, center = pos)
        }
    }

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
