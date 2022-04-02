package com.audifytask.task.model

import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.audifytask.task.database.FavoriteDatabase
import com.audifytask.task.database.FavoriteSong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SongRepositoryFactory : ViewModel() {
    private var mutableSongList = MutableLiveData<List<SongModel>>()
    val liveSongs : LiveData<List<SongModel>> get() = mutableSongList
    var existingFavoriteSongList : List<FavoriteSong>? = null
    lateinit var favoriteDatabase: FavoriteDatabase

    fun fetchSongs(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            with(context) {
                initDatabase(this)
                findAudioFiles(this)
            }
        }
    }

    fun performUserAction(songId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            var favoriteObject : FavoriteSong? = null
            existingFavoriteSongList?.let {
                for(element in it) {
                    if(element.favoriteSongId == songId) {
                        favoriteObject = element
                        break
                    }
                }
            }
            favoriteObject?.let {
                favoriteDatabase.favoriteSongDao().delete(it)
            } ?: run {
                favoriteDatabase.favoriteSongDao().insertAll(FavoriteSong(favoriteSongId = songId))
            }
            val songList = mutableSongList.value
            songList?.let {
                for(i in it.indices) {
                    if(it[i].songId == songId) {
                        it[i].isFavorite = !it[i].isFavorite
                    }
                }
            }
            mutableSongList.postValue(songList)
        }
    }

    private fun initDatabase(context: Context) {
        favoriteDatabase = Room.databaseBuilder(
            context,
            FavoriteDatabase::class.java, "favorite-songs"
        ).build()
        existingFavoriteSongList = favoriteDatabase.favoriteSongDao().getAllFavoriteSongs()
    }

    private fun findAudioFiles(context: Context) {
        val songList = ArrayList<SongModel>()

        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Audio.Media.getContentUri(
                    MediaStore.VOLUME_INTERNAL
                )
            } else {
                MediaStore.Audio.Media.INTERNAL_CONTENT_URI
            }

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Albums.ARTIST
        )

// Display videos in alphabetical order based on their display name.
        val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"

        context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder
        )?.let {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val displayName = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
            val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            while(it.moveToNext()) {
                val id = it.getLong(idColumn)
                songList.add(SongModel(id, it.getString(displayName), it.getString(artistColumn), isFavoriteSong(id)))
            }
            it.close()
        }
        mutableSongList.postValue(songList.toList())
    }

    private fun isFavoriteSong(id: Long) : Boolean {
        existingFavoriteSongList?.let {
            for(i in it.indices) {
                if(it[i].favoriteSongId == id) return true
            }
        }
        return false
    }

}