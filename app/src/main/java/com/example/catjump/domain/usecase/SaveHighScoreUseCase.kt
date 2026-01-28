package com.example.catjump.domain.usecase

import com.example.catjump.domain.repository.ScoreRepository

class SaveHighScoreUseCase(
    private val scoreRepository: ScoreRepository
) {
    suspend operator fun invoke(score: Int) {
        scoreRepository.saveHighScore(score)
    }
}
