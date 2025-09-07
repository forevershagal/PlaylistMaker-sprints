package com.example.android.playlistmaker.ui.playlist_info.adapter

import android.content.Context
import android.util.TypedValue
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TrackItemBinding
import com.example.android.playlistmaker.domain.models.Track

class TrackInPlaylistViewHolder(
    private val binding: TrackItemBinding,
    private val onClick: (Track) -> Unit,
    private val onTrackLongClick: (Track) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: Track) {
        binding.root.setOnClickListener {
            onClick(model)
        }
        binding.root.setOnLongClickListener {
            onTrackLongClick(model)
            true
        }

        binding.trackName.text = model.trackName
        val formattedTime = model.trackTimeMillis
        binding.trackInfo.text = "${model.artistName}   •   $formattedTime"

        Glide.with(binding.root)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.track_avatar)
            .transform(RoundedCorners(itemView.context.toPx(2)))
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(binding.trackIcon)
    }


    private fun formatTrackTime(millis: Long): String {
        val minutes = (millis / 1000) / 60
        val seconds = (millis / 1000) % 60
        return String.format("%d:%02d", minutes, seconds)
    }

    private fun Context.toPx(dp: Int): Int =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()

}