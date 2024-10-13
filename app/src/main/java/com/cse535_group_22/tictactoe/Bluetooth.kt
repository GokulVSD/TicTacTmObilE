package com.cse535_group_22.tictactoe

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import java.util.UUID

var bluetoothSocket: BluetoothSocket? = null

var serverSocket: BluetoothServerSocket? = null

val gameUUID: UUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66")

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
            try {
                if (bluetoothSocket?.isConnected == true) {
                    val bytesRead = inputStream?.read(buffer)
                    if (bytesRead != null && bytesRead > 0) {
                        val receivedMessage = String(buffer, 0, bytesRead)
                        gameViewModel.processJsonString(receivedMessage)
                    }
                }
            } catch (e: Exception) {
                gameViewModel.resetGame()
                gameViewModel.resetBluetooth()
            }
        }
    }.start()
}