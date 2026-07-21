package com.example.catjump.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.example.catjump.domain.model.CatSkin
import com.example.catjump.domain.model.CatSkins
import com.example.catjump.domain.model.ControlMode
import com.example.catjump.domain.model.GameState
import com.example.catjump.game.GameConstants
import com.example.catjump.presentation.components.GameBackground
import com.example.catjump.presentation.components.GameButton
import com.example.catjump.presentation.components.GameCanvas
import com.example.catjump.presentation.components.ScoreDisplay
import com.example.catjump.presentation.components.SecondaryGameButton

@Composable
fun GameScreen(
    gameStateProvider: () -> GameState?,
    isReady: Boolean,
    isPaused: Boolean,
    onStartGame: (Float, Float) -> Unit,
    onTick: (Long, Float) -> Unit,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    onStopMoving: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onExitToMenu: () -> Unit,
    controlMode: ControlMode = ControlMode.TAP,
    modifier: Modifier = Modifier,
    catSkin: CatSkin = CatSkins.ORANGE
) {
    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val screenWidthPx = with(density) { maxWidth.toPx() }
        val screenHeightPx = with(density) { maxHeight.toPx() }

        // Inicializar el juego con las dimensiones de pantalla cuando aún no arrancó
        LaunchedEffect(screenWidthPx, screenHeightPx, isReady) {
            if (!isReady && screenWidthPx > 0 && screenHeightPx > 0) {
                onStartGame(screenWidthPx, screenHeightPx)
            }
        }

        if (!isReady) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1a237e)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Loading...", color = Color.White)
            }
            return@BoxWithConstraints
        }

        // --- Reloj de frames: sincroniza la física con el refresh real de la pantalla ---
        // Se detiene solo cuando isPaused == true (el efecto se cancela) y también cuando la
        // app está en segundo plano (Choreographer no emite frames), evitando morir en pausa.
        LaunchedEffect(isPaused) {
            if (isPaused) return@LaunchedEffect
            var lastFrameNanos = 0L
            while (true) {
                val frameNanos = withFrameNanos { it }
                val nowMs = frameNanos / 1_000_000L
                if (lastFrameNanos != 0L) {
                    val deltaMs = (frameNanos - lastFrameNanos) / 1_000_000f
                    val deltaFrames = deltaMs / GameConstants.TARGET_FRAME_MS
                    onTick(nowMs, deltaFrames)
                }
                lastFrameNanos = frameNanos
            }
        }

        // Auto-pausa al mandar la app a segundo plano
        LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
            onPause()
        }

        Box(modifier = Modifier.fillMaxSize()) {
            // Fondo con parallax (lee cameraY/score de forma diferida en su Canvas)
            GameBackground(
                cameraYProvider = { gameStateProvider()?.cameraY ?: 0f },
                scoreProvider = { gameStateProvider()?.score ?: 0 }
            )

            // Elementos del juego: lee el estado dentro del draw del Canvas (sin recomposición)
            GameCanvas(gameStateProvider = gameStateProvider, catSkin = catSkin)

            // Control por toque (mitad izquierda/derecha), solo en modo TAP y sin pausa
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(isPaused, controlMode) {
                        if (!isPaused && controlMode == ControlMode.TAP) {
                            detectTapGestures(
                                onPress = { offset ->
                                    if (offset.x < size.width / 2) onMoveLeft() else onMoveRight()
                                    tryAwaitRelease()
                                    onStopMoving()
                                }
                            )
                        }
                    }
            )

            // Control por inclinación (acelerómetro), solo en modo TILT y sin pausa
            TiltControl(
                enabled = !isPaused && controlMode == ControlMode.TILT,
                onMoveLeft = onMoveLeft,
                onMoveRight = onMoveRight,
                onStopMoving = onStopMoving
            )

            // HUD (recomposición acotada a este composable vía lectura diferida)
            GameHud(
                gameStateProvider = gameStateProvider,
                modifier = Modifier.align(Alignment.TopCenter)
            )

            // Botón de pausa
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickable { onPause() }
                    .semantics { contentDescription = "Pausar" },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "II", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            // Overlay de pausa
            if (isPaused) {
                PauseOverlay(onResume = onResume, onExitToMenu = onExitToMenu)
            }
        }
    }
}

/**
 * Control por acelerómetro. Registra un listener mientras [enabled] es true e inclina
 * al gato según el eje X: inclinar hacia la izquierda mueve a la izquierda y viceversa.
 */
@Composable
private fun TiltControl(
    enabled: Boolean,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    onStopMoving: () -> Unit
) {
    val context = LocalContext.current
    // rememberUpdatedState: el listener siempre usa los callbacks más recientes
    val moveLeft by rememberUpdatedState(onMoveLeft)
    val moveRight by rememberUpdatedState(onMoveRight)
    val stopMoving by rememberUpdatedState(onStopMoving)

    DisposableEffect(enabled) {
        if (!enabled) {
            stopMoving()
            return@DisposableEffect onDispose { }
        }

        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        val listener = object : SensorEventListener {
            private var lastDir = 0
            override fun onSensorChanged(event: SensorEvent) {
                val x = event.values[0]
                val threshold = 1.5f // zona muerta para evitar jitter
                val dir = when {
                    x < -threshold -> -1 // inclinado a la izquierda
                    x > threshold -> 1   // inclinado a la derecha
                    else -> 0
                }
                if (dir != lastDir) {
                    lastDir = dir
                    when (dir) {
                        -1 -> moveLeft()
                        1 -> moveRight()
                        else -> stopMoving()
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (accelerometer != null) {
            sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        }

        onDispose {
            sensorManager?.unregisterListener(listener)
            stopMoving()
        }
    }
}

/** Lee el estado por-frame en su propio scope para no recomponer el resto de la UI. */
@Composable
private fun GameHud(
    gameStateProvider: () -> GameState?,
    modifier: Modifier = Modifier
) {
    val state = gameStateProvider() ?: return
    ScoreDisplay(
        score = state.score,
        highScore = state.highScore,
        level = state.level,
        lives = state.cat.lives,
        modifier = modifier
    )
}

@Composable
private fun PauseOverlay(
    onResume: () -> Unit,
    onExitToMenu: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .pointerInput(Unit) { detectTapGestures { } }, // Consume taps de fondo
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "PAUSA",
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            GameButton(text = "Reanudar", onClick = onResume)
            SecondaryGameButton(text = "Menú", onClick = onExitToMenu)
        }
    }
}
