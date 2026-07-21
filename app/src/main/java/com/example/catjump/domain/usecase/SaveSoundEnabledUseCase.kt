package com.example.catjump.domain.usecase

import com.example.catjump.domain.repository.ScoreRepository

class SaveSoundEnabledUseCase(
    private val scoreRepository: ScoreRepository
) {
    suspend operator fun invoke(enabled: Boolean) {
        scoreRepository.saveSoundEnabled(enabled)
    }
}
