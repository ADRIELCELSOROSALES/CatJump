package com.example.catjump.game

object GameConstants {
    // Physics
    const val GRAVITY = 0.5f
    const val JUMP_VELOCITY = -22f
    const val SPRING_JUMP_VELOCITY = -30f
    const val HORIZONTAL_SPEED = 12f
    const val MAX_FALL_VELOCITY = 18f

    // Platform generation
    const val MIN_PLATFORM_GAP = 100f
    const val MAX_PLATFORM_GAP = 160f
    const val PLATFORM_WIDTH = 220f
    const val PLATFORM_HEIGHT = 22f

    // Difficulty
    const val POINTS_PER_LEVEL = 1000
    const val MOVING_PLATFORM_SPEED = 2.5f
    const val OBSTACLE_SPEED = 4f

    // Game
    const val FRAME_TIME_MS = 12L
    const val CAT_SIZE = 160f               // Gato el doble de grande

    // Obstacles
    const val OBSTACLE_SIZE = 70f           // Obstáculos más grandes y visibles
    const val SPIDER_SIZE = 60f
    const val SPIKE_WIDTH = 80f
    const val SPIKE_HEIGHT = 40f

    // Power-ups
    const val POWERUP_SIZE = 60f
    const val JETPACK_BOOST = -35f          // Velocidad del jetpack
    const val JETPACK_DURATION = 2000L      // 2 segundos
    const val CLOUD_BOOST = -8f             // Velocidad de la nube (lenta)
    const val CLOUD_DURATION = 4000L        // 4 segundos
    const val SHIELD_DURATION = 5000L       // 5 segundos de escudo
}
