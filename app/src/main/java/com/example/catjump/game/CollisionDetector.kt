package com.example.catjump.game

import com.example.catjump.domain.model.Cat
import com.example.catjump.domain.model.Obstacle
import com.example.catjump.domain.model.ObstacleType
import com.example.catjump.domain.model.Platform
import com.example.catjump.domain.model.PowerUp

class CollisionDetector {

    fun checkPlatformCollision(cat: Cat, platform: Platform): Boolean {
        if (!platform.isActive) return false

        // Solo detectar colisión cuando el gato está cayendo
        if (cat.velocityY <= 0) return false

        // Verificar que los pies del gato están sobre la plataforma
        val catFeetY = cat.bottom
        val platformTop = platform.y

        // El gato debe estar cayendo hacia la plataforma
        val verticalOverlap = catFeetY >= platformTop &&
                              catFeetY <= platformTop + platform.height + cat.velocityY

        // Verificar overlap horizontal (con un poco de tolerancia)
        val horizontalOverlap = cat.right > platform.x + 5f && cat.x < platform.right - 5f

        return verticalOverlap && horizontalOverlap
    }

    fun checkObstacleCollision(cat: Cat, obstacle: Obstacle): Boolean {
        // Colisión por bounding box con margen pequeño para ser más preciso
        val margin = 8f

        val catLeft = cat.x + margin
        val catRight = cat.right - margin
        val catTop = cat.y + margin
        val catBottom = cat.bottom - margin

        val obsLeft = obstacle.x + 5f
        val obsRight = obstacle.right - 5f
        val obsTop = obstacle.y + 5f
        val obsBottom = obstacle.bottom - 5f

        return catRight > obsLeft &&
               catLeft < obsRight &&
               catBottom > obsTop &&
               catTop < obsBottom
    }

    fun findCollidingPlatform(cat: Cat, platforms: List<Platform>): Platform? {
        return platforms.firstOrNull { platform ->
            checkPlatformCollision(cat, platform)
        }
    }

    // Encuentra obstáculos que hacen daño (CACTUS y DOG)
    fun findDamagingObstacle(cat: Cat, obstacles: List<Obstacle>): Obstacle? {
        if (cat.isInvincible) return null  // No recibe daño si es invencible
        return obstacles.firstOrNull { obstacle ->
            (obstacle.type == ObstacleType.CACTUS || obstacle.type == ObstacleType.DOG) &&
            checkObstacleCollision(cat, obstacle)
        }
    }

    // Encuentra aves/murciélagos/ratones que el gato puede comer
    fun findEdibleObstacle(cat: Cat, obstacles: List<Obstacle>): Obstacle? {
        return obstacles.firstOrNull { obstacle ->
            (obstacle.type == ObstacleType.BIRD ||
             obstacle.type == ObstacleType.BAT ||
             obstacle.type == ObstacleType.MOUSE) &&
            checkObstacleCollision(cat, obstacle)
        }
    }

    fun checkAnyObstacleCollision(cat: Cat, obstacles: List<Obstacle>): Boolean {
        return obstacles.any { obstacle ->
            checkObstacleCollision(cat, obstacle)
        }
    }

    fun checkPowerUpCollision(cat: Cat, powerUp: PowerUp): Boolean {
        val margin = 5f

        val catLeft = cat.x + margin
        val catRight = cat.right - margin
        val catTop = cat.y + margin
        val catBottom = cat.bottom - margin

        val puLeft = powerUp.x + 5f
        val puRight = powerUp.right - 5f
        val puTop = powerUp.y + 5f
        val puBottom = powerUp.bottom - 5f

        return catRight > puLeft &&
               catLeft < puRight &&
               catBottom > puTop &&
               catTop < puBottom
    }

    fun findCollidingPowerUp(cat: Cat, powerUps: List<PowerUp>): PowerUp? {
        return powerUps.firstOrNull { powerUp ->
            checkPowerUpCollision(cat, powerUp)
        }
    }
}
