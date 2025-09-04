package com.example.android.playlistmaker.ui.bottom_sheet

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.example.playlistmaker.R
import com.example.android.playlistmaker.domain.models.Playlist

class PlaylistBottomSheetAdapter(
    private val onPlaylistClicked: (Playlist) -> Unit,
    private val onCreateNewClicked: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_PLAYLIST = 0
        private const val TYPE_CREATE_NEW = 1
    }

    private var playlists = listOf<Playlist>()

    fun submitList(playlists: List<Playlist>) {
        this.playlists = playlists
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < playlists.size) TYPE_PLAYLIST else TYPE_CREATE_NEW
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_PLAYLIST -> PlaylistViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.playlist_bottom_sheet_item, parent, false)
            )
            else -> CreateNewViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.playlist_bottom_sheet_item, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PlaylistViewHolder -> holder.bind(playlists[position])
            is CreateNewViewHolder -> holder.bind()
        }
    }

    override fun getItemCount(): Int = playlists.size + 1

    inner class PlaylistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val name = view.findViewById<TextView>(R.id.playlist_name)
        private val tracksCount = view.findViewById<TextView>(R.id.playlist_tracks_count)
        private val cover = view.findViewById<ImageView>(R.id.playlist_cover)

        fun bind(playlist: Playlist) {
            name.text = playlist.name
            tracksCount.text = itemView.context.resources
                .getQuantityString(R.plurals.tracks_count, playlist.trackCount, playlist.trackCount)

            Glide.with(itemView)
                .load(playlist.coverPath)
                .apply(
                    bitmapTransform(
                        MultiTransformation(
                            CenterCrop(),
                            RoundedCorners(itemView.context.toPx(2))
                        )
                    )
                )
                .placeholder(R.drawable.placeholder2)
                .apply(
                    bitmapTransform(
                        MultiTransformation(
                            CenterCrop(),
                            RoundedCorners(itemView.context.toPx(2))
                        )
                    )
                )
                .into(cover)

            itemView.setOnClickListener { onPlaylistClicked(playlist) }

        }
    }

    private fun Context.toPx(dp: Int): Int =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()

    inner class CreateNewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind() {
            itemView.setOnClickListener { onCreateNewClicked() }
        }
    }
}