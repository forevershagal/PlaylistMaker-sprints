package com.example.android.playlistmaker.ui.search.screen_state

import com.example.android.playlistmaker.domain.models.Track

sealed class SearchScreenState {

    data object Loading : SearchScreenState()
    data class Content(val tracks: List<Track>) : SearchScreenState()
    data class Empty(val message: String) : SearchScreenState()
    data class Error(val message: String) : SearchScreenState()
    data class History(val tracks: List<Track>) : SearchScreenState()

}