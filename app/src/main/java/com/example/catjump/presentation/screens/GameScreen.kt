package com.example.catjump.presentation.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import kotlinx.coroutines.delay

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
    tutorialSeen: Boolean = true,
    onTutorialDismissed: () -> Unit = {},
    modifier: Modifier = Modifier,
    catSkin: CatSkin = CatSkins.ORANGE
) {
    val density = LocalDensity.current

    // Mantener la pantalla encendida mientras se juega
    val view = LocalView.current
    DisposableEffect(Unit) {
        view.keepScreenOn = true
        onDispose { view.keepScreenOn = false }
    }

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
                Text(text = "Cargando...", color = Color.White)
            }
            return@BoxWithConstraints
        }

        // --- Fases de arranque: tutorial (1ra vez) -> cuenta regresiva -> jugar ---
        var tutorialDismissed by remember(isReady) { mutableStateOf(false) }
        val showTutorial = !tutorialSeen && !tutorialDismissed
        val tutorialResolved = tutorialSeen || tutorialDismissed

        var countdown by remember(isReady) { mutableIntStateOf(GameConstants.COUNTDOWN_SECONDS) }
        LaunchedEffect(tutorialResolved) {
            if (tutorialResolved) {
                while (countdown > 0) {
                    delay(GameConstants.COUNTDOWN_STEP_MS)
                    countdown--
                }
            }
        }

        val canPlay = tutorialResolved && countdown == 0

        // --- Reloj de frames: solo corre cuando se puede jugar y no está en pausa ---
        // Se detiene con la app en segundo plano (Choreographer no emite frames).
        LaunchedEffect(canPlay, isPaused) {
            if (!canPlay || isPaused) return@LaunchedEffect
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

        // Botón atrás: pausa mientras se juega, reanuda si ya estaba en pausa
        BackHandler {
            if (isPaused) onResume() else onPause()
        }

        Box(modifier = Modifier.fillMaxSize()) {
            // Fondo con parallax (lee cameraY/score de forma diferida en su Canvas)
            GameBackground(
                cameraYProvider = { gameStateProvider()?.cameraY ?: 0f },
                scoreProvider = { gameStateProvider()?.score ?: 0 }
            )

            // Elementos del juego: lee el estado dentro del draw del Canvas (sin recomposición)
            GameCanvas(gameStateProvider = gameStateProvider, catSkin = catSkin)

            // Control por toque (mitad izquierda/derecha), solo en modo TAP mientras se juega
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(canPlay, isPaused, controlMode) {
                        if (canPlay && !isPaused && controlMode == ControlMode.TAP) {
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

            // Control por inclinación (acelerómetro), solo en modo TILT mientras se juega
            TiltControl(
                enabled = canPlay && !isPaused && controlMode == ControlMode.TILT,
                onMoveLeft = onMoveLeft,
                onMoveRight = onMoveRight,
                onStopMoving = onStopMoving
            )

            // HUD (recomposición acotada + respeta insets del sistema)
            GameHud(
                gameStateProvider = gameStateProvider,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            )

            // Botón de pausa (respeta insets)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .windowInsetsPadding(WindowInsets.safeDrawing)
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

            // Cuenta regresiva de inicio
            if (tutorialResolved && countdown > 0 && !isPaused) {
                CountdownOverlay(count = countdown)
            }

            // Tutorial de primera vez
            if (showTutorial) {
                TutorialOverlay(
                    controlMode = controlMode,
                    onDismiss = {
                        tutorialDismissed = true
                        onTutorialDismissed()
                    }
                )
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
                // Mapeo: inclinar el teléfono hacia un lado mueve al gato hacia ese lado.
                val dir = when {
                    x < -threshold -> 1  // eje X negativo -> mover a la derecha
                    x > threshold -> -1  // eje X positivo -> mover a la izquierda
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
private fun CountdownOverlay(count: Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.35f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = count.toString(),
            color = Color.White,
            fontSize = 96.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TutorialOverlay(
    controlMode: ControlMode,
    onDismiss: () -> Unit
) {
    val controlText = when (controlMode) {
        ControlMode.TAP -> "Tocá el lado izquierdo o derecho de la pantalla para mover al gato."
        ControlMode.TILT -> "Incliná el teléfono a los lados para mover al gato."
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .pointerInput(Unit) { detectTapGestures { } },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "CÓMO JUGAR",
                color = Color(0xFFFFD700),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(controlText, color = Color.White, fontSize = 16.sp, textAlign = TextAlign.Center)
                    Text(
                        "Saltá sobre las plataformas para subir.",
                        color = Color.White, fontSize = 16.sp, textAlign = TextAlign.Center
                    )
                    Text(
                        "Comé pájaros y ratones, esquivá perros y cactus. ¡No caigas!",
                        color = Color.White, fontSize = 16.sp, textAlign = TextAlign.Center
                    )
                }
            }
            GameButton(text = "¡Entendido!", onClick = onDismiss)
        }
    }
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
