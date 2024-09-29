package com.cse535_group_22.tictactoe

enum class VS(val displayName: String) {
    AI("Player vs AI"),
    LOCAL("Player vs Local Player"),
    BLUETOOTH("Player vs Bluetooth Player"),
}

enum class Difficulty(val displayName: String) {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard"),
}

enum class Screens(val displayName: String) {
    GAME("Game"),
    SETTINGS("Settings"),
    PAST_GAMES("Past Games"),
}