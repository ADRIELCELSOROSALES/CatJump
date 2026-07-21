package com.example.catjump.domain.repository

import kotlinx.coroutines.flow.Flow

interface ScoreRepository {
    fun getHighScore(): Flow<Int>
    suspend fun saveHighScore(score: Int)
    fun getSelectedSkinId(): Flow<String>
    suspend fun saveSelectedSkin(skinId: String)
    fun getMusicEnabled(): Flow<Boolean>
    suspend fun saveMusicEnabled(enabled: Boolean)
    fun getSfxEnabled(): Flow<Boolean>
    suspend fun saveSfxEnabled(enabled: Boolean)
    fun getVibrationEnabled(): Flow<Boolean>
    suspend fun saveVibrationEnabled(enabled: Boolean)
    fun getControlMode(): Flow<String>
    suspend fun saveControlMode(mode: String)
    fun getTutorialSeen(): Flow<Boolean>
    suspend fun saveTutorialSeen(seen: Boolean)
}
