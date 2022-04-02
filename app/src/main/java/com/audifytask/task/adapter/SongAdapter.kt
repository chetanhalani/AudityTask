package com.audifytask.task.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.audifytask.task.MainActivity
import com.audifytask.task.R
import com.audifytask.task.model.SongModel

class SongAdapter(private var songs: List<SongModel>? = null, val songFavoriteImpl: MainActivity) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.song_row_view, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        songs?.let {
            if(position < it.size) {
                holder.songName.text = it[position].songName
                holder.songArtist.text = it[position].artistName
                holder.favorite.tag = it[position].songId
                holder.favorite.setImageResource(if(it[position].isFavorite) R.drawable.favorite else R.drawable.broken_heart)
                holder.favorite.setOnClickListener { p0 ->
                    p0?.let { view ->
                        songFavoriteImpl.favoriteActionPerformed(view.tag as Long)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        songs?.let {
            return it.size
        }
        return 0
    }

    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val songName = itemView.findViewById<TextView>(R.id.tvSongName)
        val songArtist = itemView.findViewById<TextView>(R.id.tvSongArtist)
        val favorite = itemView.findViewById<ImageView>(R.id.imgFavorite)
    }

    fun setSongs(songs: List<SongModel>) {
        this.songs = songs
        notifyDataSetChanged()
    }
}

interface SongFavoriteAction {
    fun favoriteActionPerformed(songId: Long)
}