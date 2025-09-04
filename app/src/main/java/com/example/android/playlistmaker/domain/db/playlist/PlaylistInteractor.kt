package com.example.android.playlistmaker.domain.db.playlist

import com.example.android.playlistmaker.data.db.entity.PlaylistEntity
import com.example.android.playlistmaker.domain.models.Playlist
import com.example.android.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun createPlaylist(name: String, description: String, coverPath: String?): Long
    suspend fun updatePlaylist(playlist: PlaylistEntity)
    suspend fun getPlaylist(id: Long): PlaylistEntity?
    suspend fun getPlaylistTrackCount(playlist: PlaylistEntity): Int
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>
    suspend fun addTrackToPlaylist(playlist: Playlist, track: Track): AddTrackResult
}
