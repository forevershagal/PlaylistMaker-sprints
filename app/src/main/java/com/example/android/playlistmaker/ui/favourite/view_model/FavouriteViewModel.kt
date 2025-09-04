package com.example.android.playlistmaker.ui.favourite.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.playlistmaker.domain.db.favourite_track.FavouriteTrackInteractor
import com.example.android.playlistmaker.ui.favourite.state.FavouriteTrackState
import kotlinx.coroutines.launch

class FavouriteViewModel (
    private val favouriteTrackInteractor: FavouriteTrackInteractor
): ViewModel() {

    private val _state = MutableLiveData<FavouriteTrackState>()
    val state: LiveData<FavouriteTrackState> = _state

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            favouriteTrackInteractor.getFavourite().collect { tracks ->
                _state.value = if (tracks.isEmpty()) {
                    FavouriteTrackState.Empty
                } else {
                    FavouriteTrackState.Content(tracks.map { it.copy(isFavourite = true) })
                }
            }
        }
    }

    fun refreshFavorites() {
        loadFavorites()
    }
}