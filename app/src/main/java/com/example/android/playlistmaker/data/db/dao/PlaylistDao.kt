package com.example.android.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.android.playlistmaker.data.db.entity.PlaylistEntity
import com.example.android.playlistmaker.data.db.entity.PlaylistTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(playlist: PlaylistEntity): Long

    @Update
    fun update(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlists WHERE id = :id")
    fun getById(id: Long): PlaylistEntity?

    // Теперь возвращает Flow
    @Query("SELECT * FROM playlists ORDER BY name")
    fun getAll(): Flow<List<PlaylistEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertTrack(track: PlaylistTrackEntity)

    @Query("SELECT * FROM playlist_tracks WHERE trackId = :trackId")
    fun getTrackById(trackId: String): PlaylistTrackEntity?

    @Query("UPDATE playlists SET trackIds = :trackIds WHERE id = :playlistId")
    fun addTrackToPlaylist(playlistId: Long, trackIds: String): Int

    @Query("SELECT * FROM playlist_tracks WHERE trackId IN (:trackIds)")
    fun getTracksByPlaylist(trackIds: List<String>): List<PlaylistTrackEntity>

    @Query("SELECT trackTime FROM playlist_tracks WHERE trackId IN (:trackIds)")
    fun getTrackDurations(trackIds: List<String>): List<String>

    @Query("DELETE FROM playlist_tracks WHERE trackId = :trackId")
    fun deleteTrack(trackId: String)

    @Query("SELECT trackId FROM playlist_tracks WHERE trackId = :trackId LIMIT 1")
    fun isTrackInAnyPlaylist(trackId: String): String?

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    fun deletePlaylist(playlistId: Long):Int
}
