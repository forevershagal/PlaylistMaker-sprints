package com.example.android.playlistmaker.data.favourite.impl

import com.example.android.playlistmaker.data.converter.FavouriteTrackDbConvertor
import com.example.android.playlistmaker.data.db.AppDatabase
import com.example.android.playlistmaker.domain.db.FavouriteTrackRepository
import com.example.android.playlistmaker.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class FavouriteTrackRepositoryImpl (
    private val appDatabase: AppDatabase,
    private val favouriteTrackDbConvertor: FavouriteTrackDbConvertor,
) : FavouriteTrackRepository {


    override suspend fun addToFavourite(track: Track) {
        withContext(Dispatchers.IO) {
            val existingIds = appDatabase.favouriteTrackDao().getFavouriteId().first()
            if (existingIds.contains(track.trackId)) {
                appDatabase.favouriteTrackDao().removeById(track.trackId)
            }
            appDatabase.favouriteTrackDao().addToFavourite(favouriteTrackDbConvertor.map(track))
        }
    }


    override suspend fun removeFromFavourite(track: Track) {
        withContext(Dispatchers.IO) {
            appDatabase.favouriteTrackDao()
                .removeFromFavourite(favouriteTrackDbConvertor.map(track))
        }
    }

    override fun getFavourite(): Flow<List<Track>> {
        return  appDatabase.favouriteTrackDao().getFavourite().map { list ->
            list.map { favouriteTrackDbConvertor.map(it) }
        }
    }

}