package com.example.android.playlistmaker.domain.impl

import com.example.android.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.android.playlistmaker.domain.api.SearchHistoryRepository
import com.example.android.playlistmaker.domain.models.Track

class SearchHistoryInteractorImpl (
    private val repository: SearchHistoryRepository
): SearchHistoryInteractor {

    override fun addTrack(track: Track) {
        repository.addTrack(track)
    }

    override fun getHistory(): List<Track> {
        return repository.getHistory()
    }

    override fun clearHistory() {
        repository.clearHistory()
    }
}