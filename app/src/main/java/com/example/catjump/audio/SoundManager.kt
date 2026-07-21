package com.example.catjump.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.util.Log
import com.example.catjump.R

private const val TAG = "SoundManager"

class SoundManager(private val context: Context) {

    private var musicPlayer: MediaPlayer? = null
    private var gameOverPlayer: MediaPlayer? = null

    // SoundPool para efectos cortos y frecuentes (salto, perder vida, perro)
    private val soundPool: SoundPool
    private var jumpSoundId: Int = 0
    private var loseLifeSoundId: Int = 0
    private var dogSoundId1: Int = 0
    private var dogSoundId2: Int = 0
    private var soundsLoaded = false

    /** Preferencias de audio del usuario (música y efectos por separado). */
    private var musicEnabled = true
    private var sfxEnabled = true

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        soundPool.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) {
                soundsLoaded = true
                Log.d(TAG, "Sound pool loaded")
            }
        }

        // Load sounds
        jumpSoundId = soundPool.load(context, R.raw.salto, 1)
        loseLifeSoundId = soundPool.load(context, R.raw.perdervida, 1)
        dogSoundId1 = soundPool.load(context, R.raw.aparicionperro, 1)
        dogSoundId2 = soundPool.load(context, R.raw.aparicionperroperro, 1)
    }

    /** Activa/desactiva la música de fondo. Al desactivarse, la corta. */
    fun setMusicEnabled(enabled: Boolean) {
        musicEnabled = enabled
        if (!enabled) {
            stopBackgroundMusic()
        }
    }

    /** Activa/desactiva los efectos de sonido (salto, comer, perder vida, etc.). */
    fun setSfxEnabled(enabled: Boolean) {
        sfxEnabled = enabled
    }

    fun startBackgroundMusic() {
        if (!musicEnabled) return
        try {
            stopBackgroundMusic()

            musicPlayer = MediaPlayer.create(context, R.raw.musicloop).apply {
                isLooping = true
                setVolume(0.5f, 0.5f)
                start()
            }
            Log.d(TAG, "Background music started")
        } catch (e: Exception) {
            Log.e(TAG, "Error starting background music", e)
        }
    }

    fun stopBackgroundMusic() {
        try {
            musicPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.release()
            }
            musicPlayer = null
            Log.d(TAG, "Background music stopped")
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping background music", e)
        }
    }

    fun playGameOverSound() {
        if (!sfxEnabled) return
        try {
            gameOverPlayer?.release()

            gameOverPlayer = MediaPlayer.create(context, R.raw.gameover).apply {
                setVolume(0.7f, 0.7f)
                setOnCompletionListener { mp ->
                    mp.release()
                }
                start()
            }
            Log.d(TAG, "Game over sound played")
        } catch (e: Exception) {
            Log.e(TAG, "Error playing game over sound", e)
        }
    }

    fun playJumpSound() {
        if (sfxEnabled && soundsLoaded) {
            soundPool.play(jumpSoundId, 0.3f, 0.3f, 1, 0, 1f)
        }
    }

    fun playLoseLifeSound() {
        if (sfxEnabled && soundsLoaded) {
            soundPool.play(loseLifeSoundId, 0.6f, 0.6f, 2, 0, 1f)
        }
    }

    fun playDogAppearedSound() {
        if (!sfxEnabled || !soundsLoaded) return
        // Elegir aleatoriamente uno de los dos sonidos de perro
        val dogSoundId = if (Math.random() < 0.5) dogSoundId1 else dogSoundId2
        soundPool.play(dogSoundId, 0.6f, 0.6f, 1, 0, 1f)
    }

    fun release() {
        try {
            musicPlayer?.release()
            musicPlayer = null
            gameOverPlayer?.release()
            gameOverPlayer = null
            soundPool.release()
            Log.d(TAG, "SoundManager released")
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing SoundManager", e)
        }
    }
}
