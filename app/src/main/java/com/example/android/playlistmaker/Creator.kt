package com.example.android.playlistmaker


import android.content.Context
import com.example.android.playlistmaker.data.locale.SearchHistoryStorageImpl
import com.example.android.playlistmaker.data.network.RetrofitNetworkClient
import com.example.android.playlistmaker.data.network.SearchHistoryRepositoryImpl
import com.example.android.playlistmaker.data.network.TracksRepositoryImpl
import com.example.android.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.android.playlistmaker.domain.api.TracksInteractor
import com.example.android.playlistmaker.domain.api.TracksRepository
import com.example.android.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.android.playlistmaker.domain.impl.TracksInteractorImpl
import com.example.android.playlistmaker.presentation.TrackMapper
import com.google.gson.Gson

object Creator {

    private fun getTrackMapper(): TrackMapper = TrackMapper()

    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(
            RetrofitNetworkClient(),
            getTrackMapper()
        )
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        val storage = SearchHistoryStorageImpl(
            context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE),
            Gson()
        )
        val repository = SearchHistoryRepositoryImpl(storage)
        return SearchHistoryInteractorImpl(repository)
    }
}
