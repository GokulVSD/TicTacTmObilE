package com.cse535_group_22.tictactoe

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.content.Context
import android.widget.Toast
import java.io.IOException
import java.util.UUID

@SuppressLint("MissingPermission")
fun connectToDevice(context: Context, device: BluetoothDevice) {
    val gameUUID: UUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66")
    Thread {
        try {
            val socket = device.createRfcommSocketToServiceRecord(gameUUID)
            socket.connect()
            Toast.makeText(context, "Connected to ${device.name}", Toast.LENGTH_SHORT).show()

            // Send a message once connected
            val outputStream = socket.outputStream
            val message = "Hello"
            outputStream.write(message.toByteArray())
            outputStream.flush()

            Toast.makeText(context, "Message sent to ${device.name}", Toast.LENGTH_SHORT).show()

            // Close the socket when done
            socket.close()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Failed to connect: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }.start()
}


// Receive data from another connected device
fun receiveDataFromBluetooth(onMessage: (String) -> Unit) {
    val gameUUID: UUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66")

    val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    if (bluetoothAdapter == null) {
        onMessage("Bluetooth not supported on this device")
        return
    }

    Thread {
        var serverSocket: BluetoothServerSocket? = null
        try {
            // Create a listening server socket
            serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("Tic-Tac-Toe", gameUUID)
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