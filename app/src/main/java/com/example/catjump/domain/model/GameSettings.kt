package com.example.catjump.domain.model

/** Modo de control del gato. */
enum class ControlMode {
    TAP,   // Tocar mitad izquierda/derecha de la pantalla
    TILT;  // Inclinar el dispositivo (acelerómetro)

    companion object {
        fun fromId(id: String?): ControlMode =
            entries.firstOrNull { it.name.equals(id, ignoreCase = true) } ?: TAP
    }
}

/** Preferencias del jugador persistidas localmente. */
data class GameSettings(
    val musicEnabled: Boolean = true,
    val sfxEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val controlMode: ControlMode = ControlMode.TAP
)
