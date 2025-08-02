package com.example.android.playlistmaker.data.db.dao


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.playlistmaker.data.db.entity.FavouriteTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteTrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addToFavourite(track: FavouriteTrackEntity)

    @Delete
    fun removeFromFavourite(track: FavouriteTrackEntity)

    @Query("DELETE FROM favourite_track_table WHERE trackId = :trackId")
    fun removeById(trackId: String)

    @Query("SELECT * FROM favourite_track_table ORDER BY rowid DESC")
    fun getFavourite(): Flow<List<FavouriteTrackEntity>>

    @Query("SELECT trackId FROM favourite_track_table")
    fun getFavouriteId(): Flow<List<String>>
}