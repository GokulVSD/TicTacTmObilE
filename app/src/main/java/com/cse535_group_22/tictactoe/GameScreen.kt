package com.cse535_group_22.tictactoe

import android.Manifest
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.UUID

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
    val MY_UUID: UUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66")

    val context = LocalContext.current
    val bluetoothAdapter: BluetoothAdapter? = (context.getSystemService(Context.BLUETOOTH_SERVICE) as? android.bluetooth.BluetoothManager)?.adapter

    var discoveredDevices by remember { mutableStateOf(emptyList<BluetoothDevice>()) }
    val bluetoothEnabled by remember { mutableStateOf(bluetoothAdapter?.isEnabled == true) }
    var hasBluetoothPermission by remember { mutableStateOf(false) }

    // Bluetooth permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            hasBluetoothPermission = permissions[Manifest.permission.BLUETOOTH_CONNECT] == true &&
                    permissions[Manifest.permission.BLUETOOTH_SCAN] == true
        }
    )

    // Check for permissions
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            hasBluetoothPermission = true
        } else {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN)
            )
        }
    }

    if (isOpen) {
        // Check and ask the user to enable Bluetooth if it's not already enabled
        if (!bluetoothEnabled) {
            EnableBluetooth(bluetoothAdapter)
        } else {
            if (hasBluetoothPermission) {
                // Discover devices
                discoverBluetoothDevices(context, bluetoothAdapter) { devices ->
                    discoveredDevices = devices
                }
            }
        }

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
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (hasBluetoothPermission) {
                        // Display discovered devices
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            discoveredDevices.forEach { device ->
                                Text(
                                    text = device.name ?: device.address,
                                    fontSize = 16.sp,
                                    modifier = Modifier.clickable {
                                        // Connect to the selected device
                                        connectToDevice(context, device, MY_UUID) { message ->
                                            // Show a message or handle connection result
                                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                            }
                        }
                    } else {
                        Text("Bluetooth permissions are required to display devices.")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

fun connectToDevice(
    context: Context,
    device: BluetoothDevice,
    uuid: UUID,
    onMessage: (String) -> Unit
) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
        onMessage("Bluetooth connect permission not granted")
        return
    }

    Thread {
        try {
            val socket = device.createRfcommSocketToServiceRecord(uuid)

            // Check if we still have the permission to connect before trying
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                socket.connect()
                onMessage("Connected to ${device.name}")

                // Send a message once connected
                val outputStream = socket.outputStream
                val message = "Hello"
                outputStream.write(message.toByteArray())
                outputStream.flush()

                onMessage("Message sent to ${device.name}")

                // Close the socket when done
                socket.close()
            } else {
                onMessage("Bluetooth connect permission lost during connection attempt")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            onMessage("Failed to connect: ${e.message}")
        }
    }.start()
}

// Receive data from another connected device
fun receiveDataFromBluetooth(context: Context, uuid: UUID, onMessage: (String) -> Unit) {
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
        onMessage("Bluetooth connect permission not granted")
        return
    }

    val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    if (bluetoothAdapter == null) {
        onMessage("Bluetooth not supported on this device")
        return
    }

    Thread {
        var serverSocket: BluetoothServerSocket? = null
        try {
            // Create a listening server socket
            serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("MyApp", uuid)
            onMessage("Waiting for incoming connections...")

            // Block until a connection is accepted
            val socket = serverSocket.accept()

            // Connection accepted, read data from the socket
            onMessage("Connection accepted from ${socket.remoteDevice.name}")

            val inputStream = socket.inputStream
            val buffer = ByteArray(1024)  // Buffer to store the incoming data
            var bytesRead: Int

            // Read data from the input stream
            while (true) {
                bytesRead = inputStream.read(buffer)
                val receivedMessage = String(buffer, 0, bytesRead)
                onMessage("Message received: $receivedMessage")

                // Close the socket after receiving the message
                socket.close()
                break
            }

        } catch (e: IOException) {
            e.printStackTrace()
            onMessage("Error: ${e.message}")
        } finally {
            serverSocket?.close()
        }
    }.start()
}



@Composable
fun EnableBluetooth(bluetoothAdapter: BluetoothAdapter?) {
    val context = LocalContext.current

    if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled) {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivityForResult(context as Activity, enableBtIntent, 1, null)
    }
}

fun discoverBluetoothDevices(context: Context, bluetoothAdapter: BluetoothAdapter?, onDevicesDiscovered: (List<BluetoothDevice>) -> Unit) {
    if (bluetoothAdapter == null) return

    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                // Discovery found a device
                val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                if (device != null) {
                    onDevicesDiscovered(listOf(device))
                }
            }
        }
    }

    val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
    context.registerReceiver(receiver, filter)

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.BLUETOOTH_CONNECT
        ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // Request the necessary permissions here
        return
    }

    bluetoothAdapter.startDiscovery()
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
    val scope = rememberCoroutineScope()
    var aiResult by remember { mutableStateOf(Pair(0, 0)) }

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
