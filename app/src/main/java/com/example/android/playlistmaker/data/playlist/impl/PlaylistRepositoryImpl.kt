package com.example.android.playlistmaker.data.playlist.impl

import com.example.android.playlistmaker.data.converter.toPlaylistTrackEntity
import com.example.android.playlistmaker.data.db.dao.PlaylistDao
import com.example.android.playlistmaker.data.db.entity.PlaylistEntity
import com.example.android.playlistmaker.data.db.entity.PlaylistTrackEntity
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
        try {
            val playlist = PlaylistEntity(
                id = 0,
                name = name,
                description = description,
                coverPath = coverPath
            )
            playlistDao.insert(playlist)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updatePlaylist(playlist: PlaylistEntity) = withContext(Dispatchers.IO) {
        playlistDao.update(playlist)
    }

    override suspend fun getPlaylist(id: Long) = withContext(Dispatchers.IO) {
        playlistDao.getById(id)
    }

    override suspend fun getPlaylistTracksCount(playlist: PlaylistEntity): Int {
        return withContext(Dispatchers.IO) {
            val trackIds = try {
                gson.fromJson<List<String>>(
                    playlist.trackIds,
                    object : TypeToken<List<String>>() {}.type
                ) ?: emptyList()
            } catch (e: Exception) {
                emptyList<String>()
            }
            trackIds.count()
        }
    }

    // Возвращаем Flow (оставляем как есть!)
    override fun getAllPlaylists(): Flow<List<PlaylistEntity>> {
        return playlistDao.getAll()
    }

    override suspend fun addTrackToPlaylist(
        playlist: PlaylistEntity,
        track: Track
    ): AddTrackResult {
        return withContext(Dispatchers.IO) {
            try {
                val trackIds = try {
                    gson.fromJson<List<String>>(
                        playlist.trackIds,
                        object : TypeToken<List<String>>() {}.type
                    ) ?: emptyList()
                } catch (e: Exception) {
                    emptyList<String>()
                }

                if (trackIds.contains(track.trackId)) {
                    return@withContext AddTrackResult.AlreadyExists
                }

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

    override suspend fun getTrackById(trackId: String): PlaylistTrackEntity? =
        withContext(Dispatchers.IO) {
            playlistDao.getTrackById(trackId)
        }

    override suspend fun getTracksByPlaylist(trackIds: List<String>): List<PlaylistTrackEntity> =
        withContext(Dispatchers.IO) {
            playlistDao.getTracksByPlaylist(trackIds)
        }

    override suspend fun getTrackDurations(trackIds: List<String>): List<Long> =
        withContext(Dispatchers.IO) {
            val durationStrings = playlistDao.getTrackDurations(trackIds)
            durationStrings.map { durationString ->
                try {
                    val parts = durationString.split(":")
                    val minutes = parts[0].toLong()
                    val seconds = parts.getOrNull(1)?.toLong() ?: 0
                    (minutes * 60 + seconds) * 1000
                } catch (e: Exception) {
                    0L
                }
            }
        }

    override suspend fun deleteTrackFromPlaylist(playlistId: Long, trackId: String) {
        withContext(Dispatchers.IO) {
            val playlist = getPlaylistById(playlistId) ?: return@withContext
            val trackIds = gson.fromJson<List<String>>(
                playlist.trackIds,
                object : TypeToken<List<String>>() {}.type
            )?.toMutableList() ?: mutableListOf()

            trackIds.remove(trackId)
            playlistDao.addTrackToPlaylist(playlistId, gson.toJson(trackIds))

            if (playlistDao.isTrackInAnyPlaylist(trackId) == null) {
                playlistDao.deleteTrack(trackId)
            }
        }
    }

    override suspend fun getPlaylistById(id: Long) = withContext(Dispatchers.IO) {
        playlistDao.getById(id)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        withContext(Dispatchers.IO) {
            val playlist = getPlaylistById(playlistId) ?: return@withContext
            val trackIds = gson.fromJson<List<String>>(
                playlist.trackIds,
                object : TypeToken<List<String>>() {}.type
            ) ?: emptyList()

            val deletedRows = playlistDao.deletePlaylist(playlistId)
            if (deletedRows > 0) {
                trackIds.forEach { trackId ->
                    if (playlistDao.isTrackInAnyPlaylist(trackId) == null) {
                        playlistDao.deleteTrack(trackId)
                    }
                }
            }
        }
    }
}
