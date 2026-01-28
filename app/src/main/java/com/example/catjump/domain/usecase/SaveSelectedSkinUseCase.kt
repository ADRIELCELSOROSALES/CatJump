package com.example.catjump.domain.usecase

import com.example.catjump.domain.repository.ScoreRepository

class SaveSelectedSkinUseCase(
    private val scoreRepository: ScoreRepository
) {
    suspend operator fun invoke(skinId: String) {
        scoreRepository.saveSelectedSkin(skinId)
    }
}
