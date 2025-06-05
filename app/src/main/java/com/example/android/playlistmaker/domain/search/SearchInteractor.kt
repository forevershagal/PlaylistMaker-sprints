package com.example.android.playlistmaker.domain.search

import com.example.android.playlistmaker.domain.models.Track

interface SearchInteractor {
    fun searchTracks(expression: String, consumer: (List<Track>?, ErrorType?) -> Unit)
    fun addTrackToHistory(track: Track)
    fun getSearchHistory(): List<Track>
    fun clearSearchHistory()

}