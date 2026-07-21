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

    override fun getSoundEnabled(): Flow<Boolean> = scoreDataStore.soundEnabled

    override suspend fun saveSoundEnabled(enabled: Boolean) {
        scoreDataStore.saveSoundEnabled(enabled)
    }

    override fun getControlMode(): Flow<String> = scoreDataStore.controlMode

    override suspend fun saveControlMode(mode: String) {
        scoreDataStore.saveControlMode(mode)
    }
}
