package com.example.catjump.di

import android.content.Context
import com.example.catjump.data.local.ScoreDataStore
import com.example.catjump.data.repository.ScoreRepositoryImpl
import com.example.catjump.domain.repository.ScoreRepository
import com.example.catjump.domain.usecase.GetHighScoreUseCase
import com.example.catjump.domain.usecase.GetSelectedSkinUseCase
import com.example.catjump.domain.usecase.SaveHighScoreUseCase
import com.example.catjump.domain.usecase.SaveSelectedSkinUseCase
import com.example.catjump.game.CollisionDetector
import com.example.catjump.game.DifficultyManager
import com.example.catjump.game.GameEngine
import com.example.catjump.game.PlatformGenerator

class AppContainer(context: Context) {

    // Data Layer
    private val scoreDataStore = ScoreDataStore(context)
    private val scoreRepository: ScoreRepository = ScoreRepositoryImpl(scoreDataStore)

    // Use Cases
    val getHighScoreUseCase = GetHighScoreUseCase(scoreRepository)
    val saveHighScoreUseCase = SaveHighScoreUseCase(scoreRepository)
    val getSelectedSkinUseCase = GetSelectedSkinUseCase(scoreRepository)
    val saveSelectedSkinUseCase = SaveSelectedSkinUseCase(scoreRepository)

    // Game Engine Components
    private val difficultyManager = DifficultyManager()
    private val collisionDetector = CollisionDetector()
    private val platformGenerator = PlatformGenerator(difficultyManager)

    val gameEngine = GameEngine(
        platformGenerator = platformGenerator,
        collisionDetector = collisionDetector,
        difficultyManager = difficultyManager
    )
}
