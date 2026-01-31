package com.example.catjump.game

import com.example.catjump.domain.model.Obstacle
import com.example.catjump.domain.model.ObstacleType
import com.example.catjump.domain.model.Platform
import com.example.catjump.domain.model.PlatformType
import com.example.catjump.domain.model.PowerUp
import com.example.catjump.domain.model.PowerUpType
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

        // Flying obstacles only (BIRD and BAT - CACTUS goes on platforms)
        val flyingTypes = listOf(ObstacleType.BIRD, ObstacleType.BAT)
        val type = when {
            level < 4 -> ObstacleType.BIRD
            else -> flyingTypes[Random.nextInt(flyingTypes.size)]
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

    // Genera un ratón encima de una plataforma
    fun generateMouseOnPlatform(platform: Platform): Obstacle? {
        // Solo generar en plataformas normales o con resorte
        if (platform.type == PlatformType.FRAGILE || platform.type == PlatformType.MOVING) {
            return null
        }

        // Probabilidad de generar ratón
        if (Random.nextFloat() > GameConstants.MOUSE_SPAWN_CHANCE) {
            return null
        }

        // Posicionar el ratón encima de la plataforma
        val mouseX = platform.x + (platform.width - GameConstants.MOUSE_SIZE) / 2 +
                     Random.nextFloat() * (platform.width - GameConstants.MOUSE_SIZE) * 0.5f -
                     (platform.width - GameConstants.MOUSE_SIZE) * 0.25f

        return Obstacle(
            x = mouseX.coerceIn(platform.x, platform.x + platform.width - GameConstants.MOUSE_SIZE),
            y = platform.y - GameConstants.MOUSE_SIZE,
            width = GameConstants.MOUSE_SIZE,
            height = GameConstants.MOUSE_SIZE,
            type = ObstacleType.MOUSE,
            velocityX = 0f
        )
    }

    // Genera un cactus encima de una plataforma
    fun generateCactusOnPlatform(platform: Platform): Obstacle? {
        // Solo generar en plataformas normales
        if (platform.type != PlatformType.NORMAL) {
            return null
        }

        // Probabilidad de generar cactus
        if (Random.nextFloat() > GameConstants.CACTUS_SPAWN_CHANCE) {
            return null
        }

        // Posicionar el cactus encima de la plataforma
        val cactusX = platform.x + (platform.width - GameConstants.CACTUS_SIZE) / 2 +
                      Random.nextFloat() * (platform.width - GameConstants.CACTUS_SIZE) * 0.4f -
                      (platform.width - GameConstants.CACTUS_SIZE) * 0.2f

        return Obstacle(
            x = cactusX.coerceIn(platform.x, platform.x + platform.width - GameConstants.CACTUS_SIZE),
            y = platform.y - GameConstants.CACTUS_SIZE,
            width = GameConstants.CACTUS_SIZE,
            height = GameConstants.CACTUS_SIZE,
            type = ObstacleType.CACTUS,
            velocityX = 0f
        )
    }

    // Genera un perro encima de una plataforma (camina de lado a lado)
    fun generateDogOnPlatform(platform: Platform): Obstacle? {
        // Solo generar en plataformas normales (no frágiles, no móviles, no resorte)
        if (platform.type != PlatformType.NORMAL) {
            return null
        }

        // Probabilidad de generar perro
        if (Random.nextFloat() > GameConstants.DOG_SPAWN_CHANCE) {
            return null
        }

        // Posicionar el perro en el centro de la plataforma
        val dogX = platform.x + (platform.width - GameConstants.DOG_SIZE) / 2

        // El perro camina de lado a lado en la plataforma
        val walkSpeed = GameConstants.DOG_WALK_SPEED * (if (Random.nextBoolean()) 1 else -1)

        return Obstacle(
            x = dogX,
            y = platform.y - GameConstants.DOG_SIZE,
            width = GameConstants.DOG_SIZE,
            height = GameConstants.DOG_SIZE,
            type = ObstacleType.DOG,
            velocityX = walkSpeed
        )
    }

    // Genera un power-up flotando encima de una plataforma
    fun generatePowerUpOnPlatform(platform: Platform): PowerUp? {
        // Solo generar en plataformas normales o con resorte
        if (platform.type == PlatformType.FRAGILE || platform.type == PlatformType.MOVING) {
            return null
        }

        // Probabilidad de generar power-up
        if (Random.nextFloat() > GameConstants.POWERUP_SPAWN_CHANCE) {
            return null
        }

        // Posicionar el power-up encima de la plataforma (flotando más alto)
        val powerUpX = platform.x + (platform.width - GameConstants.POWERUP_SIZE) / 2
        val powerUpY = platform.y - GameConstants.POWERUP_SIZE - 40f  // Flotando arriba

        // Tipo aleatorio de power-up
        val type = if (Random.nextBoolean()) PowerUpType.JETPACK else PowerUpType.KIBBLE

        return PowerUp(
            x = powerUpX,
            y = powerUpY,
            type = type
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

    fun cleanupPowerUps(
        powerUps: List<PowerUp>,
        cameraY: Float,
        screenHeight: Float
    ): List<PowerUp> {
        return powerUps.filter { powerUp ->
            powerUp.y < cameraY + screenHeight + 100f
        }
    }
}
