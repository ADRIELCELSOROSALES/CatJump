package com.example.catjump.domain.model

data class Cat(
    val x: Float,
    val y: Float,
    val velocityX: Float = 0f,
    val velocityY: Float = 0f,
    val width: Float = 120f,  // 50% más grande
    val height: Float = 120f, // 50% más grande
    val isJumping: Boolean = false,
    val facingRight: Boolean = true,
    val fatness: Float = 0f,  // 0.0 = normal, 1.0 = muy gordo (máx 10 comidas)
    val birdsEaten: Int = 0,  // Contador de pájaros/ratones comidos
    val lives: Int = 3,       // Vidas (ovillos de lana)
    val invincibilityFrames: Int = 0  // Frames de invencibilidad después de daño
) {
    val centerX: Float get() = x + width / 2
    val centerY: Float get() = y + height / 2
    val bottom: Float get() = y + height
    val right: Float get() = x + width
    val isInvincible: Boolean get() = invincibilityFrames > 0
}
