package com.cse535_group_22.tictactoe

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.UUID

var bluetoothSocket: BluetoothSocket? = null

var serverSocket: BluetoothServerSocket? = null

val gameUUID: UUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66")

var receivedMessage by mutableStateOf("Nothing yet")

@SuppressLint("MissingPermission")
fun startBluetoothConnection(device: BluetoothDevice, successCallback: () -> Unit, failCallback: () -> Unit) {
    bluetoothSocket = device.createRfcommSocketToServiceRecord(gameUUID)

    try {
        Thread {
            bluetoothSocket?.connect()
            successCallback()
        }.start()
    } catch (e: Exception) {
        e.printStackTrace()
        failCallback()
    }
}


@SuppressLint("MissingPermission")
fun acceptBluetoothConnection(bluetoothAdapter: BluetoothAdapter, doneCallback: () -> Unit) {
    serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("Tic-Tac-Toe", gameUUID)
    try {
        Thread {
            // Blocking to accept connection, will unblock on serverSocket.close()
            bluetoothSocket = serverSocket?.accept()
            doneCallback()
        }.start()
    } catch (e: Exception) {
        e.printStackTrace()
        doneCallback()
    }
}


fun sendData(dataToSend: String) {
    bluetoothSocket?.outputStream?.write(dataToSend.toByteArray())
    bluetoothSocket?.outputStream?.flush()
}

fun listenForResponses(gameViewModel: GameViewModel) {
    Thread {
        val inputStream = bluetoothSocket?.inputStream
        val buffer = ByteArray(1024)

        while (true) {
            val bytesRead = inputStream?.read(buffer)
            if (bytesRead != null && bytesRead > 0) {
                val receivedMessage = String(buffer, 0, bytesRead)
                gameViewModel.processJsonString(receivedMessage)
            }
        }
    }.start()
}










//private fun manageConnectedSocket(socket: BluetoothSocket) {
//    val inputStream = socket.inputStream
//    val reader = BufferedReader(InputStreamReader(inputStream))
//
//    while (true) {
//        val receivedData = reader.readLine()
//        if (receivedData != null) {
//            receivedMessage = receivedData
//        }
//    }
//}
//
//fun sendData(message: String): Boolean {
//    try {
//        if (bluetoothSocket?.isConnected == true) {  // Ensure the socket is connected
//            bluetoothSocket?.outputStream?.let { outputStream ->
//                outputStream.write("$message\n".toByteArray())
//                outputStream.flush()
//            }
//            return true
//        } else {
//            return false
//        }
//    } catch (e: Exception) {
//        e.printStackTrace()
//        return false
//    }
//}