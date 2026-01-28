package com.example.catjump

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.catjump.di.AppContainer
import com.example.catjump.navigation.CatJumpNavGraph
import com.example.catjump.presentation.viewmodel.GameViewModel
import com.example.catjump.presentation.viewmodel.GameViewModelFactory
import com.example.catjump.ui.theme.CatJumpTheme

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {

    private lateinit var appContainer: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate started")

        try {
            // Initialize dependency container
            appContainer = AppContainer(applicationContext)
            Log.d(TAG, "AppContainer initialized")

            enableEdgeToEdge()
            Log.d(TAG, "EdgeToEdge enabled")

            setContent {
                Log.d(TAG, "setContent called")
                CatJumpTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color(0xFF1a237e)
                    ) {
                        val navController = rememberNavController()
                        Log.d(TAG, "NavController created")

                        val viewModel: GameViewModel = viewModel(
                            factory = GameViewModelFactory(
                                gameEngine = appContainer.gameEngine,
                                getHighScoreUseCase = appContainer.getHighScoreUseCase,
                                saveHighScoreUseCase = appContainer.saveHighScoreUseCase,
                                getSelectedSkinUseCase = appContainer.getSelectedSkinUseCase,
                                saveSelectedSkinUseCase = appContainer.saveSelectedSkinUseCase
                            )
                        )
                        Log.d(TAG, "ViewModel created")

                        CatJumpNavGraph(
                            navController = navController,
                            viewModel = viewModel
                        )
                    }
                }
            }
            Log.d(TAG, "onCreate completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate", e)
        }
    }
}
