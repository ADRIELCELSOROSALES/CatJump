package com.example.catjump.domain.model

import com.example.catjump.game.GameConstants

enum class ObstacleType {
    CACTUS,  // En plataformas, hace daño
    BIRD,    // Volador, comestible
    BAT,     // Volador, comestible
    MOUSE,   // En plataformas, comestible
    DOG      // En plataformas, hace daño
}

data class Obstacle(
    val x: Float,
    val y: Float,
    val width: Float = GameConstants.OBSTACLE_SIZE,
    val height: Float = GameConstants.OBSTACLE_SIZE,
    val type: ObstacleType = ObstacleType.BIRD,
    val velocityX: Float = 0f,
    val velocityY: Float = 0f
) {
    val centerX: Float get() = x + width / 2
    val centerY: Float get() = y + height / 2
    val right: Float get() = x + width
    val bottom: Float get() = y + height
}
