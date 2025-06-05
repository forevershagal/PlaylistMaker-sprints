package com.example.android.playlistmaker.domain.search.impl

import com.example.android.playlistmaker.app.Resource
import com.example.android.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.android.playlistmaker.domain.models.Track
import com.example.android.playlistmaker.domain.search.ErrorType
import com.example.android.playlistmaker.domain.search.SearchInteractor
import com.example.android.playlistmaker.domain.search.SearchRepository
import com.example.android.playlistmaker.domain.utils.NetworkChecker
import java.util.concurrent.Executors

class SearchInteractorImpl(
    private val repository: SearchRepository,
    private val historyInteractor: SearchHistoryInteractor,
    private val networkChecker: NetworkChecker
) : SearchInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: (List<Track>?, ErrorType?) -> Unit) {
        executor.execute {
            if (!networkChecker.isConnected()) {
                consumer(null, ErrorType.NoInternet)
                return@execute
            }

            when (val result = repository.searchTracks(expression)) {
                is Resource.Success -> {
                    consumer(result.data, null)
                }
                is Resource.Error -> {
                    consumer(null, ErrorType.ServerError)
                }
            }
        }
    }

    override fun addTrackToHistory(track: Track) {
        historyInteractor.addTrack(track)
    }

    override fun clearSearchHistory() {
        historyInteractor.clearHistory()
    }

    override fun getSearchHistory(): List<Track> {
        return historyInteractor.getHistory()
    }
}
