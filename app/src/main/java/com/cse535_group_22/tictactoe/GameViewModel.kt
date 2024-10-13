package com.cse535_group_22.tictactoe

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class GameViewModel(context: Context) : ViewModel() {

    var currentScreen by mutableStateOf(Screens.GAME)

    var vs by mutableStateOf(VS.AI)

    var difficulty by mutableStateOf(Difficulty.MEDIUM)

    val boardState = mutableStateListOf(
        mutableStateListOf(' ', ' ', ' '),
        mutableStateListOf(' ', ' ', ' '),
        mutableStateListOf(' ', ' ', ' ')
    )

    var currentPlayerSymbol = MutableStateFlow(' ')

    var nextPlayer = 'X'

    val playing = MutableStateFlow(false)

    val waitingForConnection = MutableStateFlow(false)

    val connected = MutableStateFlow(false)

    var moveCounter by mutableStateOf(0)

    var statusKey by mutableStateOf(0)

    var gameResult = "Tic Tac Toe"

    val pastGamesDao = DatabaseBuilder.getInstance(context).pastGamesDao()

    val pastGames = MutableStateFlow<List<PastGame>>(emptyList())

    // Needed for BT JSON only
    var myMACAddress = ""
    var opponentMACAddress = ""
    var xMACAddress = ""
    var oMACAddress = ""
    var draw = false
    var winnerMAC = " "


    fun getBoard(): List<List<Char>> {
        return boardState.map { boardRow -> boardRow.toList() }
    }

    fun listToJSONArray(charList: List<List<Char>>): JSONArray {
        val jsonArray = JSONArray()

        for (innerList in charList) {
            val innerJsonArray = JSONArray()
            for (char in innerList) {
                innerJsonArray.put(char.toString())
            }
            jsonArray.put(innerJsonArray)
        }

        return jsonArray
    }


    private fun resetBoard(
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
        playing.value = false
        statusKey = 0
        draw = false
        winnerMAC = " "

        if (vs != VS.BLUETOOTH) {
            myMACAddress = ""
            opponentMACAddress = ""
            xMACAddress = ""
            oMACAddress = ""
            currentPlayerSymbol.value = ' '
            connected.value = false
            waitingForConnection.value = false
        } else {
            sendData(getStateAsJsonString(resetGame = true, choosingPlayer = false))
        }
    }

    fun loadPastGames() {
        viewModelScope.launch {
            pastGames.value = pastGamesDao.getPastGames()
        }
    }

    fun insertGame(winner: String) {
        var displayDifficulty = if (vs == VS.AI) difficulty.displayName else "PvP"
        val game = PastGame(dateTime = getCurrentDateTime(), winner = winner, difficulty = displayDifficulty)
        viewModelScope.launch {
            pastGamesDao.insertGame(game)
        }
    }

    fun getCurrentDateTime(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return current.format(formatter)
    }


    fun getStatus(counter: Int, key: Int): String {
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
                    if (waitingForConnection.value) {
                        return "Waiting for player to join"
                    } else {
                        return "Start or connect to a lobby"
                    }
                } else if (currentPlayerSymbol.value == ' ') {
                    return "Who goes first"
                } else if (playing.value) {
                    if (currentPlayerSymbol.value == nextPlayer) {
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
                VS.BLUETOOTH -> if (winner == currentPlayerSymbol.value.toString()) "You" else "Opponent"
            }
            if (winner == "You") {
                winnerMAC = myMACAddress
            } else {
                winnerMAC = opponentMACAddress
            }
            insertGame(winner)
            gameResult = "$winner won by $method"
            return true
        }

        if (boardState.all { row -> row.all { it == 'X' || it == 'O' } }) {
            insertGame("Draw")
            gameResult = "Draw"
            draw = true
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


    fun getStateAsJsonString(resetGame: Boolean, choosingPlayer: Boolean): String {
        val jsonMessage = JSONObject()

        val gameState = JSONObject()

        gameState.put("board", listToJSONArray(getBoard()))
        gameState.put("turn", moveCounter)
        gameState.put("winner", winnerMAC)
        gameState.put("draw", draw)
        gameState.put("connectionEstablished", connected.value && myMACAddress != "")
        gameState.put("reset", resetGame)

        jsonMessage.put("gameState", gameState)

        val metadata = JSONObject()

        val choice1 = JSONObject()
        choice1.put("id", "player1")
        choice1.put("name", myMACAddress)

        val choice2 = JSONObject()
        choice2.put("id", "player2")
        choice2.put("name", opponentMACAddress)

        metadata.put("choices", JSONArray(arrayOf(choice1, choice2)))

        if (myMACAddress != "") {
            val miniGame = JSONObject()

            miniGame.put("player1Choice", xMACAddress)
            if (!choosingPlayer) {
                miniGame.put("player2Choice", xMACAddress)
            }

            metadata.put("miniGame", miniGame)
        }

        jsonMessage.put("metadata", metadata)

        return jsonMessage.toString()
    }

    fun processJsonString(response: String) {
        val jsonMessage = JSONObject(response)
        val gameState = jsonMessage.getJSONObject("gameState")
        val metadata = jsonMessage.getJSONObject("metadata")

        if (!playing.value) {
            if (!gameState.getBoolean("connectionEstablished")) {
                val choices = metadata.getJSONArray("choices")
                myMACAddress = choices.getJSONObject(1).getString("name")
                return
            }
            val miniGame = metadata.getJSONObject("miniGame")
            xMACAddress =  miniGame.getString("player1Choice")
            if (myMACAddress == xMACAddress) {
                oMACAddress = opponentMACAddress
                currentPlayerSymbol.value = 'X'
            } else {
                oMACAddress = myMACAddress
                currentPlayerSymbol.value = 'O'
            }
            if (!miniGame.has("player2Choice")) {
                sendData(getStateAsJsonString(resetGame = false, choosingPlayer = false))
            }
            playing.value = true
        } else if (gameState.getBoolean("reset")) {
            resetGame()
            playing.value = true
        } else {
            val boardRows = gameState.getJSONArray("board")
            for (row in 0..2) {
                val boardRow = boardRows.getJSONArray(row)
                for (col in 0..2) {
                    if (boardState[row][col].toString() != boardRow[col]) {
                        makeNextMove(nextPlayer, row, col)
                    }
                }
            }
        }
    }
}