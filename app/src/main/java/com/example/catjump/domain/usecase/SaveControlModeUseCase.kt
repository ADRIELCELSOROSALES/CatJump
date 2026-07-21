package com.example.catjump.domain.usecase

import com.example.catjump.domain.model.ControlMode
import com.example.catjump.domain.repository.ScoreRepository

class SaveControlModeUseCase(
    private val scoreRepository: ScoreRepository
) {
    suspend operator fun invoke(mode: ControlMode) {
        scoreRepository.saveControlMode(mode.name)
    }
}
