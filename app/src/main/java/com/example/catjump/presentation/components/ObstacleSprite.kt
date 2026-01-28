package com.example.catjump.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.example.catjump.domain.model.ObstacleType

@Composable
fun ObstacleSprite(
    type: ObstacleType,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(40.dp)) {
        when (type) {
            ObstacleType.SPIKE -> drawSpike(size)
            ObstacleType.BIRD -> drawBird(size)
            ObstacleType.BAT -> drawBat(size)
        }
    }
}

private fun DrawScope.drawSpike(size: Size) {
    val spikeColor = Color(0xFF455A64)
    val spikeLightColor = Color(0xFF78909C)

    val spikeCount = 3
    val spikeWidth = size.width / spikeCount

    for (i in 0 until spikeCount) {
        val left = i * spikeWidth
        val center = left + spikeWidth / 2

        // Spike triangle
        drawPath(
            path = Path().apply {
                moveTo(left, size.height)
                lineTo(center, 0f)
                lineTo(left + spikeWidth, size.height)
                close()
            },
            color = spikeColor
        )

        // Highlight
        drawPath(
            path = Path().apply {
                moveTo(left + spikeWidth * 0.3f, size.height)
                lineTo(center, 0f)
                lineTo(center, size.height * 0.3f)
                close()
            },
            color = spikeLightColor
        )
    }
}

private fun DrawScope.drawBird(size: Size) {
    val bodyColor = Color(0xFFE53935)
    val wingColor = Color(0xFFB71C1C)
    val beakColor = Color(0xFFFFB300)
    val eyeColor = Color.White

    // Body
    drawOval(
        color = bodyColor,
        topLeft = Offset(size.width * 0.2f, size.height * 0.3f),
        size = Size(size.width * 0.6f, size.height * 0.5f)
    )

    // Head
    drawCircle(
        color = bodyColor,
        radius = size.width * 0.2f,
        center = Offset(size.width * 0.7f, size.height * 0.35f)
    )

    // Wing
    drawPath(
        path = Path().apply {
            moveTo(size.width * 0.3f, size.height * 0.5f)
            quadraticBezierTo(
                size.width * 0.4f, size.height * 0.1f,
                size.width * 0.6f, size.height * 0.4f
            )
            lineTo(size.width * 0.3f, size.height * 0.5f)
        },
        color = wingColor
    )

    // Beak
    drawPath(
        path = Path().apply {
            moveTo(size.width * 0.85f, size.height * 0.35f)
            lineTo(size.width, size.height * 0.4f)
            lineTo(size.width * 0.85f, size.height * 0.45f)
            close()
        },
        color = beakColor
    )

    // Eye
    drawCircle(
        color = eyeColor,
        radius = size.width * 0.06f,
        center = Offset(size.width * 0.75f, size.height * 0.32f)
    )
    drawCircle(
        color = Color.Black,
        radius = size.width * 0.03f,
        center = Offset(size.width * 0.76f, size.height * 0.32f)
    )

    // Tail
    drawPath(
        path = Path().apply {
            moveTo(size.width * 0.2f, size.height * 0.5f)
            lineTo(0f, size.height * 0.3f)
            lineTo(0f, size.height * 0.6f)
            close()
        },
        color = wingColor
    )
}

private fun DrawScope.drawBat(size: Size) {
    val bodyColor = Color(0xFF37474F)
    val wingColor = Color(0xFF263238)
    val eyeColor = Color(0xFFFF1744)

    // Body
    drawOval(
        color = bodyColor,
        topLeft = Offset(size.width * 0.35f, size.height * 0.3f),
        size = Size(size.width * 0.3f, size.height * 0.5f)
    )

    // Head
    drawCircle(
        color = bodyColor,
        radius = size.width * 0.15f,
        center = Offset(size.width * 0.5f, size.height * 0.25f)
    )

    // Ears
    drawPath(
        path = Path().apply {
            moveTo(size.width * 0.4f, size.height * 0.2f)
            lineTo(size.width * 0.35f, 0f)
            lineTo(size.width * 0.45f, size.height * 0.15f)
            close()
        },
        color = bodyColor
    )
    drawPath(
        path = Path().apply {
            moveTo(size.width * 0.6f, size.height * 0.2f)
            lineTo(size.width * 0.65f, 0f)
            lineTo(size.width * 0.55f, size.height * 0.15f)
            close()
        },
        color = bodyColor
    )

    // Left wing
    drawPath(
        path = Path().apply {
            moveTo(size.width * 0.35f, size.height * 0.4f)
            quadraticBezierTo(0f, size.height * 0.2f, 0f, size.height * 0.7f)
            quadraticBezierTo(size.width * 0.15f, size.height * 0.9f, size.width * 0.35f, size.height * 0.6f)
            close()
        },
        color = wingColor
    )

    // Right wing
    drawPath(
        path = Path().apply {
            moveTo(size.width * 0.65f, size.height * 0.4f)
            quadraticBezierTo(size.width, size.height * 0.2f, size.width, size.height * 0.7f)
            quadraticBezierTo(size.width * 0.85f, size.height * 0.9f, size.width * 0.65f, size.height * 0.6f)
            close()
        },
        color = wingColor
    )

    // Eyes (glowing red)
    drawCircle(
        color = eyeColor,
        radius = size.width * 0.04f,
        center = Offset(size.width * 0.45f, size.height * 0.25f)
    )
    drawCircle(
        color = eyeColor,
        radius = size.width * 0.04f,
        center = Offset(size.width * 0.55f, size.height * 0.25f)
    )
}
