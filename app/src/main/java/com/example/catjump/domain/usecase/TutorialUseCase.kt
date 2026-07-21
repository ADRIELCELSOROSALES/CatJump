package com.example.catjump.domain.usecase

import com.example.catjump.domain.repository.ScoreRepository
import kotlinx.coroutines.flow.Flow

/** Lee y marca si el tutorial de primera vez ya fue visto. */
class TutorialUseCase(
    private val scoreRepository: ScoreRepository
) {
    fun hasSeen(): Flow<Boolean> = scoreRepository.getTutorialSeen()
    suspend fun markSeen() = scoreRepository.saveTutorialSeen(true)
}
