package com.example.catjump.domain.usecase

import com.example.catjump.domain.repository.ScoreRepository
import kotlinx.coroutines.flow.Flow

class GetHighScoreUseCase(
    private val scoreRepository: ScoreRepository
) {
    operator fun invoke(): Flow<Int> = scoreRepository.getHighScore()
}
