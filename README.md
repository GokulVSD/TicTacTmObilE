# TicTacTmObilE

## Overview

The goal of this group project is to create a Tic-Tac-Toe game on Android that allows an AI to play against a human opponent. The primary goal is to create a game where the computer plays against a human player using the Minimax algorithm with alpha-beta pruning. In addition to this, the app supports player vs local player gameplay, where each player takes turns. Finally, the app also supports player vs Bluetooth player, where by two devices which have the app installed can play via a Bluetooth connection. The app keeps track of past games, and allows you to customize the difficulty of the AI. It uses Jetpack Compose components for the UI, Room for storage of past games, and interfaces with Android's Bluetooth APIs.

## Features:

1. **Human vs AI**: The user can play against the AI with three difficulty modes (Easy, Medium, Hard).
2. **Minimax Algorithm with Alpha-Beta Pruning**: The AI’s moves are determined using this algorithm to optimize performance and reduce computation time.
3. **Game History**: Store the results of past games, including the date, winner, and difficulty mode.
4. **Difficulty Modes**:
   - Easy: AI takes random actions.
   - Medium: AI takes random actions 50% of the time and optimal actions the other 50%.
   - Hard: AI plays optimally in every turn.
5. **Game Modes**:
   - Player vs AI
   - Player vs Local Player
   - Player vs Bluetooth Player

## Hardware Requirements:

- **Android device running Android 13 or greater for playing.**
- **Two devices are needed for Bluetooth gameplay, the devices need to have Bluetooth support.**

- **Computer**:
  - For Android development and compilation: Ubuntu, Windows, or Mac versions that support Android Studio Koala.
  - Android Studio Koala was used to develop the app.
- **USB Cable**: To connect your phone to the computer unless you're using an emulator.


## Setup & Usage Instructions:

1. Clone the repository using Git:
   ```bash
   git clone https://github.com/GokulVSD/TicTacTmObilE
   ```
2. Install Android Studio Koala and necessary dependencies.
3. Open the repository using Android Studio.
4. Connect your device with USB debugging enabled or set up an emulator (emulator can only test single player).
5. Run the project using the "Run" option in your IDE, this should install the app on the device/emulator.
6. To start the game, click the play button.
7. You can reset any time.
8. Switch gamemodes using the button in the top middle.
9. The settings screen accessible by clicking the settings cog, will show you your Bluetooth device name, and the ability to switch difficulty, which can be done at any time.
10. The past games screen keeps track of past games that have completed.
11. To play Player vs Local Player, players take turns on the same device until the game ends.
12. For player vs Bluetooth Player, one of the devices needs to create a lobby, subsequently, within 300 seconds, the other device needs to join the lobby by clicking on the Bluetooth device name of the lobby owner.


## Software Requirements:


### Android API:

- Android min SDK: 33
- Android compile SDK: 34

### Dependencies

- agp = "8.6.0"
- hiltAndroid = "2.51.1"
- hiltAndroidCompiler = "2.51.1"
- kotlin = "1.9.0"
- coreKtx = "1.13.1"
- junit = "4.13.2"
- junitVersion = "1.2.1"
- espressoCore = "3.6.1"
- lifecycleRuntimeKtx = "2.8.5"
- activityCompose = "1.9.2"
- composeBom = "2024.04.01"
- lifecycleViewmodelCompose = "2.8.6"
- roomCompiler = "2.6.1"
- roomKtx = "2.6.1"
- roomRuntime = "2.6.1"
- lifecycleViewmodelAndroid = "2.8.6"
- daggerHilt = "2.42"
