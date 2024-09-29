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

    var currentPlayerSymbol = ' '

    var nextPlayer = 'X'

    val playing = MutableStateFlow(false)

    val connected = MutableStateFlow(false)

    var moveCounter by mutableStateOf(0)

    var gameResult = "Tic Tac Toe"


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
        moveCounter = 0
    }


    fun resetGame() {
        resetBoard()
        currentPlayerSymbol = ' '
        playing.value = false
        connected.value = false
    }


    fun getStatus(counter: Int): String {
        when (vs) {
            VS.AI -> {
                if (playing.value) {
                    if (nextPlayer == 'X') {
                        return "Your turn ($nextPlayer)"
                    }
                    if (nextPlayer == 'O') {
                        return "AI is thinking..."
                    }
                }
                return gameResult
            }
            VS.LOCAL -> {
                println("Called")
                if (playing.value) {
                    println(nextPlayer)
                    if (nextPlayer == 'X') {
                        return "Player 1's turn ($nextPlayer)"
                    }
                    if (nextPlayer == 'O') {
                        return "Player 2's turn ($nextPlayer)"
                    }
                }
                return gameResult
            }
            VS.BLUETOOTH -> {
                if (!connected.value) {
                    return "Start or connect to a lobby"
                } else if (currentPlayerSymbol == ' ') {
                    return "Choose the first player"
                } else if (playing.value) {
                    if (currentPlayerSymbol == nextPlayer) {
                        return "Your turn ($nextPlayer)"
                    } else {
                        return "Waiting for other player..."
                    }
                }
                return gameResult
            }
        }
    }

    private fun evaluateGameEnded(): Boolean {
        var winner = ""
        var method = ""
        for (i in 0 until 3) {
            if (boardState[i].all { it == 'X' }) {
                winner = "X"
                method = "row"
            }
            if (boardState[i].all { it == 'O' }) {
                winner = "O"
                method = "row"
            }

            if (boardState.all { it[i] == 'X' }) {
                winner = "X"
                method = "column"
            }
            if (boardState.all { it[i] == 'O' }) {
                winner = "O"
                method = "column"
            }
        }

        if (boardState[0][0] == 'X' && boardState[1][1] == 'X' && boardState[2][2] == 'X') {
            winner = "X"
            method = "diagonal"
        }
        if (boardState[0][2] == 'X' && boardState[1][1] == 'X' && boardState[2][0] == 'X') {
            winner = "X"
            method = "diagonal"
        }
        if (boardState[0][0] == 'O' && boardState[1][1] == 'O' && boardState[2][2] == 'O') {
            winner = "O"
            method = "diagonal"
        }
        if (boardState[0][2] == 'O' && boardState[1][1] == 'O' && boardState[2][0] == 'O') {
            winner = "O"
            method = "diagonal"
        }

        if (winner != "") {
            winner = when (vs) {
                VS.AI -> if (winner == "X") "User" else "AI"
                VS.LOCAL -> if (winner == "X") "Player 1" else "Player 2"
                VS.BLUETOOTH -> if (winner == "X") "Player 1" else "Player 2"
            }
            // TODO: Store in past games
            gameResult = "$winner won by $method"
            return true
        }

        if (boardState.all { row -> row.all { it == 'X' || it == 'O' } }) {
            // TODO: Store in past games
            gameResult = "Draw"
            return true
        }

        // Game is still ongoing
        return false
    }


    fun makeNextMove(player: Char, row: Int, col: Int) {
        if (!playing.value || boardState[row][col] != ' ' || player != nextPlayer) {
            return
        }
        boardState[row][col] = nextPlayer
        moveCounter++

        if (evaluateGameEnded()) {
            playing.value = false
            return
        }

        nextPlayer = if (nextPlayer == 'X') 'O' else 'X'
    }
}