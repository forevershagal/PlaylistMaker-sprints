package com.example.android.playlistmaker.ui.search.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.android.playlistmaker.app.SingleLiveEvent
import com.example.android.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.android.playlistmaker.domain.models.Track
import com.example.android.playlistmaker.domain.search.SearchInteractor
import com.example.android.playlistmaker.ui.search.screen_state.SearchScreenState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Suppress("UNCHECKED_CAST")
class SearchViewModel(
    application: Application,
    private val interactor: SearchInteractor,
    private val historyInteractor: SearchHistoryInteractor
) : AndroidViewModel(application) {

    private val stateLiveData = MutableLiveData<SearchScreenState>()
    private val showToastLiveData = SingleLiveEvent<String>()

    private var latestSearchText: String? = null
    private var searchJob: Job? = null

    fun observeState(): LiveData<SearchScreenState> = stateLiveData
    fun observeShowToast(): LiveData<String> = showToastLiveData

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) return

        latestSearchText = changedText

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchRequest(changedText)
        }
    }

    private fun searchRequest(query: String) {
        if (query.isEmpty()) {
            loadSearchHistory()
            return
        }

        renderState(SearchScreenState.Loading)

        viewModelScope.launch {
            interactor.searchTracks(query).collect { (tracks, error) ->
                processResult(tracks, error)
            }
        }
    }

    private fun processResult(tracks: List<Track>?, error: String?) {
        when {
            error != null -> {
                renderState(SearchScreenState.Error(getApplication<Application>().getString(
                    R.string.something_went_wrong)))
            }
            tracks.isNullOrEmpty() -> {
                renderState(SearchScreenState.Empty(getApplication<Application>().getString(
                    R.string.nothing_found)))
            }
            else -> {
                renderState(SearchScreenState.Content(tracks = tracks))
            }
        }
    }

    fun loadSearchHistory() {
        val history = interactor.getSearchHistory()
        renderState(SearchScreenState.History(history))
    }

    fun addToHistory(track: Track) {
        interactor.addTrackToHistory(track)
    }

    fun clearSearchHistory() {
        interactor.clearSearchHistory()
        renderState(SearchScreenState.History(emptyList()))
    }

    private fun renderState(state: SearchScreenState) {
        stateLiveData.postValue(state)
    }

    private fun showToast(message: String) {
        showToastLiveData.postValue(message)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }
}
