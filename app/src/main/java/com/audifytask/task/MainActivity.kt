package com.audifytask.task

import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.audifytask.task.adapter.SongAdapter
import com.audifytask.task.adapter.SongFavoriteAction
import com.audifytask.task.database.FavoriteDatabase
import com.audifytask.task.database.FavoriteSong
import com.audifytask.task.model.SongModel
import com.audifytask.task.model.SongRepositoryFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), SongFavoriteAction {

    var songAdapter : SongAdapter? = null
    lateinit var songRepositoryFactory : SongRepositoryFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        songRepositoryFactory = ViewModelProvider(this).get(SongRepositoryFactory::class.java)
        songRepositoryFactory.liveSongs.observe(this){
            setAdapterForSongs(it)
        }
        songRepositoryFactory.fetchSongs(this@MainActivity)
    }

    fun setAdapterForSongs(songs : List<SongModel>) {
        songAdapter?.setSongs(songs) ?: run {
            songAdapter = SongAdapter(songs, this@MainActivity)
            val recyclerView = findViewById<RecyclerView>(R.id.rvSongList)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = songAdapter
        }
    }

    override fun favoriteActionPerformed(songId: Long) {
        songRepositoryFactory.performUserAction(songId)
    }
}