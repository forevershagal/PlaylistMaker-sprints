package com.example.android.playlistmaker

import android.content.Context
import com.example.android.playlistmaker.data.locale.SearchHistoryStorageImpl
import com.example.android.playlistmaker.data.network.RetrofitNetworkClient
import com.example.android.playlistmaker.data.network.SearchHistoryRepositoryImpl
import com.example.android.playlistmaker.data.network.TracksRepositoryImpl
import com.example.android.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.android.playlistmaker.domain.api.ThemeInteractor
import com.example.android.playlistmaker.domain.api.TracksInteractor
import com.example.android.playlistmaker.domain.api.TracksRepository
import com.example.android.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.android.playlistmaker.domain.impl.ThemeInteractorImpl
import com.example.android.playlistmaker.domain.impl.TracksInteractorImpl
import com.example.android.playlistmaker.presentation.TrackMapper
import com.google.gson.Gson

object Creator {

    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

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

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        val repository = getSearchHistoryRepository()
        return SearchHistoryInteractorImpl(repository)
    }

    fun provideThemeInteractor(): ThemeInteractor {
        val sharedPreferences = appContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return ThemeInteractorImpl(sharedPreferences)
    }

    private fun getSearchHistoryRepository(): SearchHistoryRepositoryImpl {
        return SearchHistoryRepositoryImpl(getSearchHistoryStorage())
    }

    private fun getSearchHistoryStorage(): SearchHistoryStorageImpl {
        val sharedPreferences = appContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return SearchHistoryStorageImpl(sharedPreferences, Gson())
    }
}
