package com.cse535_group_22.tictactoe

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun GameBoard(boardState: List<List<Char>>, playing: Boolean, onClick: (Int, Int) -> Unit) {
    Column {
        for (row in 0..2) {
            Row {
                for (col in 0..2) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(6.dp)
                            .aspectRatio(1f)
                            .background(Color.White)
                            .then(
                                if (playing) {
                                    Modifier.clickable { onClick(row, col) }
                                } else {
                                    Modifier
                                }
                            )
                    ) {
                        when (boardState[row][col]) {
                            'X' -> Image(painterResource(R.drawable.ic_x), contentDescription = "X")
                            'O' -> Image(painterResource(R.drawable.ic_o), contentDescription = "O")
                            else -> Image(painterResource(R.drawable.ic_empty), contentDescription = " ")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BluetoothModal(isOpen: Boolean, onDismiss: () -> Unit) {
    if (isOpen) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = MaterialTheme.shapes.large,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Connect Devices",
                        color = Color(0xff5c5652),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                }
            }
        }
    }
}


@Composable
fun GameModeChooser(isOpen: Boolean, onDismiss: () -> Unit) {
    val gameViewModel: GameViewModel = viewModel(factory = GameViewModelFactory(LocalContext.current))

    if (isOpen) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = MaterialTheme.shapes.large,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Game Mode",
                        color = Color(0xff5c5652),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .border(
                                    BorderStroke(
                                        3.dp,
                                        if (gameViewModel.vs == VS.AI) Color(
                                            0xff5c5652
                                        ) else Color.LightGray
                                    ),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    gameViewModel.vs = VS.AI
                                    gameViewModel.resetGame()
                                }
                        ) {
                            Text(
                                text = VS.AI.displayName,
                                color = if (gameViewModel.vs == VS.AI) Color(
                                    0xff5c5652
                                ) else Color.LightGray,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(12.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .border(
                                    BorderStroke(
                                        3.dp,
                                        if (gameViewModel.vs == VS.LOCAL) Color(
                                            0xff5c5652
                                        ) else Color.LightGray
                                    ),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    gameViewModel.vs = VS.LOCAL
                                    gameViewModel.resetGame()
                                }
                        ) {
                            Text(
                                text = VS.LOCAL.displayName,
                                color = if (gameViewModel.vs == VS.LOCAL) Color(
                                    0xff5c5652
                                ) else Color.LightGray,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(12.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .border(
                                    BorderStroke(
                                        3.dp,
                                        if (gameViewModel.vs == VS.BLUETOOTH) Color(
                                            0xff5c5652
                                        ) else Color.LightGray
                                    ),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    gameViewModel.vs = VS.BLUETOOTH
                                    gameViewModel.resetGame()
                                }
                        ) {
                            Text(
                                text = VS.BLUETOOTH.displayName,
                                color = if (gameViewModel.vs == VS.BLUETOOTH) Color(
                                    0xff5c5652
                                ) else Color.LightGray,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(12.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .border(
                                    BorderStroke(4.dp, Color(0xff5c5652)),
                                    shape = RoundedCornerShape(30.dp)
                                )
                                .clickable {
                                    onDismiss()
                                }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.back),
                                contentDescription = "Back",
                                tint = Color(0xff5c5652),
                                modifier = Modifier
                                    .size(48.dp)
                            )

                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun GameScreen() {

    val gameViewModel: GameViewModel = viewModel(factory = GameViewModelFactory(LocalContext.current))

    var isGameModeDialogOpen by remember { mutableStateOf(false) }
    var isBluetoothDialogOpen by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxSize()) {

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                painter = painterResource(R.drawable.settings),
                contentDescription = "Settings",
                tint = Color(0xff5c5652),
                modifier = Modifier
                    .size(48.dp)
                    .clickable { gameViewModel.currentScreen = Screens.SETTINGS }
            )

            Box(
                modifier = Modifier
                    .border(
                        BorderStroke(3.dp, Color(0xff5c5652)),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { isGameModeDialogOpen = true }
            ) {
                Text(
                    text = gameViewModel.vs.displayName,
                    color = Color(0xff5c5652),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(12.dp)
                )
            }

            Icon(
                painter = painterResource(R.drawable.leaderboard),
                contentDescription = "Leaderboard",
                tint = Color(0xff5c5652),
                modifier = Modifier
                    .size(48.dp)
                    .clickable {
                        gameViewModel.loadPastGames()
                        gameViewModel.currentScreen = Screens.PAST_GAMES
                    }
            )
        }
        if(gameViewModel.vs == VS.BLUETOOTH){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ){
                Box(
                    modifier = Modifier
                        .border(
                            BorderStroke(3.dp, Color(0xff5c5652)),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { isBluetoothDialogOpen = true }
                ) {
                    Text(
                        text = "Connect",
                        color = Color(0xff5c5652),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(12.dp)
                    )
                }
            }

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = gameViewModel.getStatus(gameViewModel.moveCounter),
                color = Color(0xff5c5652),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }



        GameModeChooser(isOpen = isGameModeDialogOpen, onDismiss = { isGameModeDialogOpen = false })
        BluetoothModal(isOpen =  isBluetoothDialogOpen, onDismiss = { isBluetoothDialogOpen = false })
        Spacer(modifier = Modifier.height(16.dp))

        GameBoard(boardState = gameViewModel.boardState, playing = gameViewModel.playing.collectAsState().value, onClick = { row, col ->
                when (gameViewModel.vs) {
                    VS.AI -> {
                        gameViewModel.makeNextMove('X', row, col)
                        val result = getNextMoveFromAI(gameViewModel.getBoard(), gameViewModel.difficulty)
                        gameViewModel.makeNextMove('O', result.first, result.second)
                    }
                    VS.LOCAL -> {
                        gameViewModel.makeNextMove(gameViewModel.nextPlayer, row, col)
                    }
                    VS.BLUETOOTH -> {
                        if (gameViewModel.currentPlayerSymbol == gameViewModel.nextPlayer) {
                            gameViewModel.makeNextMove(gameViewModel.currentPlayerSymbol, row, col)
                            // TODO: Add logic to transmit move to other player.
                        }
                    }
                }
            },
        )

        Spacer(modifier = Modifier.height(56.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .border(
                        BorderStroke(4.dp, Color(0xff5c5652)),
                        shape = RoundedCornerShape(30.dp)
                    )
                    .clickable {
                        gameViewModel.resetBoard()
                        gameViewModel.playing.value = true
                    }
            ) {
                Icon(
                    painter = if (gameViewModel.playing.collectAsState().value) painterResource(R.drawable.restart) else painterResource(R.drawable.play),
                    contentDescription = "Play/Restart",
                    tint = Color(0xff5c5652),
                    modifier = Modifier
                        .size(72.dp)
                )

            }
        }

    }

}
