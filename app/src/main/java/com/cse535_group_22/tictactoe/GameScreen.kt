package com.cse535_group_22.tictactoe

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.app.ActivityCompat.startActivityForResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

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
fun BluetoothModal(isOpen: Boolean, onDismiss: () -> Unit, bluetoothAdapter: BluetoothAdapter, devices: List<BluetoothDevice>) {
    val context = LocalContext.current
    val gameViewModel: GameViewModel = viewModel(factory = GameViewModelFactory(context))

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
                        text = "Connect to a device",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(480.dp)
                            .padding(16.dp)
                    ) {
                        items(devices) { device ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Box(
                                    modifier = Modifier.weight(1f)
                                        .border(
                                            BorderStroke(1.dp, Color(0xff5c5652)),
                                        ).clickable {
                                            Toast.makeText(
                                                context,
                                                "Connecting to lobby, please wait",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            startBluetoothConnection(device, {
                                                gameViewModel.opponentMACAddress = device.address
                                                gameViewModel.connected.value = true
                                                gameViewModel.statusKey++
                                                listenForResponses(gameViewModel)
                                                sendData(gameViewModel.getStateAsJsonString(resetGame = false, choosingPlayer = false))
                                                onDismiss()
                                            }, {
                                                Toast.makeText(
                                                    context,
                                                    "Failed to connect to lobby",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                onDismiss()
                                            })
                                        }
                                ) {
                                    Text(
                                        text = device.name ?: device.address,
                                        color = Color(0xff5c5652),
                                        modifier = Modifier.align(Alignment.Center).padding(8.dp),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}


@Composable
fun GameModeChooser(isOpen: Boolean, onDismiss: () -> Unit, bluetoothAdapter: BluetoothAdapter, requestPermissions: () -> Unit) {
    val context = LocalContext.current
    val gameViewModel: GameViewModel = viewModel(factory = GameViewModelFactory(context))

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
                                    requestPermissions()
                                    if (!bluetoothAdapter.isEnabled) {
                                        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                                        startActivityForResult(context as Activity, enableBtIntent, 1, null)
                                    }
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

@Composable
fun GameScreen(
    bluetoothAdapter: BluetoothAdapter,
    devices: List<BluetoothDevice>,
    requestPermissions: () -> Unit,
    makeDiscoverable: () -> Unit,
    startDiscovery: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var aiResult by remember { mutableStateOf(Pair(0, 0)) }

    val context = LocalContext.current
    val gameViewModel: GameViewModel = viewModel(factory = GameViewModelFactory(context))

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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = gameViewModel.getStatus(gameViewModel.moveCounter, gameViewModel.statusKey),
                color = Color(0xff5c5652),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }



        GameModeChooser(isOpen = isGameModeDialogOpen, onDismiss = { isGameModeDialogOpen = false }, bluetoothAdapter, requestPermissions)
        BluetoothModal(isOpen =  isBluetoothDialogOpen, onDismiss = { isBluetoothDialogOpen = false }, bluetoothAdapter, devices)
        Spacer(modifier = Modifier.height(16.dp))

        GameBoard(boardState = gameViewModel.boardState, playing = gameViewModel.playing.collectAsState().value, onClick = { row, col ->
            when (gameViewModel.vs) {
                VS.AI -> {
                    gameViewModel.makeNextMove('X', row, col)
                    if (gameViewModel.playing.value) {
                        scope.launch {
                            aiResult = withContext(Dispatchers.Default) {
                                getNextMoveFromAI(gameViewModel.getBoard(), gameViewModel.difficulty)
                            }
                            gameViewModel.makeNextMove('O', aiResult.first, aiResult.second)
                        }
                    }
                }
                VS.LOCAL -> {
                    gameViewModel.makeNextMove(gameViewModel.nextPlayer, row, col)
                }
                VS.BLUETOOTH -> {
                    if (gameViewModel.currentPlayerSymbol.value == gameViewModel.nextPlayer) {
                        gameViewModel.makeNextMove(gameViewModel.currentPlayerSymbol.value, row, col)
                        sendData(gameViewModel.getStateAsJsonString(resetGame = false, choosingPlayer = false))
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
            if(gameViewModel.vs == VS.BLUETOOTH && gameViewModel.currentPlayerSymbol.collectAsState().value == ' ') {
                if (!gameViewModel.connected.collectAsState().value) {
                    Box(
                        modifier = Modifier
                            .border(
                                BorderStroke(3.dp, Color(0xff5c5652)),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable {
                                makeDiscoverable()
                                gameViewModel.waitingForConnection.value = true
                                gameViewModel.statusKey++
                                acceptBluetoothConnection(bluetoothAdapter) {
                                    gameViewModel.waitingForConnection.value = false
                                    if (bluetoothSocket != null) {
                                        gameViewModel.opponentMACAddress = bluetoothSocket!!.remoteDevice.address
                                        gameViewModel.connected.value = true
                                        gameViewModel.statusKey++
                                        listenForResponses(gameViewModel)
                                        sendData(gameViewModel.getStateAsJsonString(resetGame = false, choosingPlayer = false))
                                    }
                                    gameViewModel.statusKey++
                                }
                            }
                    ) {
                        Text(
                            text = "Start Lobby",
                            color = Color(0xff5c5652),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    Box(
                        modifier = Modifier
                            .border(
                                BorderStroke(3.dp, Color(0xff5c5652)),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable {
                                serverSocket = null
                                startDiscovery()
                                isBluetoothDialogOpen = true
                            }
                    ) {
                        Text(
                            text = "Connect to Lobby",
                            color = Color(0xff5c5652),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(12.dp)
                        )
                    }
                } else if (gameViewModel.currentPlayerSymbol.collectAsState().value == ' ') {
                    Box(
                        modifier = Modifier
                            .border(
                                BorderStroke(3.dp, Color(0xff5c5652)),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable {
                                gameViewModel.currentPlayerSymbol.value = 'X'
                                gameViewModel.xMACAddress = gameViewModel.myMACAddress
                                gameViewModel.oMACAddress = gameViewModel.opponentMACAddress
                                scope.launch {
                                    withContext(Dispatchers.IO) {
                                        sendData(
                                            gameViewModel.getStateAsJsonString(
                                                resetGame = false,
                                                choosingPlayer = true
                                            )
                                        )
                                    }
                                }
                            }
                    ) {
                        Text(
                            text = "ME",
                            color = Color(0xff5c5652),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    Box(
                        modifier = Modifier
                            .border(
                                BorderStroke(3.dp, Color(0xff5c5652)),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable {
                                gameViewModel.currentPlayerSymbol.value = 'O'
                                gameViewModel.xMACAddress = gameViewModel.opponentMACAddress
                                gameViewModel.oMACAddress = gameViewModel.myMACAddress
                                scope.launch {
                                    withContext(Dispatchers.IO) {
                                        sendData(
                                            gameViewModel.getStateAsJsonString(
                                                resetGame = false,
                                                choosingPlayer = true
                                            )
                                        )
                                    }
                                }
                            }
                    ) {
                        Text(
                            text = "OPPONENT",
                            color = Color(0xff5c5652),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(12.dp)
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .border(
                            BorderStroke(4.dp, Color(0xff5c5652)),
                            shape = RoundedCornerShape(30.dp)
                        )
                        .clickable {
                            gameViewModel.resetGame()
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

}
