package com.audifytask.task.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FavoriteSong (
    @PrimaryKey @ColumnInfo(name = "favorite_song_id") val favoriteSongId: Long
)