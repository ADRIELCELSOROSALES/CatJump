package com.example.catjump.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.dp
import com.example.catjump.domain.model.BodyScale
import com.example.catjump.domain.model.CatSkin
import com.example.catjump.domain.model.CatSkins
import com.example.catjump.domain.model.PatternType

@Composable
fun CatSprite(
    facingRight: Boolean,
    isJumping: Boolean,
    modifier: Modifier = Modifier,
    skin: CatSkin = CatSkins.ORANGE
) {
    Canvas(modifier = modifier.size(60.dp)) {
        drawCatWithSkin(
            skin = skin,
            facingRight = facingRight,
            isJumping = isJumping,
            x = 0f,
            y = 0f,
            scale = 1f,
            canvasSize = size
        )
    }
}

fun DrawScope.drawCatWithSkin(
    skin: CatSkin,
    facingRight: Boolean,
    isJumping: Boolean,
    x: Float,
    y: Float,
    scale: Float,
    canvasSize: androidx.compose.ui.geometry.Size = size
) {
    val catColor = skin.primaryColor
    val darkCatColor = skin.darkColor
    val pinkColor = skin.earInnerColor
    val eyeColor = Color(0xFF212121)
    val noseColor = skin.noseColor

    val scaleX = if (facingRight) 1f else -1f

    // Body scale adjustments
    val bodyWidthMult = when (skin.bodyScale) {
        BodyScale.SLIM -> 0.8f
        BodyScale.NORMAL -> 1f
        BodyScale.CHUBBY -> 1.3f
        BodyScale.FLUFFY -> 1.4f
    }
    val bodyHeightMult = when (skin.bodyScale) {
        BodyScale.SLIM -> 0.85f
        BodyScale.NORMAL -> 1f
        BodyScale.CHUBBY -> 1.2f
        BodyScale.FLUFFY -> 1.15f
    }

    val pivotX = if (x == 0f) canvasSize.width / 2 else x + 30f * scale
    val pivotY = if (y == 0f) canvasSize.height / 2 else y + 30f * scale

    scale(scaleX, 1f, pivot = Offset(pivotX, pivotY)) {
        val baseX = x
        val baseY = y
        val s = scale

        // Body
        val bodyWidth = 40f * s * bodyWidthMult
        val bodyHeight = 30f * s * bodyHeightMult
        val bodyX = baseX + 10f * s + (40f * s - bodyWidth) / 2
        val bodyY = baseY + 25f * s

        drawOval(
            color = catColor,
            topLeft = Offset(bodyX, bodyY),
            size = Size(bodyWidth, bodyHeight)
        )

        // Belly for bicolor/chubby cats
        if (skin.hasBelly) {
            val bellyWidth = bodyWidth * 0.6f
            val bellyHeight = bodyHeight * 0.7f
            drawOval(
                color = skin.bellyColor,
                topLeft = Offset(bodyX + (bodyWidth - bellyWidth) / 2, bodyY + bodyHeight * 0.2f),
                size = Size(bellyWidth, bellyHeight)
            )
        }

        // Stripes for tabby cats
        if (skin.hasStripes && skin.patternType == PatternType.TABBY) {
            val stripeWidth = 2f * s
            for (i in 0..2) {
                val stripeX = bodyX + bodyWidth * (0.25f + i * 0.25f)
                drawLine(
                    color = skin.stripeColor,
                    start = Offset(stripeX, bodyY + 2f * s),
                    end = Offset(stripeX, bodyY + bodyHeight - 2f * s),
                    strokeWidth = stripeWidth
                )
            }
        }

        // Spots for calico/spotted cats
        if (skin.hasSpots) {
            drawCircle(
                color = skin.spotColor,
                radius = 6f * s,
                center = Offset(bodyX + bodyWidth * 0.3f, bodyY + bodyHeight * 0.4f)
            )
            drawCircle(
                color = skin.darkColor,
                radius = 5f * s,
                center = Offset(bodyX + bodyWidth * 0.7f, bodyY + bodyHeight * 0.5f)
            )
        }

        // Head
        val headRadius = 20f * s * (if (skin.bodyScale == BodyScale.CHUBBY) 1.1f else 1f)
        drawCircle(
            color = catColor,
            radius = headRadius,
            center = Offset(baseX + 30f * s, baseY + 20f * s)
        )

        // Siamese face mask
        if (skin.patternType == PatternType.SIAMESE) {
            drawCircle(
                color = skin.secondaryColor,
                radius = headRadius * 0.6f,
                center = Offset(baseX + 31f * s, baseY + 24f * s)
            )
        }

        // Stripes on head for tabby
        if (skin.hasStripes && skin.patternType == PatternType.TABBY) {
            // M pattern on forehead
            drawLine(
                color = skin.stripeColor,
                start = Offset(baseX + 25f * s, baseY + 10f * s),
                end = Offset(baseX + 30f * s, baseY + 15f * s),
                strokeWidth = 2f * s
            )
            drawLine(
                color = skin.stripeColor,
                start = Offset(baseX + 30f * s, baseY + 15f * s),
                end = Offset(baseX + 35f * s, baseY + 10f * s),
                strokeWidth = 2f * s
            )
        }

        // Fluffy fur details
        if (skin.bodyScale == BodyScale.FLUFFY) {
            // Cheek fluff
            drawCircle(
                color = catColor,
                radius = 8f * s,
                center = Offset(baseX + 18f * s, baseY + 25f * s)
            )
            drawCircle(
                color = catColor,
                radius = 8f * s,
                center = Offset(baseX + 42f * s, baseY + 25f * s)
            )
            // Chest fluff
            drawCircle(
                color = skin.secondaryColor,
                radius = 10f * s,
                center = Offset(baseX + 30f * s, baseY + 35f * s)
            )
        }

        // Left ear
        drawPath(
            path = Path().apply {
                moveTo(baseX + 15f * s, baseY + 8f * s)
                lineTo(baseX + 20f * s, baseY)
                lineTo(baseX + 25f * s, baseY + 12f * s)
                close()
            },
            color = catColor
        )
        // Inner left ear
        drawPath(
            path = Path().apply {
                moveTo(baseX + 17f * s, baseY + 8f * s)
                lineTo(baseX + 20f * s, baseY + 3f * s)
                lineTo(baseX + 23f * s, baseY + 10f * s)
                close()
            },
            color = pinkColor
        )

        // Right ear
        drawPath(
            path = Path().apply {
                moveTo(baseX + 35f * s, baseY + 8f * s)
                lineTo(baseX + 40f * s, baseY)
                lineTo(baseX + 45f * s, baseY + 12f * s)
                close()
            },
            color = catColor
        )
        // Inner right ear
        drawPath(
            path = Path().apply {
                moveTo(baseX + 37f * s, baseY + 8f * s)
                lineTo(baseX + 40f * s, baseY + 3f * s)
                lineTo(baseX + 43f * s, baseY + 10f * s)
                close()
            },
            color = pinkColor
        )

        // Eyes
        val eyeRadius = 6f * s
        drawCircle(
            color = Color.White,
            radius = eyeRadius,
            center = Offset(baseX + 24f * s, baseY + 18f * s)
        )
        drawCircle(
            color = Color.White,
            radius = eyeRadius,
            center = Offset(baseX + 38f * s, baseY + 18f * s)
        )

        // Pupils (change based on jump)
        val pupilOffset = if (isJumping) -1f * s else 1f * s
        drawCircle(
            color = eyeColor,
            radius = 3f * s,
            center = Offset(baseX + 25f * s, baseY + 18f * s + pupilOffset)
        )
        drawCircle(
            color = eyeColor,
            radius = 3f * s,
            center = Offset(baseX + 39f * s, baseY + 18f * s + pupilOffset)
        )

        // Eye shine
        drawCircle(
            color = Color.White,
            radius = 1.5f * s,
            center = Offset(baseX + 23f * s, baseY + 16f * s)
        )
        drawCircle(
            color = Color.White,
            radius = 1.5f * s,
            center = Offset(baseX + 37f * s, baseY + 16f * s)
        )

        // Nose
        drawPath(
            path = Path().apply {
                moveTo(baseX + 31f * s, baseY + 24f * s)
                lineTo(baseX + 29f * s, baseY + 27f * s)
                lineTo(baseX + 33f * s, baseY + 27f * s)
                close()
            },
            color = noseColor
        )

        // Mouth
        drawLine(
            color = darkCatColor,
            start = Offset(baseX + 31f * s, baseY + 27f * s),
            end = Offset(baseX + 31f * s, baseY + 30f * s),
            strokeWidth = 1.5f * s
        )
        drawLine(
            color = darkCatColor,
            start = Offset(baseX + 31f * s, baseY + 30f * s),
            end = Offset(baseX + 27f * s, baseY + 32f * s),
            strokeWidth = 1.5f * s
        )
        drawLine(
            color = darkCatColor,
            start = Offset(baseX + 31f * s, baseY + 30f * s),
            end = Offset(baseX + 35f * s, baseY + 32f * s),
            strokeWidth = 1.5f * s
        )

        // Whiskers
        drawWhiskers(this, baseX + 24f * s, baseY + 28f * s, true, s)
        drawWhiskers(this, baseX + 38f * s, baseY + 28f * s, false, s)

        // Tail
        val tailThickness = when (skin.bodyScale) {
            BodyScale.FLUFFY -> 12f * s
            BodyScale.CHUBBY -> 10f * s
            else -> 8f * s
        }
        val tailPath = if (isJumping) {
            Path().apply {
                moveTo(baseX + 10f * s, baseY + 40f * s)
                quadraticBezierTo(baseX - 5f * s, baseY + 25f * s, baseX + 5f * s, baseY + 15f * s)
            }
        } else {
            Path().apply {
                moveTo(baseX + 10f * s, baseY + 40f * s)
                quadraticBezierTo(baseX, baseY + 45f * s, baseX - 5f * s, baseY + 35f * s)
            }
        }
        drawPath(
            path = tailPath,
            color = catColor,
            style = Stroke(width = tailThickness)
        )

        // Tail stripes for tabby
        if (skin.hasStripes && skin.patternType == PatternType.TABBY) {
            val stripeTailPath = if (isJumping) {
                Path().apply {
                    moveTo(baseX + 8f * s, baseY + 35f * s)
                    quadraticBezierTo(baseX - 3f * s, baseY + 28f * s, baseX + 3f * s, baseY + 22f * s)
                }
            } else {
                Path().apply {
                    moveTo(baseX + 8f * s, baseY + 38f * s)
                    quadraticBezierTo(baseX + 2f * s, baseY + 42f * s, baseX - 2f * s, baseY + 36f * s)
                }
            }
            drawPath(
                path = stripeTailPath,
                color = skin.stripeColor,
                style = Stroke(width = tailThickness * 0.3f)
            )
        }

        // Siamese tail tip
        if (skin.patternType == PatternType.SIAMESE) {
            val tailTipPath = if (isJumping) {
                Path().apply {
                    moveTo(baseX + 2f * s, baseY + 20f * s)
                    quadraticBezierTo(baseX - 2f * s, baseY + 17f * s, baseX + 5f * s, baseY + 15f * s)
                }
            } else {
                Path().apply {
                    moveTo(baseX - 2f * s, baseY + 38f * s)
                    quadraticBezierTo(baseX - 4f * s, baseY + 37f * s, baseX - 5f * s, baseY + 35f * s)
                }
            }
            drawPath(
                path = tailTipPath,
                color = skin.secondaryColor,
                style = Stroke(width = tailThickness)
            )
        }

        // Legs
        val legWidth = 8f * s * (if (skin.bodyScale == BodyScale.CHUBBY) 1.2f else if (skin.bodyScale == BodyScale.SLIM) 0.8f else 1f)
        val legY = if (isJumping) baseY + 50f * s else baseY + 55f * s
        val legHeight = legY - (baseY + 40f * s)

        // Front legs
        drawRoundRect(
            color = catColor,
            topLeft = Offset(baseX + 18f * s, baseY + 45f * s),
            size = Size(legWidth, legHeight),
            cornerRadius = CornerRadius(4f * s)
        )
        drawRoundRect(
            color = catColor,
            topLeft = Offset(baseX + 34f * s, baseY + 45f * s),
            size = Size(legWidth, legHeight),
            cornerRadius = CornerRadius(4f * s)
        )

        // Siamese leg points
        if (skin.patternType == PatternType.SIAMESE) {
            drawRoundRect(
                color = skin.secondaryColor,
                topLeft = Offset(baseX + 18f * s, legY - 8f * s),
                size = Size(legWidth, 8f * s),
                cornerRadius = CornerRadius(4f * s)
            )
            drawRoundRect(
                color = skin.secondaryColor,
                topLeft = Offset(baseX + 34f * s, legY - 8f * s),
                size = Size(legWidth, 8f * s),
                cornerRadius = CornerRadius(4f * s)
            )
        }

        // Paws
        val pawRadius = 5f * s * (if (skin.bodyScale == BodyScale.CHUBBY) 1.2f else 1f)
        drawCircle(catColor, radius = pawRadius, center = Offset(baseX + 22f * s, legY))
        drawCircle(catColor, radius = pawRadius, center = Offset(baseX + 38f * s, legY))

        // Siamese paw points
        if (skin.patternType == PatternType.SIAMESE) {
            drawCircle(skin.secondaryColor, radius = pawRadius, center = Offset(baseX + 22f * s, legY))
            drawCircle(skin.secondaryColor, radius = pawRadius, center = Offset(baseX + 38f * s, legY))
        }
    }
}

private fun drawWhiskers(drawScope: DrawScope, x: Float, y: Float, leftSide: Boolean, scale: Float = 1f) {
    val direction = if (leftSide) -1f else 1f
    val whiskerColor = Color(0xFF5D4037)

    with(drawScope) {
        drawLine(
            color = whiskerColor,
            start = Offset(x, y - 2f * scale),
            end = Offset(x + direction * 15f * scale, y - 5f * scale),
            strokeWidth = 1f * scale
        )
        drawLine(
            color = whiskerColor,
            start = Offset(x, y),
            end = Offset(x + direction * 16f * scale, y),
            strokeWidth = 1f * scale
        )
        drawLine(
            color = whiskerColor,
            start = Offset(x, y + 2f * scale),
            end = Offset(x + direction * 15f * scale, y + 5f * scale),
            strokeWidth = 1f * scale
        )
    }
}
