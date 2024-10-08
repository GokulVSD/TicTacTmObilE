package com.cse535_group_22.tictactoe

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TableCell(content: String, modifier: Modifier) {
    Box(
        modifier = modifier
            .border(
                BorderStroke(1.dp, Color(0xff5c5652)),
            )
    ) {
        Text(
            text = content,
            color = Color(0xff5c5652),
            modifier = Modifier.align(Alignment.Center).padding(8.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun PastGames(gameViewModel: GameViewModel) {
    val pastGames by gameViewModel.pastGames.collectAsState()

    Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        TableCell(content = "Date Time", modifier = Modifier.weight(1.5f))
        TableCell(content = "Winner", modifier = Modifier.weight(1f))
        TableCell(content = "Difficulty", modifier = Modifier.weight(1f))
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(480.dp)
            .padding(16.dp)
    ) {
        items(pastGames) { game ->
            Row(modifier = Modifier.fillMaxWidth()) {
                TableCell(content = game.dateTime, modifier = Modifier.weight(1.5f))
                TableCell(content = game.winner, modifier = Modifier.weight(1f))
                TableCell(content = game.difficulty, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Preview
@Composable
fun PastGamesScreen() {

    val gameViewModel: GameViewModel = viewModel(factory = GameViewModelFactory(LocalContext.current))

    Column(modifier = Modifier.fillMaxSize()) {

        Spacer(modifier = Modifier.height(64.dp))

        PastGames(gameViewModel)

        Spacer(modifier = Modifier.height(32.dp))

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
