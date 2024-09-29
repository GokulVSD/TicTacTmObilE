package com.cse535_group_22.tictactoe


fun getNextMoveFromAI(board: List<List<Char>>, difficulty: Difficulty): Pair<Int, Int> {
    // TODO: Modify this, for now it just returns first empty position.
    for (row in 0..2) {
        for (col in 0..2) {
            if (board[row][col] == ' ') {
                return Pair(row, col)
            }
        }
    }
    return Pair(0, 0)
}