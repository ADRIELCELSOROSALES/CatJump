package com.example.catjump.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.catjump.domain.model.CatSkin
import com.example.catjump.domain.model.CatSkins
import com.example.catjump.domain.model.ControlMode
import com.example.catjump.domain.model.GameSettings
import com.example.catjump.domain.model.GameState
import com.example.catjump.domain.model.SoundEvent
import com.example.catjump.domain.usecase.GetHighScoreUseCase
import com.example.catjump.domain.usecase.GetSelectedSkinUseCase
import com.example.catjump.domain.usecase.GetSettingsUseCase
import com.example.catjump.domain.usecase.SaveControlModeUseCase
import com.example.catjump.domain.usecase.SaveHighScoreUseCase
import com.example.catjump.domain.usecase.SaveSelectedSkinUseCase
import com.example.catjump.domain.usecase.SaveSoundEnabledUseCase
import com.example.catjump.game.GameEngine
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Estado de navegación de alto nivel (cambia pocas veces). Los datos por-frame del
 * juego viven en [GameViewModel.gameState] (snapshot state) para que solo el Canvas
 * se redibuje en cada tick, sin recomponer todo el árbol.
 */
sealed class GameUiState {
    data object Menu : GameUiState()
    data object Playing : GameUiState()
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
    private val saveSelectedSkinUseCase: SaveSelectedSkinUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val saveSoundEnabledUseCase: SaveSoundEnabledUseCase,
    private val saveControlModeUseCase: SaveControlModeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<GameUiState>(GameUiState.Menu)
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val _highScore = MutableStateFlow(0)
    val highScore: StateFlow<Int> = _highScore.asStateFlow()

    private val _selectedSkin = MutableStateFlow<CatSkin>(CatSkins.ORANGE)
    val selectedSkin: StateFlow<CatSkin> = _selectedSkin.asStateFlow()

    private val _settings = MutableStateFlow(GameSettings())
    val settings: StateFlow<GameSettings> = _settings.asStateFlow()

    /** Estado del juego por-frame. Se lee de forma diferida dentro del Canvas. */
    var gameState by mutableStateOf<GameState?>(null)
        private set

    /** Pausa (manual o automática al mandar la app a segundo plano). */
    var isPaused by mutableStateOf(false)
        private set

    /** Eventos de sonido desacoplados del render (no disparan recomposición). */
    private val _soundEvents = MutableSharedFlow<SoundEvent>(extraBufferCapacity = 16)
    val soundEvents: SharedFlow<SoundEvent> = _soundEvents.asSharedFlow()

    private var isRunning = false

    init {
        loadHighScore()
        loadSelectedSkin()
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            getSettingsUseCase().collect { settings ->
                _settings.value = settings
            }
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch { saveSoundEnabledUseCase(enabled) }
    }

    fun setControlMode(mode: ControlMode) {
        viewModelScope.launch { saveControlModeUseCase(mode) }
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
        if (isRunning) return
        if (screenWidth <= 0f || screenHeight <= 0f) return

        viewModelScope.launch {
            val highScore = getHighScoreUseCase().first()
            gameEngine.setMoveDirection(0)
            gameState = gameEngine.initializeGame(screenWidth, screenHeight, highScore)
            isPaused = false
            isRunning = true
            _uiState.value = GameUiState.Playing
        }
    }

    /**
     * Avanza un frame. Lo invoca la UI desde el reloj de frames (withFrameNanos),
     * por lo que corre en el hilo principal, en sincronía con el refresh de pantalla.
     *
     * @param nowMs reloj monotónico en ms (frameTimeNanos / 1_000_000).
     * @param deltaFrames tiempo del frame en unidades de frame de referencia (1.0 = 12 ms).
     */
    fun tick(nowMs: Long, deltaFrames: Float) {
        if (!isRunning || isPaused) return
        val state = gameState ?: return
        if (state.isGameOver) return

        val newState = gameEngine.update(state, nowMs, deltaFrames)
        gameState = newState

        // Emitir eventos de sonido de este frame
        newState.soundEvents.forEach { _soundEvents.tryEmit(it) }

        if (newState.isGameOver) {
            isRunning = false
            handleGameOver(newState)
        }
    }

    private fun handleGameOver(state: GameState) {
        viewModelScope.launch {
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
    }

    fun pause() {
        if (isRunning) isPaused = true
    }

    fun resume() {
        if (isRunning) isPaused = false
    }

    fun togglePause() {
        if (isRunning) isPaused = !isPaused
    }

    fun moveLeft() {
        if (!isPaused) gameEngine.setMoveDirection(-1)
    }

    fun moveRight() {
        if (!isPaused) gameEngine.setMoveDirection(1)
    }

    fun stopMoving() {
        gameEngine.setMoveDirection(0)
    }

    fun goToMenu() {
        resetGame()
        _uiState.value = GameUiState.Menu
    }

    fun prepareForRestart() {
        resetGame()
        _uiState.value = GameUiState.Menu // El GameScreen arrancará una partida nueva
    }

    fun restartGame(screenWidth: Float, screenHeight: Float) {
        resetGame()
        startGame(screenWidth, screenHeight)
    }

    private fun resetGame() {
        isRunning = false
        isPaused = false
        gameState = null
        gameEngine.setMoveDirection(0)
    }

    override fun onCleared() {
        super.onCleared()
        isRunning = false
    }
}

class GameViewModelFactory(
    private val gameEngine: GameEngine,
    private val getHighScoreUseCase: GetHighScoreUseCase,
    private val saveHighScoreUseCase: SaveHighScoreUseCase,
    private val getSelectedSkinUseCase: GetSelectedSkinUseCase,
    private val saveSelectedSkinUseCase: SaveSelectedSkinUseCase,
    private val getSettingsUseCase: GetSettingsUseCase,
    private val saveSoundEnabledUseCase: SaveSoundEnabledUseCase,
    private val saveControlModeUseCase: SaveControlModeUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            return GameViewModel(
                gameEngine = gameEngine,
                getHighScoreUseCase = getHighScoreUseCase,
                saveHighScoreUseCase = saveHighScoreUseCase,
                getSelectedSkinUseCase = getSelectedSkinUseCase,
                saveSelectedSkinUseCase = saveSelectedSkinUseCase,
                getSettingsUseCase = getSettingsUseCase,
                saveSoundEnabledUseCase = saveSoundEnabledUseCase,
                saveControlModeUseCase = saveControlModeUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
