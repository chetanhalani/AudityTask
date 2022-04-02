package com.audifytask.task.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FavoriteSongDao {

    @Query("SELECT * FROM favoritesong")
    fun getAllFavoriteSongs(): List<FavoriteSong>

    @Query("SELECT * FROM favoritesong WHERE favorite_song_id IN (:favoriteSongIds)")
    fun loadAllByIds(favoriteSongIds: IntArray): List<FavoriteSong>

    @Insert
    fun insertAll(vararg users: FavoriteSong)

    @Delete
    fun delete(user: FavoriteSong)
}