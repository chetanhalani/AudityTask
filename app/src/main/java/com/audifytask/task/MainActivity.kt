package com.audifytask.task

import android.app.Notification
import android.net.Uri
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.audifytask.task.adapter.SongAdapter
import com.audifytask.task.adapter.SongFavoriteAction
import com.audifytask.task.model.SongModel
import com.audifytask.task.model.SongRepositoryFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.NotificationUtil
import kotlin.collections.indices as indices1


class MainActivity : AppCompatActivity(), SongFavoriteAction {

    var songAdapter : SongAdapter? = null
    lateinit var songRepositoryFactory : SongRepositoryFactory
    private val exoPlayer: ExoPlayer by lazy { ExoPlayer.Builder(this).build()}
    private lateinit var pv: PlayerControlView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pv = findViewById(R.id.exoPlayer)
        songRepositoryFactory = ViewModelProvider(this).get(SongRepositoryFactory::class.java)
        songRepositoryFactory.liveSongs.observe(this){
            setAdapterForSongs(it)
        }
        songRepositoryFactory.fetchSongs(this@MainActivity)
    }

    private fun setAdapterForSongs(songs : List<SongModel>) {
        songAdapter?.setSongs(songs) ?: run {
            songAdapter = SongAdapter(songs, this@MainActivity)
            val recyclerView = findViewById<RecyclerView>(R.id.rvSongList)
            with(recyclerView) {
                layoutManager = LinearLayoutManager(this@MainActivity)
                adapter = songAdapter
            }
        }
    }

    override fun favoriteActionPerformed(songId: Long) {
        songRepositoryFactory.performUserAction(songId)
    }

    override fun playSong(position: Int) {
        pv.visibility = VISIBLE
        prepareExoPlayer(position)
        createNotification()
    }

    private fun prepareExoPlayer(songPosition: Int = 0) {
        songRepositoryFactory.liveSongs.value?.let { songs ->

            val dataSourceFactory =
                DefaultDataSource.Factory(this)

            val concateMediaSource = ConcatenatingMediaSource()

            // to play from song list
            for(i in songs.indices1) {      // song list song arraylist
                val mediaSource = ProgressiveMediaSource
                    .Factory(
                        DefaultDataSourceFactory(this, dataSourceFactory),
                        DefaultExtractorsFactory()
                    )
                    .createMediaSource(MediaItem.fromUri(Uri.parse(songs[i].data))/*Uri.parse(i.uri)*/)
                concateMediaSource.addMediaSource(mediaSource)
            }
            exoPlayer.prepare(concateMediaSource/*audioSource*/)
            exoPlayer.seekToDefaultPosition(songPosition)
            exoPlayer.playWhenReady = true
            exoPlayer.addListener(object : Player.Listener{
                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    if (playbackState == ExoPlayer.STATE_ENDED){
                        pv.visibility = GONE
                    }
                }
            })
            pv.player = exoPlayer
        }
    }

    private fun createNotification() {
        val myNoti = PlayerNotificationManager.Builder(
            applicationContext, 1234, "1234"
        )
        myNoti.setChannelImportance(NotificationUtil.IMPORTANCE_LOW)
        myNoti.setChannelNameResourceId(R.string.app_name)

        val notiBuilder = myNoti.build()
        notiBuilder.setUseNextAction(true)
        notiBuilder.setUsePreviousAction(true)
        notiBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        notiBuilder.setPlayer(exoPlayer)
    }
}