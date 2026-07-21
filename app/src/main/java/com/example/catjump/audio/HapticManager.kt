package com.example.catjump.audio

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.catjump.domain.model.HapticEvent

/** Gestiona la vibración (haptics) del juego, respetando la preferencia del usuario. */
class HapticManager(context: Context) {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager)?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private var enabled = true

    fun setEnabled(value: Boolean) {
        enabled = value
    }

    fun vibrate(event: HapticEvent) {
        if (!enabled) return
        val v = vibrator ?: return
        if (!v.hasVibrator()) return

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = when (event) {
                    // Waveforms para eventos importantes: buzz doble = más notorio
                    HapticEvent.GAME_OVER -> VibrationEffect.createWaveform(
                        longArrayOf(0, 120, 60, 200), -1
                    )
                    HapticEvent.LOSE_LIFE -> VibrationEffect.createWaveform(
                        longArrayOf(0, 70, 40, 90), -1
                    )
                    // Pulsos simples a fuerza máxima (DEFAULT_AMPLITUDE) para que se sientan
                    HapticEvent.GAIN_LIFE -> VibrationEffect.createOneShot(60L, VibrationEffect.DEFAULT_AMPLITUDE)
                    HapticEvent.SPRING -> VibrationEffect.createOneShot(45L, VibrationEffect.DEFAULT_AMPLITUDE)
                    HapticEvent.EAT -> VibrationEffect.createOneShot(35L, VibrationEffect.DEFAULT_AMPLITUDE)
                }
                v.vibrate(effect)
            } else {
                // API 24-25: API antigua por milisegundos
                val durationMs = when (event) {
                    HapticEvent.EAT -> 35L
                    HapticEvent.SPRING -> 45L
                    HapticEvent.GAIN_LIFE -> 60L
                    HapticEvent.LOSE_LIFE -> 90L
                    HapticEvent.GAME_OVER -> 200L
                }
                @Suppress("DEPRECATION")
                v.vibrate(durationMs)
            }
        } catch (_: Exception) {
            // Ignorar: la vibración no es crítica
        }
    }
}
