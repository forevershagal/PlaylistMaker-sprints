package com.example.android.playlistmaker.ui.playlist.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.playlistmaker.app.extensions.toDomain
import com.example.android.playlistmaker.domain.db.playlist.PlaylistInteractor
import com.example.android.playlistmaker.domain.models.Playlist
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val interactor: PlaylistInteractor
) : ViewModel() {

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    fun loadPlaylists() {
        viewModelScope.launch {
            interactor.getAllPlaylists().collect { playlistEntities ->
                val playlistDomain = playlistEntities.map { entity ->
                    entity.toDomain(interactor.getPlaylistTrackCount(entity))
                }
                _playlists.value = playlistDomain
            }
        }
    }
}
