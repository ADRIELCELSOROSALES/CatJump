package com.example.catjump.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.catjump.audio.HapticManager
import com.example.catjump.audio.SoundManager
import com.example.catjump.domain.model.CatSkin
import com.example.catjump.domain.model.HapticEvent
import com.example.catjump.domain.model.SoundEvent
import com.example.catjump.presentation.screens.GameOverScreen
import com.example.catjump.presentation.screens.GameScreen
import com.example.catjump.presentation.screens.MenuScreen
import com.example.catjump.presentation.screens.SettingsScreen
import com.example.catjump.presentation.screens.SkinSelectionScreen
import com.example.catjump.presentation.viewmodel.GameUiState
import com.example.catjump.presentation.viewmodel.GameViewModel

sealed class Screen(val route: String) {
    data object Menu : Screen("menu")
    data object Game : Screen("game")
    data object GameOver : Screen("game_over")
    data object Skins : Screen("skins")
    data object Settings : Screen("settings")
}

@Composable
fun CatJumpNavGraph(
    navController: NavHostController,
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val soundManager = remember { SoundManager(context) }
    val hapticManager = remember { HapticManager(context) }

    // Cleanup when composable is disposed
    DisposableEffect(Unit) {
        onDispose {
            soundManager.release()
        }
    }

    val uiState by viewModel.uiState.collectAsState()
    val highScore by viewModel.highScore.collectAsState()
    val selectedSkin by viewModel.selectedSkin.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val tutorialSeen by viewModel.tutorialSeen.collectAsState()

    // Aplicar preferencias de audio y vibración
    LaunchedEffect(settings.musicEnabled) { soundManager.setMusicEnabled(settings.musicEnabled) }
    LaunchedEffect(settings.sfxEnabled) { soundManager.setSfxEnabled(settings.sfxEnabled) }
    LaunchedEffect(settings.vibrationEnabled) { hapticManager.setEnabled(settings.vibrationEnabled) }

    // Vibración: colector global para no perder eventos entre transiciones de pantalla
    LaunchedEffect(Unit) {
        viewModel.hapticEvents.collect { event -> hapticManager.vibrate(event) }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Menu.route,
        modifier = modifier
    ) {
        composable(Screen.Menu.route) {
            // Ensure music is stopped when in menu
            LaunchedEffect(Unit) {
                soundManager.stopBackgroundMusic()
            }

            MenuScreen(
                highScore = highScore,
                selectedSkin = selectedSkin,
                onPlayClick = {
                    navController.navigate(Screen.Game.route) {
                        popUpTo(Screen.Menu.route) { inclusive = false }
                    }
                },
                onSkinsClick = {
                    navController.navigate(Screen.Skins.route)
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                settings = settings,
                onMusicToggle = { enabled -> viewModel.setMusicEnabled(enabled) },
                onSfxToggle = { enabled -> viewModel.setSfxEnabled(enabled) },
                onVibrationToggle = { enabled ->
                    viewModel.setVibrationEnabled(enabled)
                    // Buzz de prueba inmediato al activar (sin esperar el DataStore)
                    if (enabled) {
                        hapticManager.setEnabled(true)
                        hapticManager.vibrate(HapticEvent.LOSE_LIFE)
                    }
                },
                onControlModeSelected = { mode -> viewModel.setControlMode(mode) },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Skins.route) {
            SkinSelectionScreen(
                selectedSkin = selectedSkin,
                onSkinSelected = { skin -> viewModel.selectSkin(skin) },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Game.route) {
            val state = uiState

            // Música de fondo (reacciona al mute de música)
            LaunchedEffect(settings.musicEnabled) {
                if (settings.musicEnabled) {
                    soundManager.startBackgroundMusic()
                } else {
                    soundManager.stopBackgroundMusic()
                }
            }

            // Handle navigation to GameOver
            LaunchedEffect(state) {
                if (state is GameUiState.GameOver) {
                    navController.navigate(Screen.GameOver.route) {
                        popUpTo(Screen.Game.route) { inclusive = true }
                    }
                }
            }

            // Sonidos: se consumen desde un SharedFlow, desacoplados del render por-frame
            LaunchedEffect(Unit) {
                viewModel.soundEvents.collect { event ->
                    when (event) {
                        SoundEvent.JUMP -> soundManager.playJumpSound()
                        SoundEvent.LOSE_LIFE -> soundManager.playLoseLifeSound()
                        SoundEvent.DOG_APPEARED -> soundManager.playDogAppearedSound()
                    }
                }
            }

            if (state !is GameUiState.GameOver) {
                GameScreen(
                    gameStateProvider = { viewModel.gameState },
                    isReady = state is GameUiState.Playing,
                    isPaused = viewModel.isPaused,
                    onStartGame = { width, height -> viewModel.startGame(width, height) },
                    onTick = viewModel::tick,
                    onMoveLeft = viewModel::moveLeft,
                    onMoveRight = viewModel::moveRight,
                    onStopMoving = viewModel::stopMoving,
                    onPause = viewModel::pause,
                    onResume = viewModel::resume,
                    onExitToMenu = {
                        viewModel.goToMenu()
                        navController.navigate(Screen.Menu.route) {
                            popUpTo(Screen.Game.route) { inclusive = true }
                        }
                    },
                    controlMode = settings.controlMode,
                    tutorialSeen = tutorialSeen,
                    onTutorialDismissed = viewModel::markTutorialSeen,
                    catSkin = selectedSkin
                )
            }
        }

        composable(Screen.GameOver.route) {
            val gameOverState = uiState as? GameUiState.GameOver

            // Stop music and play game over sound
            LaunchedEffect(Unit) {
                soundManager.stopBackgroundMusic()
                soundManager.playGameOverSound()
            }

            GameOverScreen(
                score = gameOverState?.score ?: 0,
                highScore = gameOverState?.highScore ?: highScore,
                isNewHighScore = gameOverState?.isNewHighScore ?: false,
                onRetryClick = {
                    viewModel.prepareForRestart()
                    navController.navigate(Screen.Game.route) {
                        popUpTo(Screen.GameOver.route) { inclusive = true }
                    }
                },
                onMenuClick = {
                    viewModel.goToMenu()
                    navController.navigate(Screen.Menu.route) {
                        popUpTo(Screen.GameOver.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
