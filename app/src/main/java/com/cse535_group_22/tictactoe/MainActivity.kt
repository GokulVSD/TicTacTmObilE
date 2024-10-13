package com.cse535_group_22.tictactoe

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModelProvider
import com.cse535_group_22.tictactoe.ui.theme.TicTacToeTheme

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: GameViewModel
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private val discoveredDevices = mutableStateListOf<BluetoothDevice>()

    private val requestBluetoothPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (!permissions[android.Manifest.permission.BLUETOOTH_SCAN]!! || !permissions[android.Manifest.permission.BLUETOOTH_CONNECT]!!) {
                Toast.makeText(baseContext, "Permissions denied", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

    private fun requestPermissions() {
        requestBluetoothPermissions.launch(
            arrayOf(
                android.Manifest.permission.BLUETOOTH_SCAN,
                android.Manifest.permission.BLUETOOTH_CONNECT,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    @SuppressLint("MissingPermission")
    private fun makeDiscoverable() {
        val discoverableIntent: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
        }
        startActivity(discoverableIntent)
    }

    @SuppressLint("MissingPermission")
    private fun startDiscovery() {
        if (bluetoothAdapter.isDiscovering) {
            bluetoothAdapter.cancelDiscovery()
            discoveredDevices.clear()
        }
        bluetoothAdapter.startDiscovery()
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (BluetoothDevice.ACTION_FOUND == action) {
                val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                discoveredDevices.add(device)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bluetoothManager = this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)

        val factory = GameViewModelFactory(applicationContext)
        viewModel = ViewModelProvider(this, factory)[GameViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            TicTacToeTheme {
                when (viewModel.currentScreen) {
                    Screens.GAME -> GameScreen(bluetoothAdapter, discoveredDevices,
                        { requestPermissions() }, { makeDiscoverable() }, { startDiscovery() })
                    Screens.SETTINGS -> SettingsScreen(bluetoothAdapter)
                    Screens.PAST_GAMES -> PastGamesScreen()
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}