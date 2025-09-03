package com.example.android.playlistmaker.app.extensions

import com.example.android.playlistmaker.data.db.entity.PlaylistEntity
import com.example.android.playlistmaker.domain.models.Playlist

fun PlaylistEntity.toDomain(trackCount: Int): Playlist {
    return Playlist(
        id = this.id,
        name = this.name,
        description = this.description,
        coverPath = this.coverPath,
        trackCount = trackCount,
        trackIds = this.trackIds
    )
}