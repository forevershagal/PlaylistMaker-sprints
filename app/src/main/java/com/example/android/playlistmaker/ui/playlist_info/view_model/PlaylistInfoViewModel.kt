package com.example.android.playlistmaker.ui.playlist_info.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.playlistmaker.data.converter.toTrack
import com.example.android.playlistmaker.domain.db.playlist.PlaylistInteractor
import com.example.android.playlistmaker.domain.db.playlist.PlaylistRepository
import com.example.android.playlistmaker.domain.models.Playlist
import com.example.android.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

class PlaylistInfoViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val gson: Gson,
    private val repository: PlaylistRepository
) : ViewModel() {

    private val _refreshTrigger = MutableLiveData<Unit>()
    val refreshTrigger: LiveData<Unit> = _refreshTrigger

    private val _playlist = MutableLiveData<Playlist>()
    val playlist: LiveData<Playlist> = _playlist

    private val _duration = MutableLiveData<String>()
    val duration: LiveData<String> = _duration

    private val _tracks = MutableLiveData<List<Track>>()
    val tracks: LiveData<List<Track>> = _tracks

    fun loadPlaylist(playlistId: Long) {
        viewModelScope.launch {
            val playlistEntity = playlistInteractor.getPlaylist(playlistId)
            playlistEntity?.let {
                val trackCount = playlistInteractor.getPlaylistTrackCount(it)
                _playlist.postValue(
                    Playlist(
                        id = playlistEntity.id,
                        name = playlistEntity.name,
                        description = playlistEntity.description,
                        coverPath = playlistEntity.coverPath,
                        trackCount = trackCount,
                        trackIds = playlistEntity.trackIds
                    )
                )
                val trackIds = gson.fromJson<List<String>>(it.trackIds, object : TypeToken<List<String>>() {}.type)
                    ?: emptyList()
                val tracks = playlistInteractor.getTracksByPlaylist(trackIds).map { it.toTrack() }
                _tracks.postValue(tracks)
                calculateDuration(it.trackIds)
            }
        }
    }

    fun calculateDuration(trackIdsJson: String) {
        viewModelScope.launch {
            try {
                val trackIds = gson.fromJson<List<String>>(
                    trackIdsJson,
                    object : TypeToken<List<String>>() {}.type
                ) ?: emptyList()

                _duration.postValue(
                    playlistInteractor.calculateTotalDuration(trackIds)
                )
            } catch (e: Exception) {
                _duration.postValue("00:00")
            }
        }
    }

    fun deleteTrack(playlistId: Long, trackId: String) {
        viewModelScope.launch {
            repository.deleteTrackFromPlaylist(playlistId, trackId)
            loadPlaylist(playlistId)
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            repository.deletePlaylist(playlistId)
        }
    }

    fun forceRefresh() {
        _refreshTrigger.postValue(Unit)
    }
}