package com.cse535_group_22.tictactoe

import androidx.room.Database
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.Room

@Database(entities = [PastGame::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pastGamesDao(): PastGamesDao
}

object DatabaseBuilder {
    private var INSTANCE: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase {
        if (INSTANCE == null) {
            synchronized(AppDatabase::class) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "past_games.db"
                ).build()
            }
        }
        return INSTANCE!!
    }
}

