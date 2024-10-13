package com.cse535_group_22.tictactoe

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@SuppressLint("MissingPermission")
@Composable
fun SettingsScreen(bluetoothAdapter: BluetoothAdapter) {

    val gameViewModel: GameViewModel = viewModel(factory = GameViewModelFactory(LocalContext.current))

    Column(modifier = Modifier.fillMaxSize()) {

        Spacer(modifier = Modifier.height(192.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .border(
                        BorderStroke(
                            3.dp,
                            if (gameViewModel.difficulty == Difficulty.EASY) Color(0xff5c5652) else Color.LightGray
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { gameViewModel.difficulty = Difficulty.EASY }
            ) {
                Text(
                    text = Difficulty.EASY.displayName,
                    color = if (gameViewModel.difficulty == Difficulty.EASY) Color(0xff5c5652) else Color.LightGray,
                    fontSize = 32.sp,
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
                            if (gameViewModel.difficulty == Difficulty.MEDIUM) Color(0xff5c5652) else Color.LightGray
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { gameViewModel.difficulty = Difficulty.MEDIUM }
            ) {
                Text(
                    text = Difficulty.MEDIUM.displayName,
                    color = if (gameViewModel.difficulty == Difficulty.MEDIUM) Color(0xff5c5652) else Color.LightGray,
                    fontSize = 32.sp,
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
                            if (gameViewModel.difficulty == Difficulty.HARD) Color(0xff5c5652) else Color.LightGray
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { gameViewModel.difficulty = Difficulty.HARD }
            ) {
                Text(
                    text = Difficulty.HARD.displayName,
                    color = if (gameViewModel.difficulty == Difficulty.HARD) Color(0xff5c5652) else Color.LightGray,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(64.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "BT Device: ${bluetoothAdapter.name}",
                color = Color(0xff5c5652),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(250.dp))

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
                        gameViewModel.currentScreen = Screens.GAME
                    }
            ) {
                Icon(
                    painter = painterResource(R.drawable.back),
                    contentDescription = "Back",
                    tint = Color(0xff5c5652),
                    modifier = Modifier
                        .size(72.dp)
                )

            }
        }
    }

}
