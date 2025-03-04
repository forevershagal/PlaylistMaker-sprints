package com.example.android.playlistmaker

import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val trackIcon: ImageView = itemView.findViewById(R.id.trackIcon)
    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val trackInfo: TextView = itemView.findViewById(R.id.trackInfo)

    fun bind(item: Track) {
        Glide.with(itemView).load(item.artworkUrl100).placeholder(R.drawable.track_avatar)
            .centerCrop().transform(
                RoundedCorners(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 2F, itemView.context.resources.displayMetrics
                    ).toInt()
                )
            ).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(trackIcon)
        trackName.text = item.trackName
        trackInfo.text = itemView.context.getString(
            R.string.trackInfo,
            item.artistName,
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(item.trackTimeMillis.toLong())
        )
    }
}
