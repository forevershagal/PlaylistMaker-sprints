package com.example.android.playlistmaker.data.network

import com.example.android.playlistmaker.data.locale.SearchHistoryStorage
import com.example.android.playlistmaker.domain.api.SearchHistoryRepository
import com.example.android.playlistmaker.domain.models.Track

class SearchHistoryRepositoryImpl(
    private val storage: SearchHistoryStorage,
    private val maxHistorySize: Int = 10
) : SearchHistoryRepository {

    override fun addTrack(track: Track) {
        val history = getHistory().toMutableList()
        history.removeIf { it.trackId == track.trackId }
        history.add(0, track)

        if (history.size > maxHistorySize) {
            history.removeAt(history.lastIndex)
        }

        storage.saveHistory(history)
    }

    override fun getHistory(): List<Track> = storage.getHistory()

    override fun clearHistory() = storage.clearHistory()
}