package com.example.android.playlistmaker.ui.search.adapter

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.android.playlistmaker.domain.models.Track

class TrackViewHolder(itemView: View, private val onClick: (Track) -> Unit) : RecyclerView.ViewHolder(itemView) {
    private val trackNameView: TextView
    private val artistNameView: TextView
    private val trackTimeView: TextView
    private val sourceImageView: ImageView

    init {
        trackNameView = itemView.findViewById(R.id.track_title)
        artistNameView = itemView.findViewById(R.id.artist_name)
        trackTimeView = itemView.findViewById(R.id.track_time)
        sourceImageView = itemView.findViewById(R.id.track_image)
    }

    fun bind(model: Track) {
        itemView.setOnClickListener {
            onClick(model)
        }
        trackNameView.text = model.trackName
        artistNameView.text = model.artistName
        trackTimeView.text = model.trackTimeMillis
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.track_avatar)
            .transform(RoundedCorners(itemView.context.toPx(2)))
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(sourceImageView)
    }

    private fun Context.toPx(dp: Int): Int =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
}