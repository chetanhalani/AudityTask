package com.audifytask.task.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FavoriteSong::class], version = 1)
abstract class FavoriteDatabase : RoomDatabase() {
    abstract fun favoriteSongDao(): FavoriteSongDao
}