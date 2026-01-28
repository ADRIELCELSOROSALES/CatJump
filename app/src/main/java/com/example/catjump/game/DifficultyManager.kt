package com.example.catjump.game

import com.example.catjump.domain.model.PlatformType
import kotlin.random.Random

class DifficultyManager {

    fun calculateLevel(score: Int): Int {
        return (score / GameConstants.POINTS_PER_LEVEL) + 1
    }

    fun getMaxPlatformGap(level: Int): Float {
        val baseGap = GameConstants.MAX_PLATFORM_GAP
        val increase = (level - 1) * 8f
        return (baseGap + increase).coerceAtMost(180f) // Limitado para ser siempre alcanzable
    }

    fun getMinPlatformGap(level: Int): Float {
        val baseGap = GameConstants.MIN_PLATFORM_GAP
        val increase = (level - 1) * 5f
        return (baseGap + increase).coerceAtMost(100f)
    }

    fun selectPlatformType(level: Int): PlatformType {
        val random = Random.nextFloat()

        return when {
            level == 1 -> PlatformType.NORMAL
            level == 2 -> {
                when {
                    random < 0.8f -> PlatformType.NORMAL
                    random < 0.95f -> PlatformType.SPRING
                    else -> PlatformType.MOVING
                }
            }
            level == 3 -> {
                when {
                    random < 0.6f -> PlatformType.NORMAL
                    random < 0.75f -> PlatformType.MOVING
                    random < 0.9f -> PlatformType.SPRING
                    else -> PlatformType.FRAGILE
                }
            }
            else -> {
                when {
                    random < 0.4f -> PlatformType.NORMAL
                    random < 0.6f -> PlatformType.MOVING
                    random < 0.8f -> PlatformType.FRAGILE
                    else -> PlatformType.SPRING
                }
            }
        }
    }

    fun shouldSpawnObstacle(level: Int): Boolean {
        if (level < 3) return false
        val chance = ((level - 2) * 0.05f).coerceAtMost(0.15f)
        return Random.nextFloat() < chance
    }

    fun getMovingPlatformSpeed(level: Int): Float {
        return GameConstants.MOVING_PLATFORM_SPEED + (level - 1) * 0.3f
    }
}
