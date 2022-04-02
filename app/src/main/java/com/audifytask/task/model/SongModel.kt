package com.audifytask.task.model

data class SongModel(val songId: Long, val songName: String, val artistName: String, var isFavorite: Boolean)