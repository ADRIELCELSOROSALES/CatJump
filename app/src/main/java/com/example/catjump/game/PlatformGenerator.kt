package com.example.catjump.game

import com.example.catjump.domain.model.Obstacle
import com.example.catjump.domain.model.ObstacleType
import com.example.catjump.domain.model.Platform
import com.example.catjump.domain.model.PlatformType
import kotlin.math.abs
import kotlin.random.Random

class PlatformGenerator(
    private val difficultyManager: DifficultyManager
) {
    fun generateInitialPlatforms(screenWidth: Float, screenHeight: Float): List<Platform> {
        val platforms = mutableListOf<Platform>()

        // Plataforma inicial donde aparece el gato (centrada)
        val initialPlatformY = screenHeight - 200f
        val initialPlatformX = screenWidth / 2 - GameConstants.PLATFORM_WIDTH / 2
        platforms.add(
            Platform(
                x = initialPlatformX,
                y = initialPlatformY,
                type = PlatformType.NORMAL
            )
        )

        // Generar plataformas hacia arriba asegurando que sean alcanzables
        var currentY = initialPlatformY
        var lastPlatformX = initialPlatformX

        while (currentY > -screenHeight) {
            val gap = Random.nextFloat() *
                (GameConstants.MAX_PLATFORM_GAP - GameConstants.MIN_PLATFORM_GAP) +
                GameConstants.MIN_PLATFORM_GAP
            currentY -= gap

            // Generar X asegurando que sea alcanzable desde la plataforma anterior
            val newX = generateReachableX(lastPlatformX, screenWidth, gap)

            platforms.add(
                Platform(
                    x = newX,
                    y = currentY,
                    type = PlatformType.NORMAL
                )
            )

            lastPlatformX = newX
        }

        return platforms
    }

    private fun generateReachableX(lastX: Float, screenWidth: Float, verticalGap: Float): Float {
        // Calcular la distancia horizontal máxima que el gato puede recorrer
        // durante el salto basado en la física del juego
        val jumpTime = (2 * abs(GameConstants.JUMP_VELOCITY) / GameConstants.GRAVITY)
        val maxHorizontalDistance = GameConstants.HORIZONTAL_SPEED * jumpTime * 0.6f

        // Limitar el rango de X para que sea alcanzable
        val minX = (lastX - maxHorizontalDistance).coerceAtLeast(0f)
        val maxX = (lastX + maxHorizontalDistance).coerceAtMost(screenWidth - GameConstants.PLATFORM_WIDTH)

        return if (maxX > minX) {
            Random.nextFloat() * (maxX - minX) + minX
        } else {
            (screenWidth - GameConstants.PLATFORM_WIDTH) / 2
        }
    }

    fun generateNewPlatform(
        screenWidth: Float,
        highestPlatformY: Float,
        level: Int,
        lastPlatformX: Float = screenWidth / 2
    ): Platform {
        val minGap = difficultyManager.getMinPlatformGap(level)
        val maxGap = difficultyManager.getMaxPlatformGap(level)

        val gap = Random.nextFloat() * (maxGap - minGap) + minGap
        val newY = highestPlatformY - gap

        // Generar X alcanzable
        val x = generateReachableX(lastPlatformX, screenWidth, gap)
        val type = difficultyManager.selectPlatformType(level)

        val velocityX = if (type == PlatformType.MOVING) {
            val speed = difficultyManager.getMovingPlatformSpeed(level)
            if (Random.nextBoolean()) speed else -speed
        } else 0f

        return Platform(
            x = x,
            y = newY,
            type = type,
            velocityX = velocityX
        )
    }

    fun generateObstacle(
        screenWidth: Float,
        y: Float,
        level: Int
    ): Obstacle? {
        if (!difficultyManager.shouldSpawnObstacle(level)) return null

        val type = when {
            level < 4 -> ObstacleType.SPIKE
            level < 6 -> if (Random.nextBoolean()) ObstacleType.SPIKE else ObstacleType.BIRD
            else -> ObstacleType.entries[Random.nextInt(ObstacleType.entries.size)]
        }

        // Posicionar el obstáculo en un lugar aleatorio pero no bloqueando completamente
        val x = Random.nextFloat() * (screenWidth - GameConstants.OBSTACLE_SIZE)

        val velocityX = when (type) {
            ObstacleType.BIRD -> GameConstants.OBSTACLE_SPEED * (if (Random.nextBoolean()) 1 else -1)
            ObstacleType.BAT -> GameConstants.OBSTACLE_SPEED * 0.7f * (if (Random.nextBoolean()) 1 else -1)
            else -> 0f
        }

        return Obstacle(
            x = x,
            y = y - 60f, // Posicionar un poco más arriba de la plataforma
            type = type,
            velocityX = velocityX
        )
    }

    fun cleanupPlatforms(
        platforms: List<Platform>,
        cameraY: Float,
        screenHeight: Float
    ): List<Platform> {
        return platforms.filter { platform ->
            platform.y < cameraY + screenHeight + 100f
        }
    }

    fun cleanupObstacles(
        obstacles: List<Obstacle>,
        cameraY: Float,
        screenHeight: Float
    ): List<Obstacle> {
        return obstacles.filter { obstacle ->
            obstacle.y < cameraY + screenHeight + 100f
        }
    }
}
