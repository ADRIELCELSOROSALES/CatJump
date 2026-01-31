package com.example.catjump.domain.model

import com.example.catjump.game.GameConstants

data class GameState(
    val cat: Cat,
    val platforms: List<Platform>,
    val obstacles: List<Obstacle> = emptyList(),
    val powerUps: List<PowerUp> = emptyList(),
    val score: Int = 0,
    val highScore: Int = 0,
    val level: Int = 1,
    val isGameOver: Boolean = false,
    val isNewHighScore: Boolean = false,
    val cameraY: Float = 0f,
    val screenWidth: Float = 0f,
    val screenHeight: Float = 0f,
    val currentTime: Long = System.currentTimeMillis(),
    val soundEvents: List<SoundEvent> = emptyList(),
    val activeDogCount: Int = 0
) {
    companion object {
        fun initial(screenWidth: Float, screenHeight: Float): GameState {
            // La plataforma inicial está en screenHeight - 200f
            val initialPlatformY = screenHeight - 200f

            // Posicionar el gato encima de la plataforma
            val catStartX = screenWidth / 2 - GameConstants.CAT_SIZE / 2
            val catStartY = initialPlatformY - GameConstants.CAT_SIZE

            return GameState(
                cat = Cat(
                    x = catStartX,
                    y = catStartY,
                    width = GameConstants.CAT_SIZE,
                    height = GameConstants.CAT_SIZE,
                    velocityY = GameConstants.JUMP_VELOCITY, // Salto inicial
                    fatness = 0f, // Empieza delgado
                    lives = GameConstants.INITIAL_LIVES
                ),
                platforms = emptyList(),
                screenWidth = screenWidth,
                screenHeight = screenHeight
            )
        }
    }
}
