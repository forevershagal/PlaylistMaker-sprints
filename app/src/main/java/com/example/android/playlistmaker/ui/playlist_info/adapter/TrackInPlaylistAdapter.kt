package com.example.android.playlistmaker.ui.playlist_info.adapter


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android.playlistmaker.domain.models.Track
import com.example.playlistmaker.databinding.TrackItemBinding

class TrackInPlaylistAdapter(
    private var tracks: List<Track>,
    private val onTrackClick: (Track) -> Unit,
    private val onTrackLongClick: (Track) -> Unit
) : RecyclerView.Adapter<TrackInPlaylistViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackInPlaylistViewHolder {
        val binding = TrackItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TrackInPlaylistViewHolder(binding, onTrackClick, onTrackLongClick)
    }


    override fun onBindViewHolder(holder: TrackInPlaylistViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount() = tracks.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateTracks(newTracks: List<Track>) {
        tracks = newTracks
        notifyDataSetChanged()
    }
}