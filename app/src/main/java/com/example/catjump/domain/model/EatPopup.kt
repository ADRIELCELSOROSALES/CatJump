package com.example.catjump.domain.model

/**
 * Texto flotante "+1" que aparece al comer un animalito.
 * Coordenadas en el mundo (se convierten a pantalla con la cámara al dibujar).
 */
data class EatPopup(
    val id: Long,
    val x: Float,
    val y: Float,
    val createdTime: Long,
    val text: String = "+1"
)
