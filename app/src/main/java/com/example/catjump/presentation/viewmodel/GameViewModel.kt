package com.example.catjump.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.catjump.domain.model.CatSkin
import com.example.catjump.domain.model.CatSkins
import com.example.catjump.domain.model.GameState
import com.example.catjump.domain.usecase.GetHighScoreUseCase
import com.example.catjump.domain.usecase.GetSelectedSkinUseCase
import com.example.catjump.domain.usecase.SaveHighScoreUseCase
import com.example.catjump.domain.usecase.SaveSelectedSkinUseCase
import com.example.catjump.game.GameConstants
import com.example.catjump.game.GameEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "GameViewModel"

sealed class GameUiState {
    data object Menu : GameUiState()
    data class Playing(val gameState: GameState) : GameUiState()
    data class GameOver(
        val score: Int,
        val highScore: Int,
        val isNewHighScore: Boolean
    ) : GameUiState()
}

class GameViewModel(
    private val gameEngine: GameEngine,
    private val getHighScoreUseCase: GetHighScoreUseCase,
    private val saveHighScoreUseCase: SaveHighScoreUseCase,
    private val getSelectedSkinUseCase: GetSelectedSkinUseCase,
    private val saveSelectedSkinUseCase: SaveSelectedSkinUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<GameUiState>(GameUiState.Menu)
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val _highScore = MutableStateFlow(0)
    val highScore: StateFlow<Int> = _highScore.asStateFlow()

    private val _selectedSkin = MutableStateFlow<CatSkin>(CatSkins.ORANGE)
    val selectedSkin: StateFlow<CatSkin> = _selectedSkin.asStateFlow()

    private var gameLoopJob: Job? = null
    private var currentGameState: GameState? = null

    init {
        loadHighScore()
        loadSelectedSkin()
    }

    private fun loadHighScore() {
        viewModelScope.launch {
            getHighScoreUseCase().collect { score ->
                _highScore.value = score
            }
        }
    }

    private fun loadSelectedSkin() {
        viewModelScope.launch {
            getSelectedSkinUseCase().collect { skin ->
                _selectedSkin.value = skin
            }
        }
    }

    fun selectSkin(skin: CatSkin) {
        viewModelScope.launch {
            saveSelectedSkinUseCase(skin.id)
            _selectedSkin.value = skin
        }
    }

    fun startGame(screenWidth: Float, screenHeight: Float) {
        Log.d(TAG, "startGame called: width=$screenWidth, height=$screenHeight")

        // Prevent multiple starts
        if (_uiState.value is GameUiState.Playing) {
            Log.d(TAG, "Already playing, ignoring startGame")
            return
        }
        if (gameLoopJob?.isActive == true) {
            Log.d(TAG, "Game loop already active, ignoring startGame")
            return
        }

        viewModelScope.launch {
            try {
                Log.d(TAG, "Initializing game...")
                val highScore = getHighScoreUseCase().first()
                Log.d(TAG, "High score loaded: $highScore")

                currentGameState = gameEngine.initializeGame(screenWidth, screenHeight, highScore)
                Log.d(TAG, "Game initialized, platforms: ${currentGameState?.platforms?.size}")

                _uiState.value = GameUiState.Playing(currentGameState!!)
                startGameLoop()
            } catch (e: Exception) {
                Log.e(TAG, "Error starting game", e)
            }
        }
    }

    private fun startGameLoop() {
        Log.d(TAG, "Starting game loop")
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            try {
                var frameCount = 0
                while (true) {
                    val state = currentGameState ?: break

                    if (state.isGameOver) {
                        Log.d(TAG, "Game over detected")
                        handleGameOver(state)
                        break
                    }

                    // Update game state on Default dispatcher
                    val newState = withContext(Dispatchers.Default) {
                        gameEngine.update(state)
                    }
                    currentGameState = newState
                    _uiState.value = GameUiState.Playing(newState)

                    frameCount++
                    if (frameCount % 60 == 0) {
                        Log.d(TAG, "Frame $frameCount, score: ${newState.score}, cat y: ${newState.cat.y}")
                    }

                    delay(GameConstants.FRAME_TIME_MS)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in game loop", e)
            }
        }
    }

    private suspend fun handleGameOver(state: GameState) {
        if (state.isNewHighScore) {
            saveHighScoreUseCase(state.score)
            _highScore.value = state.score
        }

        _uiState.value = GameUiState.GameOver(
            score = state.score,
            highScore = if (state.isNewHighScore) state.score else state.highScore,
            isNewHighScore = state.isNewHighScore
        )
    }

    fun moveLeft() {
        gameEngine.setMoveDirection(-1)
    }

    fun moveRight() {
        gameEngine.setMoveDirection(1)
    }

    fun stopMoving() {
        gameEngine.setMoveDirection(0)
    }

    fun goToMenu() {
        gameLoopJob?.cancel()
        currentGameState = null
        _uiState.value = GameUiState.Menu
    }

    fun prepareForRestart() {
        Log.d(TAG, "Preparing for restart")
        gameLoopJob?.cancel()
        currentGameState = null
        _uiState.value = GameUiState.Menu // Reset to Menu so Game screen will start fresh
    }

    fun restartGame(screenWidth: Float, screenHeight: Float) {
        gameLoopJob?.cancel()
        startGame(screenWidth, screenHeight)
    }

    override fun onCleared() {
        super.onCleared()
        gameLoopJob?.cancel()
    }
}

class GameViewModelFactory(
    private val gameEngine: GameEngine,
    private val getHighScoreUseCase: GetHighScoreUseCase,
    private val saveHighScoreUseCase: SaveHighScoreUseCase,
    private val getSelectedSkinUseCase: GetSelectedSkinUseCase,
    private val saveSelectedSkinUseCase: SaveSelectedSkinUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            return GameViewModel(
                gameEngine = gameEngine,
                getHighScoreUseCase = getHighScoreUseCase,
                saveHighScoreUseCase = saveHighScoreUseCase,
                getSelectedSkinUseCase = getSelectedSkinUseCase,
                saveSelectedSkinUseCase = saveSelectedSkinUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
