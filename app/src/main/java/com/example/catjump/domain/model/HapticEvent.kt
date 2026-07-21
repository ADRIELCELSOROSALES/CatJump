package com.example.catjump.domain.model

/** Eventos que disparan vibración (haptics). */
enum class HapticEvent {
    SPRING,     // Rebote en plataforma resorte
    EAT,        // Comer un animalito
    GAIN_LIFE,  // Ganar una vida
    LOSE_LIFE,  // Perder una vida
    GAME_OVER   // Fin del juego
}
