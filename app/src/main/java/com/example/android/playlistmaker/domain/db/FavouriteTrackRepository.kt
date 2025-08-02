package com.example.android.playlistmaker.domain.db

import com.example.android.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavouriteTrackRepository {
    suspend fun addToFavourite(track: Track)
    suspend fun removeFromFavourite(track: Track)
    fun getFavourite(): Flow<List<Track>>
}