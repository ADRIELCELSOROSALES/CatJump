package com.example.catjump.domain.usecase

import com.example.catjump.domain.repository.ScoreRepository

/** Guarda las preferencias de audio y vibración (una por una). */
class SaveAudioSettingsUseCase(
    private val scoreRepository: ScoreRepository
) {
    suspend fun setMusicEnabled(enabled: Boolean) = scoreRepository.saveMusicEnabled(enabled)
    suspend fun setSfxEnabled(enabled: Boolean) = scoreRepository.saveSfxEnabled(enabled)
    suspend fun setVibrationEnabled(enabled: Boolean) = scoreRepository.saveVibrationEnabled(enabled)
}
