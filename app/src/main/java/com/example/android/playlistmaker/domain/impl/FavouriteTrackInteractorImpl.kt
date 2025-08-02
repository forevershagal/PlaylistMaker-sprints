package com.example.android.playlistmaker.domain.impl

import com.example.android.playlistmaker.domain.db.FavouriteTrackInteractor
import com.example.android.playlistmaker.domain.db.FavouriteTrackRepository
import com.example.android.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavouriteTrackInteractorImpl (
    private val repository: FavouriteTrackRepository
) : FavouriteTrackInteractor {

    override suspend fun toggleFavourite(track: Track) {
        if (track.isFavourite) {
            repository.removeFromFavourite(track)
        } else {
            repository.addToFavourite(track)
        }
    }

    override fun getFavourite(): Flow<List<Track>> {
        return repository.getFavourite()
    }

    override suspend  fun removeFromFavourite(track: Track) {
        repository.removeFromFavourite(track)
    }
}