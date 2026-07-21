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
        scoreRepository.getMusicEnabled(),
        scoreRepository.getSfxEnabled(),
        scoreRepository.getVibrationEnabled(),
        scoreRepository.getControlMode()
    ) { music, sfx, vibration, controlModeId ->
        GameSettings(
            musicEnabled = music,
            sfxEnabled = sfx,
            vibrationEnabled = vibration,
            controlMode = ControlMode.fromId(controlModeId)
        )
    }
}
