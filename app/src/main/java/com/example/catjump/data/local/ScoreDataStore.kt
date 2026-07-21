package com.example.catjump.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "catjump_scores")

class ScoreDataStore(private val context: Context) {

    private object PreferencesKeys {
        val HIGH_SCORE = intPreferencesKey("high_score")
        val SELECTED_SKIN = stringPreferencesKey("selected_skin")
        val MUSIC_ENABLED = booleanPreferencesKey("music_enabled")
        val SFX_ENABLED = booleanPreferencesKey("sfx_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val CONTROL_MODE = stringPreferencesKey("control_mode")
        val TUTORIAL_SEEN = booleanPreferencesKey("tutorial_seen")
    }

    val highScore: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.HIGH_SCORE] ?: 0
        }

    val selectedSkinId: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.SELECTED_SKIN] ?: "orange"
        }

    val musicEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.MUSIC_ENABLED] ?: true
        }

    val sfxEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.SFX_ENABLED] ?: true
        }

    val vibrationEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.VIBRATION_ENABLED] ?: true
        }

    val controlMode: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.CONTROL_MODE] ?: "TAP"
        }

    val tutorialSeen: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.TUTORIAL_SEEN] ?: false
        }

    suspend fun saveHighScore(score: Int) {
        context.dataStore.edit { preferences ->
            val currentHighScore = preferences[PreferencesKeys.HIGH_SCORE] ?: 0
            if (score > currentHighScore) {
                preferences[PreferencesKeys.HIGH_SCORE] = score
            }
        }
    }

    suspend fun saveSelectedSkin(skinId: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_SKIN] = skinId
        }
    }

    suspend fun saveMusicEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.MUSIC_ENABLED] = enabled
        }
    }

    suspend fun saveSfxEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SFX_ENABLED] = enabled
        }
    }

    suspend fun saveVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.VIBRATION_ENABLED] = enabled
        }
    }

    suspend fun saveControlMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CONTROL_MODE] = mode
        }
    }

    suspend fun saveTutorialSeen(seen: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TUTORIAL_SEEN] = seen
        }
    }
}
