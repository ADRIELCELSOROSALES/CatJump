package com.example.catjump.game

import com.example.catjump.domain.model.Cat
import com.example.catjump.domain.model.GameState
import com.example.catjump.domain.model.ObstacleType
import com.example.catjump.domain.model.Platform
import com.example.catjump.domain.model.PlatformType

class GameEngine(
    private val platformGenerator: PlatformGenerator,
    private val collisionDetector: CollisionDetector,
    private val difficultyManager: DifficultyManager
) {
    private var moveDirection: Int = 0 // -1 left, 0 none, 1 right

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

        var newState = state

        // Update cat physics
        newState = updateCatPhysics(newState)

        // Update platforms (moving ones)
        newState = updatePlatforms(newState)

        // Update obstacles
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

    private fun updateCatPhysics(state: GameState): GameState {
        val cat = state.cat

        // Apply gravity
        var newVelocityY = cat.velocityY + GameConstants.GRAVITY
        newVelocityY = newVelocityY.coerceAtMost(GameConstants.MAX_FALL_VELOCITY)

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

            // Bounce or wrap
            if (newX <= 0 || newX >= state.screenWidth - obstacle.width) {
                newVelocityX = -newVelocityX
                newX = newX.coerceIn(0f, state.screenWidth - obstacle.width)
            }

            obstacle.copy(x = newX, velocityX = newVelocityX)
        }

        return state.copy(obstacles = updatedObstacles)
    }

    private fun checkCollisions(state: GameState): GameState {
        var cat = state.cat
        var obstacles = state.obstacles

        // Decrease invincibility frames
        if (cat.invincibilityFrames > 0) {
            cat = cat.copy(invincibilityFrames = cat.invincibilityFrames - 1)
        }

        // Check damaging obstacle collision (SPIKE and DOG)
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
            // Remove the dog if it was a dog (spikes stay)
            if (damagingObstacle.type == ObstacleType.DOG) {
                obstacles = obstacles.filter { it != damagingObstacle }
            }
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
            cat = cat.copy(fatness = newFatness, birdsEaten = newBirdsEaten)
            // Remove the eaten obstacle
            obstacles = obstacles.filter { it != edibleObstacle }
        }

        // Check platform collision (only when falling)
        val collidingPlatform = collisionDetector.findCollidingPlatform(cat, state.platforms)

        if (collidingPlatform != null) {
            // Jump velocity (no reduction from fatness)
            val jumpVelocity = when (collidingPlatform.type) {
                PlatformType.SPRING -> GameConstants.SPRING_JUMP_VELOCITY
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
                obstacles = obstacles
            )
        }

        return state.copy(cat = cat, obstacles = obstacles)
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

            // Generate flying obstacle (bird, bat, spike)
            val newObstacle = platformGenerator.generateObstacle(
                screenWidth = state.screenWidth,
                y = newPlatform.y,
                level = state.level
            )

            // Generate mouse on the platform
            val mouseOnPlatform = platformGenerator.generateMouseOnPlatform(newPlatform)

            // Generate dog on the platform (only if no mouse)
            val dogOnPlatform = if (mouseOnPlatform == null) {
                platformGenerator.generateDogOnPlatform(newPlatform)
            } else null

            // Collect all new obstacles
            val newObstacles = listOfNotNull(newObstacle, mouseOnPlatform, dogOnPlatform)

            return state.copy(
                platforms = state.platforms + newPlatform,
                obstacles = state.obstacles + newObstacles
            )
        }

        return state
    }

    private fun cleanupOffScreen(state: GameState): GameState {
        val bottomOfScreen = state.cameraY + state.screenHeight

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
