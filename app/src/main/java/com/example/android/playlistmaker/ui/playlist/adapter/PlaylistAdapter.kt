package com.example.playlistmaker.ui.playlist.adapter

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistItemBinding
import com.example.android.playlistmaker.domain.models.Playlist


class PlaylistAdapter(
    private val onPlaylistClick: (Playlist) -> Unit
) : ListAdapter<Playlist, PlaylistAdapter.PlaylistViewHolder>(PlaylistDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val binding = PlaylistItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PlaylistViewHolder(private val binding: PlaylistItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(playlist: Playlist) {
            with(binding) {
                val coverPath = playlist.coverPath
                if (!coverPath.isNullOrEmpty()) {
                    Glide.with(itemView)
                        .load(coverPath)
                        .apply(
                            bitmapTransform(
                                MultiTransformation(
                                    CenterCrop(),
                                    RoundedCorners(itemView.context.toPx(8))
                                )
                            )
                        )
                        .into(playlistCover)
                } else {
                    Glide.with(itemView)
                        .load(R.drawable.ic1)
                        .apply(
                            bitmapTransform(
                                MultiTransformation(
                                    CenterCrop(),
                                    RoundedCorners(itemView.context.toPx(8))
                                )
                            )
                        )
                        .into(playlistCover)
                }

                playlistName.text = playlist.name
                playlistTracksCount.text = itemView.context.resources.getQuantityString(
                    R.plurals.tracks_count,
                    playlist.trackCount,
                    playlist.trackCount
                )

                itemView.setOnClickListener {
                    onPlaylistClick(playlist)
                }
            }
        }
    }

    private fun Context.toPx(dp: Int): Int =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()

}

class PlaylistDiffCallback : DiffUtil.ItemCallback<Playlist>() {
    override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
        return oldItem == newItem
    }
}