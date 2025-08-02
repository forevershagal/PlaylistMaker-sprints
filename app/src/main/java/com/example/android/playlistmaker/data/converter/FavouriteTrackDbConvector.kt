package com.example.android.playlistmaker.data.converter

import com.example.android.playlistmaker.data.db.entity.FavouriteTrackEntity
import com.example.android.playlistmaker.domain.models.Track

class FavouriteTrackDbConvertor {

    fun map(track: Track): FavouriteTrackEntity {
        return FavouriteTrackEntity(
            trackId = track.trackId,
            artworkUrl100 = track.artworkUrl100,
            trackName = track.trackName,
            artistName = track.artistName,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            trackTimeMillis = track.trackTimeMillis,
            previewUrl = track.previewUrl,
            isFavourite = track.isFavourite)
    }

    fun map(track:  FavouriteTrackEntity): Track {
        return Track(
            trackId = track.trackId,
            artworkUrl100 = track.artworkUrl100,
            trackName = track.trackName,
            artistName = track.artistName,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            trackTimeMillis = track.trackTimeMillis,
            previewUrl = track.previewUrl,
            isFavourite = track.isFavourite)
    }

}