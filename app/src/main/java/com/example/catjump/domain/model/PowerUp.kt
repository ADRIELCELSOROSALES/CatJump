package com.example.catjump.domain.model

import com.example.catjump.game.GameConstants

enum class PowerUpType {
    JETPACK,    // Propulsor - sube rápido
    KIBBLE      // Croqueta - super saltos
}

data class PowerUp(
    val x: Float,
    val y: Float,
    val width: Float = GameConstants.POWERUP_SIZE,
    val height: Float = GameConstants.POWERUP_SIZE,
    val type: PowerUpType
) {
    val centerX: Float get() = x + width / 2
    val centerY: Float get() = y + height / 2
    val right: Float get() = x + width
    val bottom: Float get() = y + height
}
