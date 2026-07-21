package com.example.catjump.domain.repository

import kotlinx.coroutines.flow.Flow

interface ScoreRepository {
    fun getHighScore(): Flow<Int>
    suspend fun saveHighScore(score: Int)
    fun getSelectedSkinId(): Flow<String>
    suspend fun saveSelectedSkin(skinId: String)
    fun getSoundEnabled(): Flow<Boolean>
    suspend fun saveSoundEnabled(enabled: Boolean)
    fun getControlMode(): Flow<String>
    suspend fun saveControlMode(mode: String)
}
