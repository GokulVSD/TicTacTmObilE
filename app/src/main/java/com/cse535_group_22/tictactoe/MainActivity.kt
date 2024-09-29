package com.cse535_group_22.tictactoe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.cse535_group_22.tictactoe.ui.theme.TicTacToeTheme

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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