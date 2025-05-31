package com.example.android.playlistmaker.domain.api

import com.example.android.playlistmaker.domain.models.Track


interface TracksInteractor{
    fun searchTracks(expression: String, consumer: TrackConsumer)

    interface TrackConsumer{
        fun consume(foundTracks: List<Track>)
        fun onError(error: Throwable)
    }
}