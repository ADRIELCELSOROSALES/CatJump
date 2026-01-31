package com.example.catjump.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.sin

// Capas del cielo según altura (score)
enum class SkyLayer {
    SUNSET,         // 0-5000: Atardecer cálido
    CLOUDY_SKY,     // 5000-15000: Cielo nublado
    AIRPLANE_ZONE,  // 15000-30000: Zona de aviones
    STRATOSPHERE,   // 30000-50000: Estratosfera
    SPACE           // 50000+: Espacio
}

fun getSkyLayer(score: Int): SkyLayer = when {
    score < 5000 -> SkyLayer.SUNSET
    score < 15000 -> SkyLayer.CLOUDY_SKY
    score < 30000 -> SkyLayer.AIRPLANE_ZONE
    score < 50000 -> SkyLayer.STRATOSPHERE
    else -> SkyLayer.SPACE
}

// Calcula el progreso de transición entre capas (0.0 a 1.0)
fun getLayerTransition(score: Int): Float {
    val thresholds = listOf(0, 5000, 15000, 30000, 50000)
    for (i in 0 until thresholds.size - 1) {
        if (score < thresholds[i + 1]) {
            val rangeStart = thresholds[i]
            val rangeEnd = thresholds[i + 1]
            return (score - rangeStart).toFloat() / (rangeEnd - rangeStart)
        }
    }
    return 1f
}

@Composable
fun GameBackground(
    cameraY: Float,
    modifier: Modifier = Modifier,
    score: Int = 0
) {
    val skyLayer = getSkyLayer(score)
    val transition = getLayerTransition(score)

    Canvas(modifier = modifier.fillMaxSize()) {
        when (skyLayer) {
            SkyLayer.SUNSET -> drawSunsetBackground(cameraY, transition)
            SkyLayer.CLOUDY_SKY -> drawCloudySkyBackground(cameraY, transition)
            SkyLayer.AIRPLANE_ZONE -> drawAirplaneZoneBackground(cameraY, transition)
            SkyLayer.STRATOSPHERE -> drawStratosphereBackground(cameraY, transition)
            SkyLayer.SPACE -> drawSpaceBackground(cameraY)
        }
    }
}

private fun DrawScope.drawSunsetBackground(cameraY: Float, transition: Float) {
    // Colores de atardecer que transicionan a cielo nublado
    val topColor = lerpColor(Color(0xFFFF6B35), Color(0xFF5C6BC0), transition)
    val middleColor = lerpColor(Color(0xFFFF8C42), Color(0xFF7986CB), transition)
    val bottomColor = lerpColor(Color(0xFFFFD166), Color(0xFF9FA8DA), transition)

    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(topColor, middleColor, bottomColor)
        )
    )

    // Sol
    val sunAlpha = 1f - transition * 0.7f
    val sunY = size.height * 0.7f + (cameraY * 0.02f) % 50f
    drawCircle(
        color = Color(0xFFFFE082).copy(alpha = sunAlpha),
        radius = 80f,
        center = Offset(size.width * 0.8f, sunY)
    )
    // Resplandor del sol
    drawCircle(
        color = Color(0xFFFFD54F).copy(alpha = sunAlpha * 0.3f),
        radius = 120f,
        center = Offset(size.width * 0.8f, sunY)
    )

    // Nubes suaves de atardecer
    drawSunsetClouds(cameraY, 1f - transition * 0.5f)
}

private fun DrawScope.drawSunsetClouds(cameraY: Float, alpha: Float) {
    val cloudColor = Color(0xFFFFE0B2).copy(alpha = alpha * 0.6f)

    val cloudPositions = listOf(
        Triple(0.15f, 0.3f, 100f),
        Triple(0.5f, 0.5f, 120f),
        Triple(0.85f, 0.4f, 90f)
    )

    cloudPositions.forEachIndexed { index, (xRatio, yRatio, cloudWidth) ->
        val parallaxFactor = 0.1f + (index % 2) * 0.05f
        val yOffset = (cameraY * parallaxFactor) % size.height

        val x = xRatio * size.width
        var y = (yRatio * size.height + yOffset) % size.height
        if (y < 0) y += size.height

        drawCircle(cloudColor, radius = cloudWidth * 0.5f, center = Offset(x, y))
        drawCircle(cloudColor, radius = cloudWidth * 0.35f, center = Offset(x - cloudWidth * 0.4f, y + 15f))
        drawCircle(cloudColor, radius = cloudWidth * 0.4f, center = Offset(x + cloudWidth * 0.4f, y + 10f))
    }
}

private fun DrawScope.drawCloudySkyBackground(cameraY: Float, transition: Float) {
    // Cielo nublado que transiciona a zona de aviones
    val topColor = lerpColor(Color(0xFF5C6BC0), Color(0xFF3949AB), transition)
    val middleColor = lerpColor(Color(0xFF7986CB), Color(0xFF5C6BC0), transition)
    val bottomColor = lerpColor(Color(0xFF9FA8DA), Color(0xFF7986CB), transition)

    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(topColor, middleColor, bottomColor)
        )
    )

    // Muchas nubes
    drawDenseClouds(cameraY, 1f - transition * 0.3f)
}

private fun DrawScope.drawDenseClouds(cameraY: Float, alpha: Float) {
    val cloudColor = Color.White.copy(alpha = alpha * 0.5f)
    val darkCloudColor = Color(0xFFB0BEC5).copy(alpha = alpha * 0.4f)

    val cloudPositions = listOf(
        Triple(0.1f, 0.15f, 130f),
        Triple(0.35f, 0.3f, 110f),
        Triple(0.6f, 0.2f, 140f),
        Triple(0.85f, 0.35f, 100f),
        Triple(0.2f, 0.5f, 120f),
        Triple(0.5f, 0.55f, 150f),
        Triple(0.75f, 0.6f, 110f),
        Triple(0.1f, 0.75f, 100f),
        Triple(0.4f, 0.8f, 130f),
        Triple(0.7f, 0.85f, 120f),
        Triple(0.9f, 0.7f, 90f)
    )

    cloudPositions.forEachIndexed { index, (xRatio, yRatio, cloudWidth) ->
        val parallaxFactor = 0.12f + (index % 3) * 0.04f
        val yOffset = (cameraY * parallaxFactor) % size.height

        val x = xRatio * size.width
        var y = (yRatio * size.height + yOffset) % size.height
        if (y < 0) y += size.height

        val color = if (index % 2 == 0) cloudColor else darkCloudColor
        drawCircle(color, radius = cloudWidth * 0.5f, center = Offset(x, y))
        drawCircle(color, radius = cloudWidth * 0.35f, center = Offset(x - cloudWidth * 0.35f, y + 12f))
        drawCircle(color, radius = cloudWidth * 0.4f, center = Offset(x + cloudWidth * 0.38f, y + 8f))
        drawCircle(color, radius = cloudWidth * 0.3f, center = Offset(x, y - cloudWidth * 0.2f))
    }
}

private fun DrawScope.drawAirplaneZoneBackground(cameraY: Float, transition: Float) {
    // Cielo más oscuro con aviones
    val topColor = lerpColor(Color(0xFF3949AB), Color(0xFF1A237E), transition)
    val middleColor = lerpColor(Color(0xFF5C6BC0), Color(0xFF283593), transition)
    val bottomColor = lerpColor(Color(0xFF7986CB), Color(0xFF3949AB), transition)

    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(topColor, middleColor, bottomColor)
        )
    )

    // Nubes dispersas más abajo
    drawSparseClouds(cameraY, 0.6f - transition * 0.4f)

    // Aviones
    drawAirplanes(cameraY, 1f - transition * 0.5f)
}

private fun DrawScope.drawSparseClouds(cameraY: Float, alpha: Float) {
    if (alpha <= 0) return

    val cloudColor = Color.White.copy(alpha = alpha * 0.3f)

    val cloudPositions = listOf(
        Triple(0.2f, 0.6f, 80f),
        Triple(0.6f, 0.75f, 100f),
        Triple(0.85f, 0.85f, 70f)
    )

    cloudPositions.forEachIndexed { index, (xRatio, yRatio, cloudWidth) ->
        val parallaxFactor = 0.15f
        val yOffset = (cameraY * parallaxFactor) % size.height

        val x = xRatio * size.width
        var y = (yRatio * size.height + yOffset) % size.height
        if (y < 0) y += size.height

        drawCircle(cloudColor, radius = cloudWidth * 0.4f, center = Offset(x, y))
        drawCircle(cloudColor, radius = cloudWidth * 0.3f, center = Offset(x - cloudWidth * 0.3f, y + 10f))
        drawCircle(cloudColor, radius = cloudWidth * 0.35f, center = Offset(x + cloudWidth * 0.32f, y + 5f))
    }
}

private fun DrawScope.drawAirplanes(cameraY: Float, alpha: Float) {
    if (alpha <= 0) return

    val planeColor = Color.White.copy(alpha = alpha * 0.7f)
    val trailColor = Color.White.copy(alpha = alpha * 0.3f)

    // Posiciones de aviones
    val planes = listOf(
        Triple(0.2f, 0.25f, 0.8f),  // x, y, escala
        Triple(0.7f, 0.4f, 0.6f),
        Triple(0.4f, 0.6f, 1f),
        Triple(0.85f, 0.75f, 0.5f)
    )

    planes.forEachIndexed { index, (xRatio, yRatio, scale) ->
        val parallaxFactor = 0.08f + (index % 2) * 0.04f
        val yOffset = (cameraY * parallaxFactor) % size.height
        val xOffset = (cameraY * 0.05f * (if (index % 2 == 0) 1 else -1)) % size.width

        var x = (xRatio * size.width + xOffset) % size.width
        if (x < 0) x += size.width
        var y = (yRatio * size.height + yOffset) % size.height
        if (y < 0) y += size.height

        val planeSize = 25f * scale

        // Estela del avión
        drawLine(
            color = trailColor,
            start = Offset(x - planeSize * 3, y),
            end = Offset(x - planeSize * 0.5f, y),
            strokeWidth = 3f * scale
        )

        // Cuerpo del avión
        drawOval(
            color = planeColor,
            topLeft = Offset(x - planeSize, y - planeSize * 0.2f),
            size = Size(planeSize * 2, planeSize * 0.4f)
        )

        // Alas
        drawPath(
            path = Path().apply {
                moveTo(x - planeSize * 0.3f, y)
                lineTo(x, y - planeSize * 0.8f)
                lineTo(x + planeSize * 0.3f, y)
                close()
            },
            color = planeColor
        )
        drawPath(
            path = Path().apply {
                moveTo(x - planeSize * 0.3f, y)
                lineTo(x, y + planeSize * 0.8f)
                lineTo(x + planeSize * 0.3f, y)
                close()
            },
            color = planeColor
        )

        // Cola
        drawPath(
            path = Path().apply {
                moveTo(x - planeSize * 0.8f, y)
                lineTo(x - planeSize, y - planeSize * 0.4f)
                lineTo(x - planeSize * 0.6f, y)
                close()
            },
            color = planeColor
        )
    }
}

private fun DrawScope.drawStratosphereBackground(cameraY: Float, transition: Float) {
    // Estratosfera - muy oscuro, casi negro arriba
    val topColor = lerpColor(Color(0xFF1A237E), Color(0xFF0D1B2A), transition)
    val middleColor = lerpColor(Color(0xFF283593), Color(0xFF1B2838), transition)
    val bottomColor = lerpColor(Color(0xFF3949AB), Color(0xFF1A237E), transition)

    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(topColor, middleColor, bottomColor)
        )
    )

    // Estrellas empezando a aparecer
    drawStars(cameraY, transition * 0.7f)

    // Curvatura de la Tierra visible abajo
    drawEarthCurvature(transition)
}

private fun DrawScope.drawEarthCurvature(alpha: Float) {
    if (alpha < 0.3f) return

    val earthGlow = Color(0xFF64B5F6).copy(alpha = (alpha - 0.3f) * 0.5f)
    val earthEdge = Color(0xFF2196F3).copy(alpha = (alpha - 0.3f) * 0.3f)

    // Resplandor de la atmósfera
    drawOval(
        color = earthGlow,
        topLeft = Offset(-size.width * 0.5f, size.height * 0.85f),
        size = Size(size.width * 2f, size.height * 0.4f)
    )
    drawOval(
        color = earthEdge,
        topLeft = Offset(-size.width * 0.5f, size.height * 0.9f),
        size = Size(size.width * 2f, size.height * 0.3f)
    )
}

private fun DrawScope.drawSpaceBackground(cameraY: Float) {
    // Espacio - negro con estrellas
    val topColor = Color(0xFF000000)
    val middleColor = Color(0xFF0D1B2A)
    val bottomColor = Color(0xFF1B2838)

    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(topColor, middleColor, bottomColor)
        )
    )

    // Muchas estrellas
    drawStars(cameraY, 1f)

    // Nebulosas distantes
    drawNebulas(cameraY)

    // Curvatura de la Tierra completa
    drawEarthCurvature(1f)

    // Tal vez la luna
    drawMoon(cameraY)
}

private fun DrawScope.drawStars(cameraY: Float, alpha: Float) {
    if (alpha <= 0) return

    val starColor = Color.White.copy(alpha = alpha * 0.8f)
    val brightStarColor = Color(0xFFFFFFE0).copy(alpha = alpha)

    val starPositions = listOf(
        Pair(0.05f, 0.05f), Pair(0.15f, 0.12f), Pair(0.25f, 0.03f), Pair(0.35f, 0.18f),
        Pair(0.45f, 0.08f), Pair(0.55f, 0.15f), Pair(0.65f, 0.02f), Pair(0.75f, 0.2f),
        Pair(0.85f, 0.1f), Pair(0.95f, 0.05f),
        Pair(0.08f, 0.28f), Pair(0.18f, 0.35f), Pair(0.28f, 0.22f), Pair(0.38f, 0.4f),
        Pair(0.48f, 0.32f), Pair(0.58f, 0.25f), Pair(0.68f, 0.38f), Pair(0.78f, 0.3f),
        Pair(0.88f, 0.42f), Pair(0.98f, 0.28f),
        Pair(0.03f, 0.48f), Pair(0.13f, 0.55f), Pair(0.23f, 0.5f), Pair(0.33f, 0.6f),
        Pair(0.43f, 0.52f), Pair(0.53f, 0.45f), Pair(0.63f, 0.58f), Pair(0.73f, 0.48f),
        Pair(0.83f, 0.62f), Pair(0.93f, 0.55f),
        Pair(0.1f, 0.68f), Pair(0.2f, 0.72f), Pair(0.3f, 0.65f), Pair(0.4f, 0.78f),
        Pair(0.5f, 0.7f), Pair(0.6f, 0.75f), Pair(0.7f, 0.68f), Pair(0.8f, 0.8f)
    )

    starPositions.forEachIndexed { index, (xRatio, yRatio) ->
        val parallaxFactor = 0.05f + (index % 4) * 0.02f
        val yOffset = (cameraY * parallaxFactor) % size.height

        val x = xRatio * size.width
        var y = (yRatio * size.height + yOffset) % size.height
        if (y < 0) y += size.height

        val twinkle = (sin(cameraY * 0.015f + index * 0.5f) * 0.3f + 0.7f).coerceIn(0.5f, 1f)
        val isBright = index % 5 == 0
        val starSize = if (isBright) 3f else 1.5f + (index % 2)

        drawCircle(
            color = (if (isBright) brightStarColor else starColor).copy(alpha = alpha * twinkle),
            radius = starSize,
            center = Offset(x, y)
        )
    }
}

private fun DrawScope.drawNebulas(cameraY: Float) {
    val nebula1 = Color(0xFF7C4DFF).copy(alpha = 0.1f)
    val nebula2 = Color(0xFFE040FB).copy(alpha = 0.08f)

    val yOffset = (cameraY * 0.02f) % 100f

    // Nebulosa morada
    drawCircle(
        color = nebula1,
        radius = 150f,
        center = Offset(size.width * 0.2f, size.height * 0.3f + yOffset)
    )
    drawCircle(
        color = nebula1.copy(alpha = 0.05f),
        radius = 200f,
        center = Offset(size.width * 0.2f, size.height * 0.3f + yOffset)
    )

    // Nebulosa rosa
    drawCircle(
        color = nebula2,
        radius = 120f,
        center = Offset(size.width * 0.75f, size.height * 0.15f + yOffset * 0.5f)
    )
}

private fun DrawScope.drawMoon(cameraY: Float) {
    val yOffset = (cameraY * 0.01f) % 30f
    val moonColor = Color(0xFFE0E0E0)
    val craterColor = Color(0xFFBDBDBD)

    val moonX = size.width * 0.15f
    val moonY = size.height * 0.12f + yOffset
    val moonRadius = 40f

    // Luna
    drawCircle(
        color = moonColor,
        radius = moonRadius,
        center = Offset(moonX, moonY)
    )

    // Cráteres
    drawCircle(craterColor, radius = 8f, center = Offset(moonX - 12f, moonY - 8f))
    drawCircle(craterColor, radius = 5f, center = Offset(moonX + 10f, moonY + 5f))
    drawCircle(craterColor, radius = 6f, center = Offset(moonX - 5f, moonY + 12f))
    drawCircle(craterColor, radius = 4f, center = Offset(moonX + 15f, moonY - 10f))
}

// Función auxiliar para interpolar colores
private fun lerpColor(start: Color, end: Color, fraction: Float): Color {
    val f = fraction.coerceIn(0f, 1f)
    return Color(
        red = start.red + (end.red - start.red) * f,
        green = start.green + (end.green - start.green) * f,
        blue = start.blue + (end.blue - start.blue) * f,
        alpha = start.alpha + (end.alpha - start.alpha) * f
    )
}
