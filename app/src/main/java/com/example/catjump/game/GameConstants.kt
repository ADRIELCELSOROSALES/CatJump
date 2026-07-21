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
    const val CAT_SIZE = 120f               // Tamaño del gato (50% más grande que original)

    // Delta-time: física independiente del framerate.
    // Las velocidades/gravedad de abajo están expresadas en "unidades por frame de
    // referencia" (12 ms ≈ el ritmo para el que se ajustó el juego). Cada frame real
    // se integra multiplicando por deltaFrames = tiempoRealTranscurrido / 12ms.
    const val TARGET_FRAME_MS = 12f         // Frame de referencia
    const val MAX_DELTA_FRAMES = 1.8f       // Tope anti-tunneling tras pausas/lag (~45 FPS efectivos)

    // Cuenta regresiva de inicio
    const val COUNTDOWN_SECONDS = 3
    const val COUNTDOWN_STEP_MS = 700L

    // Fatness system
    const val FATNESS_GAIN_PER_BIRD = 0.1f  // Cuánto engorda al comer (10 pájaros = máximo)
    const val MAX_FATNESS = 1f              // Máximo de gordura (10 pájaros)
    const val MIN_FATNESS = 0f

    // Mouse spawn
    const val MOUSE_SIZE = 50f
    const val MOUSE_SPAWN_CHANCE = 0.15f    // 15% de probabilidad de ratón en plataforma

    // Dog spawn
    const val DOG_SIZE = 100f              // Perros más grandes
    const val DOG_SPAWN_CHANCE = 0.18f     // 18% de probabilidad de perro en plataforma
    const val DOG_WALK_SPEED = 1.5f        // Velocidad de caminata del perro

    // Cactus spawn
    const val CACTUS_SIZE = 60f
    const val CACTUS_SPAWN_CHANCE = 0.12f  // 12% de probabilidad de cactus en plataforma

    // Spacing - evitar obstáculos dañinos muy seguidos
    const val MIN_DAMAGING_OBSTACLE_GAP = 3  // Mínimo 3 plataformas entre obstáculos dañinos

    // Lives system
    const val INITIAL_LIVES = 3
    const val INVINCIBILITY_FRAMES = 60f    // Duración de invencibilidad en frames de referencia (~0.72s)

    // Feedback visual (efectos)
    const val SHAKE_MAGNITUDE = 22f         // Intensidad de la sacudida de cámara al recibir daño (px)
    const val SHAKE_DURATION_MS = 350L      // Duración de la sacudida
    const val GAIN_LIFE_FLASH_MS = 500L     // Duración del destello verde al ganar vida
    const val EAT_POPUP_DURATION_MS = 700L  // Duración del texto flotante "+1"

    // Obstacles
    const val OBSTACLE_SIZE = 70f           // Obstáculos más grandes y visibles
    const val SPIDER_SIZE = 60f
    const val CACTUS_WIDTH = 60f
    const val CACTUS_HEIGHT = 60f

    // Power-ups
    const val POWERUP_SIZE = 70f
    const val POWERUP_SPAWN_CHANCE = 0.08f  // 8% de probabilidad de power-up
    const val JETPACK_BOOST = -40f          // Velocidad del jetpack (muy rápido hacia arriba)
    const val JETPACK_DURATION = 2500L      // 2.5 segundos
    const val SUPER_JUMP_VELOCITY = -35f    // Velocidad del super salto
    const val SUPER_JUMP_COUNT = 3          // Cantidad de super saltos que da la croqueta
    const val SUPER_JUMP_DURATION = 8000L   // 8 segundos para usar los super saltos
}
