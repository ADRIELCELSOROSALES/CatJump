package com.example.catjump.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.catjump.domain.model.CatSkin
import com.example.catjump.presentation.screens.GameOverScreen
import com.example.catjump.presentation.screens.GameScreen
import com.example.catjump.presentation.screens.MenuScreen
import com.example.catjump.presentation.screens.SkinSelectionScreen
import com.example.catjump.presentation.viewmodel.GameUiState
import com.example.catjump.presentation.viewmodel.GameViewModel

sealed class Screen(val route: String) {
    data object Menu : Screen("menu")
    data object Game : Screen("game")
    data object GameOver : Screen("game_over")
    data object Skins : Screen("skins")
}

@Composable
fun CatJumpNavGraph(
    navController: NavHostController,
    viewModel: GameViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val highScore by viewModel.highScore.collectAsState()
    val selectedSkin by viewModel.selectedSkin.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Menu.route,
        modifier = modifier
    ) {
        composable(Screen.Menu.route) {
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
                }
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

            // Handle navigation to GameOver
            LaunchedEffect(state) {
                if (state is GameUiState.GameOver) {
                    navController.navigate(Screen.GameOver.route) {
                        popUpTo(Screen.Game.route) { inclusive = true }
                    }
                }
            }

            // Show game screen
            if (state is GameUiState.Playing) {
                GameScreen(
                    gameState = state.gameState,
                    onStartGame = { _, _ -> }, // Already started
                    onMoveLeft = viewModel::moveLeft,
                    onMoveRight = viewModel::moveRight,
                    onStopMoving = viewModel::stopMoving,
                    catSkin = selectedSkin
                )
            } else if (state !is GameUiState.GameOver) {
                // Initial state - start game
                GameScreen(
                    gameState = null,
                    onStartGame = { width, height ->
                        viewModel.startGame(width, height)
                    },
                    onMoveLeft = viewModel::moveLeft,
                    onMoveRight = viewModel::moveRight,
                    onStopMoving = viewModel::stopMoving,
                    catSkin = selectedSkin
                )
            }
        }

        composable(Screen.GameOver.route) {
            val gameOverState = uiState as? GameUiState.GameOver

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
