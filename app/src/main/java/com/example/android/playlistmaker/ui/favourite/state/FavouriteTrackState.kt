package com.example.android.playlistmaker.ui.favourite.state

import com.example.android.playlistmaker.domain.models.Track

sealed class FavouriteTrackState {
    object Empty : FavouriteTrackState()
    data class Content(val tracks: List<Track>) : FavouriteTrackState()
}