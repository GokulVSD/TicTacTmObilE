package com.cse535_group_22.tictactoe

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class BoardViewModel: ViewModel() {
    val boardState = mutableStateListOf(
        mutableStateListOf(' ', ' ', ' '),
        mutableStateListOf(' ', ' ', ' '),
        mutableStateListOf(' ', ' ', ' ')
    )

    private var nextMove = 'X'

    fun onClick(row: Int, col: Int) {
        if (boardState[row][col] != ' ') {
            return
        }
        boardState[row][col] = nextMove
        nextMove = if (nextMove == 'X') 'O' else 'X'
    }
}