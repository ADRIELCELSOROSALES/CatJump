package com.example.catjump.domain.model

data class Cat(
    val x: Float,
    val y: Float,
    val velocityX: Float = 0f,
    val velocityY: Float = 0f,
    val width: Float = 80f,
    val height: Float = 80f,
    val isJumping: Boolean = false,
    val facingRight: Boolean = true
) {
    val centerX: Float get() = x + width / 2
    val centerY: Float get() = y + height / 2
    val bottom: Float get() = y + height
    val right: Float get() = x + width
}
