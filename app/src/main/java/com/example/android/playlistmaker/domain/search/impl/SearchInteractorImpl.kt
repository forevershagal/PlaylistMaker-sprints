package com.example.android.playlistmaker.domain.search.impl

import com.example.android.playlistmaker.app.Resource
import com.example.android.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.android.playlistmaker.domain.models.Track
import com.example.android.playlistmaker.domain.search.SearchInteractor
import com.example.android.playlistmaker.domain.search.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchInteractorImpl(
    private val repository: SearchRepository,
    private val historyInteractor: SearchHistoryInteractor
) : SearchInteractor {

    override fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>> {
        return repository.searchTracks(expression).map { result ->
            when (result) {
                is Resource.Success -> {
                    val favouriteIds = repository.getFavouriteIds()
                    val tracksWithFavourites = result.data?.map { track ->
                        track.copy(isFavourite = favouriteIds.contains(track.trackId))
                    }
                    Pair(tracksWithFavourites, null)
                }

                is Resource.Error -> {
                    Pair(null, result.message)
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

    override suspend fun getFavouriteIds(): List<String> {
        return repository.getFavouriteIds()
    }

    override suspend fun getSearchHistory(): List<Track> {
        val history = historyInteractor.getHistory()
        val favouriteIds = repository.getFavouriteIds()
        return history.map { track ->
            track.copy(isFavourite = favouriteIds.contains(track.trackId))
        }
    }
}
