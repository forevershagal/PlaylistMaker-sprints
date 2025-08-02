package com.example.android.playlistmaker.domain.db

import com.example.android.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavouriteTrackInteractor {
    suspend fun toggleFavourite(track: Track)
    fun getFavourite(): Flow<List<Track>>
    suspend fun removeFromFavourite(track: Track)
}