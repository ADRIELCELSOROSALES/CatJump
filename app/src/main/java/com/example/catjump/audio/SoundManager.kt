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
    private var dogPlayer: MediaPlayer? = null

    // SoundPool for short, frequent sounds (jump, lose life)
    private val soundPool: SoundPool
    private var jumpSoundId: Int = 0
    private var loseLifeSoundId: Int = 0
    private var soundsLoaded = false

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(4)
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
    }

    fun startBackgroundMusic() {
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
        if (soundsLoaded) {
            soundPool.play(jumpSoundId, 0.3f, 0.3f, 1, 0, 1f)
        }
    }

    fun playLoseLifeSound() {
        if (soundsLoaded) {
            soundPool.play(loseLifeSoundId, 0.6f, 0.6f, 2, 0, 1f)
        }
    }

    fun playDogAppearedSound() {
        try {
            // Only play if no dog sound is currently playing
            if (dogPlayer?.isPlaying == true) {
                Log.d(TAG, "Dog sound already playing, skipping")
                return
            }

            dogPlayer?.release()

            // Randomly choose between the two dog sounds
            val dogSoundRes = if (Math.random() < 0.5) {
                R.raw.aparicionperro
            } else {
                R.raw.aparicionperroperro
            }

            dogPlayer = MediaPlayer.create(context, dogSoundRes).apply {
                setVolume(0.6f, 0.6f)
                setOnCompletionListener { mp ->
                    mp.release()
                    if (dogPlayer == mp) {
                        dogPlayer = null
                    }
                }
                start()
            }
            Log.d(TAG, "Dog appeared sound played")
        } catch (e: Exception) {
            Log.e(TAG, "Error playing dog appeared sound", e)
        }
    }

    fun release() {
        try {
            musicPlayer?.release()
            musicPlayer = null
            gameOverPlayer?.release()
            gameOverPlayer = null
            dogPlayer?.release()
            dogPlayer = null
            soundPool.release()
            Log.d(TAG, "SoundManager released")
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing SoundManager", e)
        }
    }
}
