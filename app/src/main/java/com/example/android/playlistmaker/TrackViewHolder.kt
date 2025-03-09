package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(parent: ViewGroup): RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.track_cell, parent, false)
) {
    private val trackName: TextView// Название композиции
    private val artistName: TextView // Имя исполнителя
    private val trackTime: TextView // Продолжительность трека
    private val trackImage: ImageView // Ссылка на изображение обложки
    init {
        trackName = itemView.findViewById(R.id.track_title)
        artistName = itemView.findViewById(R.id.artist_name)
        trackTime = itemView.findViewById(R.id.track_time)
        trackImage = itemView.findViewById(R.id.track_image)
    }

    fun bind(model: Track){
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis).toString()
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .transform(CenterCrop(),RoundedCorners(4))
            .placeholder(R.drawable.track_avatar)
            .into(trackImage)
    }
}