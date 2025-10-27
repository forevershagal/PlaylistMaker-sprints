package com.example.android.playlistmaker.data.converter

import com.example.android.playlistmaker.data.db.entity.PlaylistTrackEntity
import com.example.android.playlistmaker.domain.models.Track

fun Track.toPlaylistTrackEntity() = PlaylistTrackEntity(
    trackId = trackId,
    trackName = trackName,
    artistName = artistName,
    trackTime = trackTimeMillis,
    artworkUrl100 = artworkUrl100,
    collectionName = collectionName,
    releaseDate = releaseDate,
    primaryGenreName = primaryGenreName,
    country = country,
    previewUrl = previewUrl,
    isFavourite = isFavourite
)