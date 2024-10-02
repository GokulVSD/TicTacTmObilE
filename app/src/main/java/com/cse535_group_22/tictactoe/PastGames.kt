package com.cse535_group_22.tictactoe

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Entity(tableName = "past_games")
data class PastGame(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateTime: String,
    val winner: String,
)

@Dao
interface PastGamesDao {

    @Insert
    suspend fun insertGame(game: PastGame)

    @Query("SELECT * FROM past_games ORDER BY dateTime DESC")
    suspend fun getPastGames(): List<PastGame>

    @Query("DELETE FROM past_games")
    suspend fun clearPastGames()
}
