package com.cse535_group_22.tictactoe

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class GameViewModel() : ViewModel() {

    var currentScreen by mutableStateOf(Screens.GAME)

    var vs by mutableStateOf(VS.AI)

    var difficulty by mutableStateOf(Difficulty.MEDIUM)

    val boardState = mutableStateListOf(
        mutableStateListOf(' ', ' ', ' '),
        mutableStateListOf(' ', ' ', ' '),
        mutableStateListOf(' ', ' ', ' ')
    )

    var currentPlayerSymbol = 'X'

    var nextPlayer = 'X'

    val playing = MutableStateFlow(false)


    fun getBoard(): List<List<Char>> {
        return boardState.map { boardRow -> boardRow.toList() }
    }


    fun resetBoard(
        board: List<List<Char>> = List(3) { List(3) { ' ' } }
    ) {
        for (row in 0..2) {
            for (col in 0..2) {
                boardState[row][col] = board[row][col]
            }
        }
        nextPlayer = 'X'
    }


    fun makeNextMove(player: Char, row: Int, col: Int) {
        if (!playing.value || boardState[row][col] != ' ' || player != nextPlayer) {
            return
        }
        boardState[row][col] = nextPlayer

        nextPlayer = if (nextPlayer == 'X') 'O' else 'X'
    }
}