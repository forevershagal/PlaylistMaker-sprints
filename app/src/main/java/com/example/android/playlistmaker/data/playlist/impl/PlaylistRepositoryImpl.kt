package com.example.android.playlistmaker.data.playlist.impl

import com.example.android.playlistmaker.data.converter.toPlaylistTrackEntity
import com.example.android.playlistmaker.data.db.dao.PlaylistDao
import com.example.android.playlistmaker.data.db.entity.PlaylistEntity
import com.example.android.playlistmaker.domain.db.playlist.AddTrackResult
import com.example.android.playlistmaker.domain.db.playlist.PlaylistRepository
import com.example.android.playlistmaker.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val gson: Gson
) : PlaylistRepository {

    override suspend fun createPlaylist(
        name: String,
        description: String,
        coverPath: String?
    ): Long = withContext(Dispatchers.IO) {
        val playlist = PlaylistEntity(
            name = name,
            description = description,
            coverPath = coverPath
        )
        playlistDao.insert(playlist)
    }

    override suspend fun updatePlaylist(playlist: PlaylistEntity) = withContext(Dispatchers.IO) {
        playlistDao.update(playlist)
    }

    override suspend fun getPlaylist(id: Long) = withContext(Dispatchers.IO) {
        playlistDao.getById(id)
    }

    override suspend fun getPlaylistTracksCount(playlist: PlaylistEntity): Int = withContext(Dispatchers.IO) {
        val trackIds = try {
            gson.fromJson<List<String>>(
                playlist.trackIds,
                object : TypeToken<List<String>>() {}.type
            ) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
        trackIds.count()
    }

    // Возвращаем Flow
    override fun getAllPlaylists(): Flow<List<PlaylistEntity>> {
        return playlistDao.getAll()
    }

    override suspend fun addTrackToPlaylist(playlist: PlaylistEntity, track: Track): AddTrackResult {
        return withContext(Dispatchers.IO) {
            try {
                val trackIds = try {
                    gson.fromJson<List<String>>(
                        playlist.trackIds,
                        object : TypeToken<List<String>>() {}.type
                    ) ?: emptyList()
                } catch (e: Exception) {
                    emptyList()
                }

                if (trackIds.contains(track.trackId)) return@withContext AddTrackResult.AlreadyExists

                playlistDao.insertTrack(track.toPlaylistTrackEntity())
                val updatedTrackIds = trackIds + track.trackId
                playlistDao.addTrackToPlaylist(
                    playlist.id,
                    gson.toJson(updatedTrackIds)
                )

                AddTrackResult.Success
            } catch (e: Exception) {
                AddTrackResult.Error(e.message ?: "Unknown error")
            }
        }
    }
}
