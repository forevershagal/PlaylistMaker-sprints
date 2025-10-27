package com.example.android.playlistmaker.domain.db.playlist

import com.example.android.playlistmaker.data.db.entity.PlaylistEntity
import com.example.android.playlistmaker.data.db.entity.PlaylistTrackEntity
import com.example.android.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun createPlaylist(name: String, description: String, coverPath: String?): Long
    suspend fun updatePlaylist(playlist: PlaylistEntity)
    suspend fun getPlaylist(id: Long): PlaylistEntity?
    suspend fun getPlaylistTracksCount(playlist: PlaylistEntity): Int
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>  // <- Flow
    suspend fun addTrackToPlaylist(playlist: PlaylistEntity, track: Track): AddTrackResult
    suspend fun getTrackById(trackId: String): PlaylistTrackEntity?
    suspend fun getTracksByPlaylist(trackIds: List<String>): List<PlaylistTrackEntity>
    suspend fun getTrackDurations(trackIds: List<String>): List<Long>
    suspend fun deleteTrackFromPlaylist(playlistId: Long, trackId: String)
    suspend fun getPlaylistById(id: Long): PlaylistEntity?
    suspend fun deletePlaylist(playlistId: Long)
}

sealed class AddTrackResult {
    object Success : AddTrackResult()
    object AlreadyExists : AddTrackResult()
    class Error(val message: String) : AddTrackResult()
}
