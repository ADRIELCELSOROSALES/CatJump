package com.example.catjump.domain.usecase

import com.example.catjump.domain.model.ControlMode
import com.example.catjump.domain.model.GameSettings
import com.example.catjump.domain.repository.ScoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetSettingsUseCase(
    private val scoreRepository: ScoreRepository
) {
    operator fun invoke(): Flow<GameSettings> = combine(
        scoreRepository.getSoundEnabled(),
        scoreRepository.getControlMode()
    ) { soundEnabled, controlModeId ->
        GameSettings(
            soundEnabled = soundEnabled,
            controlMode = ControlMode.fromId(controlModeId)
        )
    }
}
