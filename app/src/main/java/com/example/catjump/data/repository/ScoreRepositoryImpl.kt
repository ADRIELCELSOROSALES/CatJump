package com.example.catjump.data.repository

import com.example.catjump.data.local.ScoreDataStore
import com.example.catjump.domain.repository.ScoreRepository
import kotlinx.coroutines.flow.Flow

class ScoreRepositoryImpl(
    private val scoreDataStore: ScoreDataStore
) : ScoreRepository {

    override fun getHighScore(): Flow<Int> = scoreDataStore.highScore

    override suspend fun saveHighScore(score: Int) {
        scoreDataStore.saveHighScore(score)
    }

    override fun getSelectedSkinId(): Flow<String> = scoreDataStore.selectedSkinId

    override suspend fun saveSelectedSkin(skinId: String) {
        scoreDataStore.saveSelectedSkin(skinId)
    }

    override fun getMusicEnabled(): Flow<Boolean> = scoreDataStore.musicEnabled

    override suspend fun saveMusicEnabled(enabled: Boolean) {
        scoreDataStore.saveMusicEnabled(enabled)
    }

    override fun getSfxEnabled(): Flow<Boolean> = scoreDataStore.sfxEnabled

    override suspend fun saveSfxEnabled(enabled: Boolean) {
        scoreDataStore.saveSfxEnabled(enabled)
    }

    override fun getVibrationEnabled(): Flow<Boolean> = scoreDataStore.vibrationEnabled

    override suspend fun saveVibrationEnabled(enabled: Boolean) {
        scoreDataStore.saveVibrationEnabled(enabled)
    }

    override fun getControlMode(): Flow<String> = scoreDataStore.controlMode

    override suspend fun saveControlMode(mode: String) {
        scoreDataStore.saveControlMode(mode)
    }

    override fun getTutorialSeen(): Flow<Boolean> = scoreDataStore.tutorialSeen

    override suspend fun saveTutorialSeen(seen: Boolean) {
        scoreDataStore.saveTutorialSeen(seen)
    }
}
