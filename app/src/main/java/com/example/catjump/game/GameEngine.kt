package com.example.catjump.game

import com.example.catjump.domain.model.Cat
import com.example.catjump.domain.model.GameState
import com.example.catjump.domain.model.ObstacleType
import com.example.catjump.domain.model.Platform
import com.example.catjump.domain.model.PlatformType
import com.example.catjump.domain.model.PowerUpType

class GameEngine(
    private val platformGenerator: PlatformGenerator,
    private val collisionDetector: CollisionDetector,
    private val difficultyManager: DifficultyManager
) {
    private var moveDirection: Int = 0 // -1 left, 0 none, 1 right
    private var platformsSinceLastDamagingObstacle: Int = 0  // Contador para espaciar obstáculos

    fun setMoveDirection(direction: Int) {
        moveDirection = direction.coerceIn(-1, 1)
    }

    fun initializeGame(screenWidth: Float, screenHeight: Float, highScore: Int): GameState {
        val platforms = platformGenerator.generateInitialPlatforms(screenWidth, screenHeight)
        val initialState = GameState.initial(screenWidth, screenHeight)

        return initialState.copy(
            platforms = platforms,
            highScore = highScore
        )
    }

    fun update(state: GameState): GameState {
        if (state.isGameOver) return state

        var newState = state.copy(currentTime = System.currentTimeMillis())

        // Update power-up timers
        newState = updatePowerUpTimers(newState)

        // Update cat physics
        newState = updateCatPhysics(newState)

        // Update platforms (moving ones)
        newState = updatePlatforms(newState)

        // Update obstacles (including dog walking)
        newState = updateObstacles(newState)

        // Check collisions
        newState = checkCollisions(newState)

        // Update camera and score
        newState = updateCameraAndScore(newState)

        // Generate new platforms if needed
        newState = generateNewContent(newState)

        // Cleanup off-screen elements
        newState = cleanupOffScreen(newState)

        // Check game over
        newState = checkGameOver(newState)

        return newState
    }

    private fun updatePowerUpTimers(state: GameState): GameState {
        val cat = state.cat
        val currentTime = state.currentTime

        var updatedCat = cat

        // Check jetpack expiration
        if (cat.jetpackActive && currentTime >= cat.jetpackEndTime) {
            updatedCat = updatedCat.copy(jetpackActive = false)
        }

        // Check super jump expiration
        if (cat.superJumpActive && (currentTime >= cat.superJumpEndTime || cat.superJumpsRemaining <= 0)) {
            updatedCat = updatedCat.copy(superJumpActive = false, superJumpsRemaining = 0)
        }

        return state.copy(cat = updatedCat)
    }

    private fun updateCatPhysics(state: GameState): GameState {
        val cat = state.cat

        var newVelocityY: Float

        // Si el jetpack está activo, sube constantemente
        if (cat.jetpackActive) {
            newVelocityY = GameConstants.JETPACK_BOOST
        } else {
            // Apply gravity
            newVelocityY = cat.velocityY + GameConstants.GRAVITY
            newVelocityY = newVelocityY.coerceAtMost(GameConstants.MAX_FALL_VELOCITY)
        }

        // Apply horizontal movement
        val newVelocityX = moveDirection * GameConstants.HORIZONTAL_SPEED

        // Update position
        var newX = cat.x + newVelocityX
        val newY = cat.y + newVelocityY

        // Screen wrapping (horizontal)
        if (newX < -cat.width) {
            newX = state.screenWidth
        } else if (newX > state.screenWidth) {
            newX = -cat.width
        }

        val facingRight = when {
            newVelocityX > 0 -> true
            newVelocityX < 0 -> false
            else -> cat.facingRight
        }

        return state.copy(
            cat = cat.copy(
                x = newX,
                y = newY,
                velocityX = newVelocityX,
                velocityY = newVelocityY,
                isJumping = newVelocityY < 0,
                facingRight = facingRight
            )
        )
    }

    private fun updatePlatforms(state: GameState): GameState {
        val updatedPlatforms = state.platforms.map { platform ->
            if (platform.type == PlatformType.MOVING && platform.isActive) {
                var newX = platform.x + platform.velocityX

                // Bounce off screen edges
                val newVelocityX = if (newX <= 0 || newX >= state.screenWidth - platform.width) {
                    -platform.velocityX
                } else {
                    platform.velocityX
                }

                newX = newX.coerceIn(0f, state.screenWidth - platform.width)

                platform.copy(x = newX, velocityX = newVelocityX)
            } else {
                platform
            }
        }

        return state.copy(platforms = updatedPlatforms)
    }

    private fun updateObstacles(state: GameState): GameState {
        val updatedObstacles = state.obstacles.map { obstacle ->
            var newX = obstacle.x + obstacle.velocityX
            var newVelocityX = obstacle.velocityX

            // Los perros caminan dentro de su plataforma
            if (obstacle.type == ObstacleType.DOG) {
                // Rebotar en los límites de la plataforma
                if (newX <= obstacle.platformMinX || newX >= obstacle.platformMaxX) {
                    newVelocityX = -newVelocityX
                    newX = newX.coerceIn(obstacle.platformMinX, obstacle.platformMaxX)
                }
            } else {
                // Otros obstáculos rebotan en los bordes de la pantalla
                if (newX <= 0 || newX >= state.screenWidth - obstacle.width) {
                    newVelocityX = -newVelocityX
                    newX = newX.coerceIn(0f, state.screenWidth - obstacle.width)
                }
            }

            obstacle.copy(x = newX, velocityX = newVelocityX)
        }

        return state.copy(obstacles = updatedObstacles)
    }

    private fun checkCollisions(state: GameState): GameState {
        var cat = state.cat
        var obstacles = state.obstacles
        var powerUps = state.powerUps
        val currentTime = state.currentTime

        // Decrease invincibility frames
        if (cat.invincibilityFrames > 0) {
            cat = cat.copy(invincibilityFrames = cat.invincibilityFrames - 1)
        }

        // Check power-up collision
        val collidingPowerUp = collisionDetector.findCollidingPowerUp(cat, powerUps)
        if (collidingPowerUp != null) {
            // Apply power-up effect
            cat = when (collidingPowerUp.type) {
                PowerUpType.JETPACK -> cat.copy(
                    jetpackActive = true,
                    jetpackEndTime = currentTime + GameConstants.JETPACK_DURATION,
                    velocityY = GameConstants.JETPACK_BOOST
                )
                PowerUpType.KIBBLE -> cat.copy(
                    superJumpActive = true,
                    superJumpEndTime = currentTime + GameConstants.SUPER_JUMP_DURATION,
                    superJumpsRemaining = GameConstants.SUPER_JUMP_COUNT
                )
            }
            // Remove the collected power-up
            powerUps = powerUps.filter { it != collidingPowerUp }
        }

        // Check damaging obstacle collision (CACTUS and DOG)
        val damagingObstacle = collisionDetector.findDamagingObstacle(cat, obstacles)
        if (damagingObstacle != null) {
            val newLives = cat.lives - 1
            if (newLives <= 0) {
                return state.copy(isGameOver = true)
            }
            // Lose a life and become invincible temporarily
            cat = cat.copy(
                lives = newLives,
                invincibilityFrames = GameConstants.INVINCIBILITY_FRAMES
            )
            // Los perros y cactus NO desaparecen al hacer daño
        }

        // Check if cat eats a bird, bat or mouse
        val edibleObstacle = collisionDetector.findEdibleObstacle(cat, obstacles)
        if (edibleObstacle != null) {
            // Cat eats and gets fatter (up to 10 creatures max for visual growth)
            val newBirdsEaten = cat.birdsEaten + 1
            val newFatness = if (cat.fatness < GameConstants.MAX_FATNESS) {
                (cat.fatness + GameConstants.FATNESS_GAIN_PER_BIRD)
                    .coerceAtMost(GameConstants.MAX_FATNESS)
            } else {
                cat.fatness // Already at max, can still eat but won't grow more
            }

            // Cada 5 animalitos comidos, gana una vida (máximo 3 vidas)
            val newLives = if (newBirdsEaten % 5 == 0 && cat.lives < GameConstants.INITIAL_LIVES) {
                cat.lives + 1
            } else {
                cat.lives
            }

            cat = cat.copy(fatness = newFatness, birdsEaten = newBirdsEaten, lives = newLives)
            // Remove the eaten obstacle
            obstacles = obstacles.filter { it != edibleObstacle }
        }

        // Check platform collision (only when falling and not using jetpack)
        if (!cat.jetpackActive) {
            val collidingPlatform = collisionDetector.findCollidingPlatform(cat, state.platforms)

            if (collidingPlatform != null) {
                // Jump velocity - use super jump if active
                val jumpVelocity = when {
                    cat.superJumpActive && cat.superJumpsRemaining > 0 -> {
                        cat = cat.copy(superJumpsRemaining = cat.superJumpsRemaining - 1)
                        GameConstants.SUPER_JUMP_VELOCITY
                    }
                    collidingPlatform.type == PlatformType.SPRING -> GameConstants.SPRING_JUMP_VELOCITY
                    else -> GameConstants.JUMP_VELOCITY
                }

                val updatedPlatforms = if (collidingPlatform.type == PlatformType.FRAGILE) {
                    state.platforms.map { platform ->
                        if (platform == collidingPlatform) {
                            platform.copy(isActive = false)
                        } else {
                            platform
                        }
                    }
                } else {
                    state.platforms
                }

                return state.copy(
                    cat = cat.copy(
                        y = collidingPlatform.y - cat.height,
                        velocityY = jumpVelocity,
                        isJumping = true
                    ),
                    platforms = updatedPlatforms,
                    obstacles = obstacles,
                    powerUps = powerUps
                )
            }
        }

        return state.copy(cat = cat, obstacles = obstacles, powerUps = powerUps)
    }

    private fun updateCameraAndScore(state: GameState): GameState {
        val cat = state.cat
        val screenMiddle = state.cameraY + state.screenHeight / 3

        // Only move camera up, never down
        if (cat.y < screenMiddle) {
            val cameraOffset = screenMiddle - cat.y
            val newCameraY = state.cameraY - cameraOffset

            // Score is based on how high the cat has gone
            val heightClimbed = -newCameraY
            val newScore = heightClimbed.toInt().coerceAtLeast(state.score)
            val newLevel = difficultyManager.calculateLevel(newScore)
            val isNewHighScore = newScore > state.highScore

            return state.copy(
                cameraY = newCameraY,
                score = newScore,
                level = newLevel,
                isNewHighScore = isNewHighScore
            )
        }

        return state
    }

    private fun generateNewContent(state: GameState): GameState {
        val highestPlatform = state.platforms.minByOrNull { it.y }
        val visibleTop = state.cameraY

        if (highestPlatform == null || highestPlatform.y > visibleTop - 200f) {
            val newPlatform = platformGenerator.generateNewPlatform(
                screenWidth = state.screenWidth,
                highestPlatformY = highestPlatform?.y ?: visibleTop,
                level = state.level,
                lastPlatformX = highestPlatform?.x ?: (state.screenWidth / 2)
            )

            // Incrementar contador de plataformas
            platformsSinceLastDamagingObstacle++

            // Generate flying obstacle (bird, bat) - estos son comestibles, no dañinos
            val newObstacle = platformGenerator.generateObstacle(
                screenWidth = state.screenWidth,
                y = newPlatform.y,
                level = state.level
            )

            // Generate mouse on the platform (comestible)
            val mouseOnPlatform = platformGenerator.generateMouseOnPlatform(newPlatform)

            // Solo generar obstáculos dañinos si han pasado suficientes plataformas
            val canSpawnDamagingObstacle = platformsSinceLastDamagingObstacle >= GameConstants.MIN_DAMAGING_OBSTACLE_GAP

            // Generate dog on the platform (only if no mouse and enough gap)
            val dogOnPlatform = if (mouseOnPlatform == null && canSpawnDamagingObstacle) {
                val dog = platformGenerator.generateDogOnPlatform(newPlatform)
                if (dog != null) platformsSinceLastDamagingObstacle = 0
                dog
            } else null

            // Generate cactus on the platform (only if no mouse, no dog, and enough gap)
            val cactusOnPlatform = if (mouseOnPlatform == null && dogOnPlatform == null && canSpawnDamagingObstacle) {
                val cactus = platformGenerator.generateCactusOnPlatform(newPlatform)
                if (cactus != null) platformsSinceLastDamagingObstacle = 0
                cactus
            } else null

            // Generate power-up (only if no obstacles on platform)
            val powerUpOnPlatform = if (mouseOnPlatform == null && dogOnPlatform == null && cactusOnPlatform == null) {
                platformGenerator.generatePowerUpOnPlatform(newPlatform)
            } else null

            // Collect all new obstacles
            val newObstacles = listOfNotNull(newObstacle, mouseOnPlatform, dogOnPlatform, cactusOnPlatform)
            val newPowerUps = listOfNotNull(powerUpOnPlatform)

            return state.copy(
                platforms = state.platforms + newPlatform,
                obstacles = state.obstacles + newObstacles,
                powerUps = state.powerUps + newPowerUps
            )
        }

        return state
    }

    private fun cleanupOffScreen(state: GameState): GameState {
        return state.copy(
            platforms = platformGenerator.cleanupPlatforms(
                state.platforms,
                state.cameraY,
                state.screenHeight
            ),
            obstacles = platformGenerator.cleanupObstacles(
                state.obstacles,
                state.cameraY,
                state.screenHeight
            ),
            powerUps = platformGenerator.cleanupPowerUps(
                state.powerUps,
                state.cameraY,
                state.screenHeight
            )
        )
    }

    private fun checkGameOver(state: GameState): GameState {
        val cat = state.cat
        val bottomOfScreen = state.cameraY + state.screenHeight

        // Game over if cat falls below screen
        if (cat.y > bottomOfScreen + 100f) {
            return state.copy(isGameOver = true)
        }

        return state
    }
}
