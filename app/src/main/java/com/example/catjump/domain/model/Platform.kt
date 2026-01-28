package com.example.catjump.domain.model

import com.example.catjump.game.GameConstants

enum class PlatformType {
    NORMAL,
    MOVING,
    FRAGILE,
    SPRING
}

data class Platform(
    val x: Float,
    val y: Float,
    val width: Float = GameConstants.PLATFORM_WIDTH,
    val height: Float = GameConstants.PLATFORM_HEIGHT,
    val type: PlatformType = PlatformType.NORMAL,
    val velocityX: Float = 0f,
    val isActive: Boolean = true,
    val hasBeenUsed: Boolean = false
) {
    val centerX: Float get() = x + width / 2
    val right: Float get() = x + width
    val bottom: Float get() = y + height
}
