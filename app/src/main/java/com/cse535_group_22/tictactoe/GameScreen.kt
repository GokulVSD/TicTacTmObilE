package com.cse535_group_22.tictactoe

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GameBoard(boardState: List<List<Char>>, onClick: (Int, Int) -> Unit) {
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
                            .clickable { onClick(row, col) }
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

@Preview
@Composable
fun GameScreen() {
    val boardViewModel = viewModel(BoardViewModel::class.java)


    Column(modifier = Modifier.fillMaxSize()) {

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                painter = painterResource(R.drawable.settings),
                contentDescription = "Settings",
                tint = Color(0xff5c5652),
                modifier = Modifier
                    .size(48.dp)
                    .clickable { /* Handle settings click */ }
            )

            Box(
                modifier = Modifier
                    .border(BorderStroke(3.dp, Color(0xff5c5652)), shape = RoundedCornerShape(20.dp))
                    .clickable { /* Handle button click */ }
            ) {
                Text(
                    text = "Player vs AI",
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
                    .clickable { /* Handle leaderboard click */ }
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Tic Tac Toe",
                color = Color(0xff5c5652),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        GameBoard(boardState = boardViewModel.boardState, onClick = { row, col ->
                boardViewModel.onClick(row, col)
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
                    .clickable { /* Handle button click */ }
            ) {
                Row {
                    Icon(
                        painter = painterResource(R.drawable.play),
                        contentDescription = "Play",
                        tint = Color(0xff5c5652),
                        modifier = Modifier
                            .size(72.dp)
                    )
                }

            }
        }
    }

}
