package com.cse535_group_22.tictactoe

fun hasPlayerWon(board: List<List<Char>>, player: Char): Boolean {
    for (i in 0..2) {
        if ((board[i][0] == player && board[i][1] == player && board[i][2] == player) || // R
            (board[0][i] == player && board[1][i] == player && board[2][i] == player)) { // C
            return true
        }
    }
    return (board[0][0] == player && board[1][1] == player && board[2][2] == player) || // \
            (board[0][2] == player && board[1][1] == player && board[2][0] == player)   // /
}

// Check draw
fun isBoardFull(board: List<List<Char>>): Boolean {
    for (row in board) {
        if (row.contains(' ')) return false
    }
    return true
}

// MINIMAX
// player = 'X'  AI = 'O'
fun minimax(board: List<List<Char>>, depth: Int, isMaximizing: Boolean): Int {
    if (hasPlayerWon(board, 'O')) return 10 - depth // AI wins
    if (hasPlayerWon(board, 'X')) return depth - 10 // Player wins
    if (isBoardFull(board)) return 0 // Draw

//    MAX when AI
    return if (isMaximizing) {
        var bestScore = Int.MIN_VALUE
        for (row in 0..2) {
            for (col in 0..2) {
                if (board[row][col] == ' ') {
                    val newBoard = board.map { it.toMutableList() }
                    newBoard[row][col] = 'O' // AI move
                    val score = minimax(newBoard, depth + 1, false)
                    bestScore = maxOf(bestScore, score)
                }
            }
        }
        bestScore
    }
//    MIN when Player
    else {
        var bestScore = Int.MAX_VALUE
        for (row in 0..2) {
            for (col in 0..2) {
                if (board[row][col] == ' ') {
                    val newBoard = board.map { it.toMutableList() } // Create a mutable copy of the board
                    newBoard[row][col] = 'X' // Player move
                    val score = minimax(newBoard, depth + 1, true)
                    bestScore = minOf(bestScore, score)
                }
            }
        }
        bestScore
    }
}

// Get the next move for the AI using Minimax
fun getNextMoveFromAI(board: List<List<Char>>, difficulty: Difficulty): Pair<Int, Int> {
    var bestScore = Int.MIN_VALUE
    var bestMove = Pair(-1, -1)

    for (row in 0..2) {
        for (col in 0..2) {
            if (board[row][col] == ' ') {
                val newBoard = board.map { it.toMutableList() } //copy of board
                newBoard[row][col] = 'O' // AI move
                val score = minimax(newBoard, 0, false)
                if (score > bestScore) {
                    bestScore = score
                    bestMove = Pair(row, col)
                }
            }
        }
    }

    return bestMove
}