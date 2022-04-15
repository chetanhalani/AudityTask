package com.audifytask.task.model

data class SongModel(val songId: Long, val songName: String, val artistName: String, val data: String, var isFavorite: Boolean)