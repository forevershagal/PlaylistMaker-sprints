package com.example.android.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.signature.ObjectKey
import com.example.playlistmaker.R

class TrackViewHolder private constructor(trackView: View): RecyclerView.ViewHolder(trackView) {
    private val trackTitle: TextView
    private val artistName: TextView
    private val trackTime: TextView
    private val artwork: ImageView
    private val trackView = trackView

    init {
        trackTitle = trackView.findViewById(R.id.track_title)
        artistName = trackView.findViewById(R.id.artist)
        trackTime = trackView.findViewById(R.id.track_time)
        artwork = trackView.findViewById(R.id.track_image)
    }

    fun bind(model: Track) {
        trackTitle.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = model.trackTime
        Glide.with(trackView.context)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.track_avatar)
            .fitCenter()
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .signature(ObjectKey(System.currentTimeMillis()))
            .transform(RoundedCorners(2))
            .into(artwork)
    }

    companion object {
        fun from(parent: ViewGroup): TrackViewHolder {
            val view = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.track_cell, parent,false)
            return TrackViewHolder(view)
        }
    }
}