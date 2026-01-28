package com.example.catjump.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
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
    }

    val highScore: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.HIGH_SCORE] ?: 0
        }

    val selectedSkinId: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.SELECTED_SKIN] ?: "orange"
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
}
