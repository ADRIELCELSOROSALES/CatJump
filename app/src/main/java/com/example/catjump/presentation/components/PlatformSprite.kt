package com.example.catjump.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.example.catjump.domain.model.PlatformType

@Composable
fun PlatformSprite(
    type: PlatformType,
    width: Float,
    height: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(width.dp, height.dp)) {
        when (type) {
            PlatformType.NORMAL -> drawNormalPlatform(size)
            PlatformType.MOVING -> drawMovingPlatform(size)
            PlatformType.FRAGILE -> drawFragilePlatform(size)
            PlatformType.SPRING -> drawSpringPlatform(size)
        }
    }
}

private fun DrawScope.drawNormalPlatform(size: Size) {
    val grassColor = Color(0xFF4CAF50)
    val grassDarkColor = Color(0xFF388E3C)
    val dirtColor = Color(0xFF8D6E63)
    val dirtDarkColor = Color(0xFF6D4C41)

    // Dirt layer
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(dirtColor, dirtDarkColor)
        ),
        topLeft = Offset(0f, size.height * 0.3f),
        size = Size(size.width, size.height * 0.7f),
        cornerRadius = CornerRadius(4f)
    )

    // Grass layer
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(grassColor, grassDarkColor)
        ),
        topLeft = Offset.Zero,
        size = Size(size.width, size.height * 0.5f),
        cornerRadius = CornerRadius(4f)
    )

    // Grass blades
    val bladeCount = (size.width / 8).toInt()
    for (i in 0 until bladeCount) {
        val x = (i * 8f) + 4f
        drawLine(
            color = Color(0xFF66BB6A),
            start = Offset(x, 0f),
            end = Offset(x + (if (i % 2 == 0) 2f else -2f), -4f),
            strokeWidth = 2f
        )
    }
}

private fun DrawScope.drawMovingPlatform(size: Size) {
    val cloudColor = Color(0xFFE3F2FD)
    val cloudShadowColor = Color(0xFFBBDEFB)

    // Cloud platform
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(cloudColor, cloudShadowColor)
        ),
        topLeft = Offset.Zero,
        size = size,
        cornerRadius = CornerRadius(size.height / 2)
    )

    // Cloud puffs
    drawCircle(
        color = cloudColor,
        radius = size.height * 0.6f,
        center = Offset(size.width * 0.2f, size.height * 0.3f)
    )
    drawCircle(
        color = cloudColor,
        radius = size.height * 0.5f,
        center = Offset(size.width * 0.5f, size.height * 0.2f)
    )
    drawCircle(
        color = cloudColor,
        radius = size.height * 0.6f,
        center = Offset(size.width * 0.8f, size.height * 0.3f)
    )

    // Arrows indicating movement
    val arrowColor = Color(0xFF90CAF9)
    drawPath(
        path = Path().apply {
            moveTo(size.width * 0.15f, size.height * 0.7f)
            lineTo(size.width * 0.05f, size.height * 0.5f)
            lineTo(size.width * 0.15f, size.height * 0.3f)
        },
        color = arrowColor,
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
    )
    drawPath(
        path = Path().apply {
            moveTo(size.width * 0.85f, size.height * 0.7f)
            lineTo(size.width * 0.95f, size.height * 0.5f)
            lineTo(size.width * 0.85f, size.height * 0.3f)
        },
        color = arrowColor,
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
    )
}

private fun DrawScope.drawFragilePlatform(size: Size) {
    val woodColor = Color(0xFFBCAAA4)
    val woodDarkColor = Color(0xFF8D6E63)
    val crackColor = Color(0xFF5D4037)

    // Wooden platform with cracks
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(woodColor, woodDarkColor)
        ),
        topLeft = Offset.Zero,
        size = size,
        cornerRadius = CornerRadius(3f)
    )

    // Wood grain lines
    for (i in 0..3) {
        val y = size.height * (0.2f + i * 0.2f)
        drawLine(
            color = woodDarkColor.copy(alpha = 0.5f),
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 1f
        )
    }

    // Cracks
    drawPath(
        path = Path().apply {
            moveTo(size.width * 0.3f, 0f)
            lineTo(size.width * 0.35f, size.height * 0.4f)
            lineTo(size.width * 0.25f, size.height)
        },
        color = crackColor,
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f)
    )
    drawPath(
        path = Path().apply {
            moveTo(size.width * 0.7f, 0f)
            lineTo(size.width * 0.65f, size.height * 0.5f)
            lineTo(size.width * 0.75f, size.height)
        },
        color = crackColor,
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f)
    )
}

private fun DrawScope.drawSpringPlatform(size: Size) {
    val metalColor = Color(0xFF78909C)
    val metalLightColor = Color(0xFFB0BEC5)
    val springColor = Color(0xFFFF5722)
    val springLightColor = Color(0xFFFF8A65)

    // Metal base
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(metalLightColor, metalColor)
        ),
        topLeft = Offset(0f, size.height * 0.5f),
        size = Size(size.width, size.height * 0.5f),
        cornerRadius = CornerRadius(3f)
    )

    // Spring coil
    val springWidth = size.width * 0.3f
    val springLeft = (size.width - springWidth) / 2

    drawRoundRect(
        brush = Brush.horizontalGradient(
            colors = listOf(springColor, springLightColor, springColor)
        ),
        topLeft = Offset(springLeft, 0f),
        size = Size(springWidth, size.height * 0.6f),
        cornerRadius = CornerRadius(springWidth / 2)
    )

    // Spring lines
    for (i in 0..3) {
        val y = size.height * (0.1f + i * 0.12f)
        drawLine(
            color = Color(0xFFBF360C),
            start = Offset(springLeft + 2f, y),
            end = Offset(springLeft + springWidth - 2f, y),
            strokeWidth = 2f
        )
    }

    // Arrow up indicator
    drawPath(
        path = Path().apply {
            moveTo(size.width / 2, -5f)
            lineTo(size.width / 2 - 8f, 8f)
            lineTo(size.width / 2 + 8f, 8f)
            close()
        },
        color = springColor
    )
}
