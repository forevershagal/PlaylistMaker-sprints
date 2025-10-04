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
            playlistEntity?.let { entity ->

                // Обновляем LiveData плейлиста
                val trackCount = playlistInteractor.getPlaylistTrackCount(entity)
                _playlist.postValue(
                    Playlist(
                        id = entity.id,
                        name = entity.name,
                        description = entity.description,
                        coverPath = entity.coverPath,
                        trackCount = trackCount,
                        trackIds = entity.trackIds
                    )
                )

                // Преобразуем trackIds из JSON
                val trackIds: List<String> = gson.fromJson(
                    entity.trackIds,
                    object : TypeToken<List<String>>() {}.type
                ) ?: emptyList()

                // Получаем треки и сортируем: последние добавленные вверху
                val tracksList = playlistInteractor.getTracksByPlaylist(trackIds)
                    .map { it.toTrack() }
                    .sortedByDescending { trackIds.indexOf(it.trackId) }

                _tracks.postValue(tracksList)

                // Обновляем длительность
                calculateDuration(entity.trackIds)
            }
        }
    }

    fun calculateDuration(trackIdsJson: String) {
        viewModelScope.launch {
            try {
                val trackIds: List<String> = gson.fromJson(
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
            // Удаляем трек через репозиторий
            repository.deleteTrackFromPlaylist(playlistId, trackId)

            // Перезагружаем плейлист с сортировкой
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
