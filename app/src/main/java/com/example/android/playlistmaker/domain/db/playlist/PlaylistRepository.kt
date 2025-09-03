package com.example.android.playlistmaker.domain.db.playlist

import com.example.android.playlistmaker.data.db.entity.PlaylistEntity
import com.example.android.playlistmaker.domain.models.Track

interface PlaylistRepository {
    suspend fun createPlaylist(name: String, description: String, coverPath: String?): Long
    suspend fun updatePlaylist(playlist: PlaylistEntity)
    suspend fun getPlaylist(id: Long): PlaylistEntity?
    suspend fun getPlaylistTracksCount(playlist: PlaylistEntity): Int
    suspend fun getAllPlaylists(): List<PlaylistEntity>
    suspend fun addTrackToPlaylist(playlist: PlaylistEntity, track: Track): AddTrackResult
}

sealed class AddTrackResult {
    object Success : AddTrackResult()
    object AlreadyExists : AddTrackResult()
    class Error(val message: String) : AddTrackResult()
}