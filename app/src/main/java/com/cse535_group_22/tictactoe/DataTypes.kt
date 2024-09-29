package com.cse535_group_22.tictactoe

enum class VS(val displayName: String) {
    AI("AI"),
    LOCAL("Local Player"),
    BLUETOOTH("Regular User"),
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