package com.example.android.playlistmaker.ui.search.view_model

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.android.playlistmaker.app.SingleLiveEvent
import com.example.android.playlistmaker.creator.Creator
import com.example.android.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.android.playlistmaker.domain.models.Track
import com.example.android.playlistmaker.domain.search.ErrorType
import com.example.android.playlistmaker.domain.search.SearchInteractor
import com.example.android.playlistmaker.ui.search.screen_state.SearchScreenState

@Suppress("UNCHECKED_CAST")
class SearchViewModel(
    application: Application,
    private val interactor: SearchInteractor,
    private val historyInteractor: SearchHistoryInteractor
) : AndroidViewModel(application) {

    private val handler = Handler(Looper.getMainLooper())
    private val stateLiveData = MutableLiveData<SearchScreenState>()
    private val showToastLiveData = SingleLiveEvent<String>()

    private var latestSearchText: String? = null

    fun observeState(): LiveData<SearchScreenState> = stateLiveData
    fun observeShowToast(): LiveData<String> = showToastLiveData

    fun searchDebounce(changedText: String, force: Boolean = false) {
        if (!force && latestSearchText == changedText) return

        latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchRequest(changedText) }
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        )
    }

    private fun searchRequest(query: String) {
        if (query.isEmpty()) {
            loadSearchHistory()
            return
        }

        renderState(SearchScreenState.Loading)

        interactor.searchTracks(query) { foundTracks, errorType ->
            when (errorType) {
                null -> {
                    if (foundTracks.isNullOrEmpty()) {
                        renderState(SearchScreenState.Empty(
                            getApplication<Application>().getString(R.string.nothing_found)
                        ))
                    } else {
                        renderState(SearchScreenState.Content(foundTracks))
                    }
                }
                ErrorType.NoInternet -> {
                    renderState(SearchScreenState.Error(
                        getApplication<Application>().getString(R.string.something_went_wrong)
                    ))
                }
                ErrorType.ServerError -> {
                    renderState(SearchScreenState.Error(
                        getApplication<Application>().getString(R.string.something_went_wrong)
                    ))
                }
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


    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getViewModelFactory(application: Application): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SearchViewModel(
                        application,
                        Creator.provideSearchInteractor(application),
                        Creator.provideSearchHistoryInteractor(application)
                    ) as T
                }
            }
        }
    }
}
