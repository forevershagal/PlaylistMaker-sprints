package com.example.android.playlistmaker.data.converter

import com.example.android.playlistmaker.data.db.entity.PlaylistTrackEntity
import com.example.android.playlistmaker.domain.models.Track

fun PlaylistTrackEntity.toTrack(): Track {
    return Track(
        trackId = this.trackId,
        trackName = this.trackName,
        artistName = this.artistName,
        trackTimeMillis = this.trackTime,
        artworkUrl100 = this.artworkUrl100,
        collectionName = this.collectionName,
        releaseDate = this.releaseDate,
        primaryGenreName = this.primaryGenreName,
        country = this.country,
        previewUrl = this.previewUrl,
        isFavourite = this.isFavourite
    )
}
