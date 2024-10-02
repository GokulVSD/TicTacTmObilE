package com.cse535_group_22.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cse535_group_22.tictactoe.ui.theme.TicTacToeTheme

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: GameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factory = GameViewModelFactory(applicationContext)
        viewModel = ViewModelProvider(this, factory)[GameViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            TicTacToeTheme {
                when (viewModel.currentScreen) {
                    Screens.GAME -> GameScreen()
                    Screens.SETTINGS -> SettingsScreen()
                    Screens.PAST_GAMES -> PastGamesScreen()
                }
            }
        }
    }
}