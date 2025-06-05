package com.example.android.playlistmaker.creator

import android.content.Context
import com.example.android.playlistmaker.data.locale.SearchHistoryStorageImpl
import com.example.android.playlistmaker.data.network.RetrofitNetworkClient
import com.example.android.playlistmaker.app.TrackMapper
import com.example.android.playlistmaker.data.locale.SearchHistoryStorage
import com.example.android.playlistmaker.data.main.impl.MainExternalNavigatorImpl
import com.example.android.playlistmaker.data.network.SearchHistoryRepositoryImpl
import com.example.android.playlistmaker.data.player.impl.AudioPlayerRepositoryImpl
import com.example.android.playlistmaker.data.search.SearchRepositoryImpl
import com.example.android.playlistmaker.data.settings.impl.SettingsRepositoryImpl
import com.example.android.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import com.example.android.playlistmaker.data.utils.impl.NetworkCheckerImpl
import com.example.android.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.android.playlistmaker.domain.api.SearchHistoryRepository
import com.example.android.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.android.playlistmaker.domain.main.MainExternalNavigator
import com.example.android.playlistmaker.domain.main.MainInteractor
import com.example.android.playlistmaker.domain.main.impl.MainInteractorImpl
import com.example.android.playlistmaker.domain.player.AudioPlayerInteractor
import com.example.android.playlistmaker.domain.player.AudioPlayerRepository
import com.example.android.playlistmaker.domain.search.SearchInteractor
import com.example.android.playlistmaker.domain.search.SearchRepository
import com.example.android.playlistmaker.domain.search.impl.SearchInteractorImpl
import com.example.android.playlistmaker.domain.settings.SettingsInteractor
import com.example.android.playlistmaker.domain.settings.SettingsRepository
import com.example.android.playlistmaker.domain.settings.impl.SettingsInteractorImpl
import com.example.android.playlistmaker.domain.sharing.ExternalNavigator
import com.example.android.playlistmaker.domain.sharing.SharingInteractor
import com.example.android.playlistmaker.domain.sharing.impl.SharingInteractorImpl
import com.example.android.playlistmaker.domain.utils.NetworkChecker
import com.google.gson.Gson

object Creator {

    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun getTrackMapper(): TrackMapper = TrackMapper()

    fun provideSearchInteractor(context: Context): SearchInteractor {
        return SearchInteractorImpl(
            repository = getSearchRepository(),
            historyInteractor = provideSearchHistoryInteractor(context),
            networkChecker = provideNetworkChecker(context)
        )
    }

    private fun getSearchRepository(): SearchRepository {
        return SearchRepositoryImpl(
            networkClient = RetrofitNetworkClient(),
            trackMapper = getTrackMapper()
        )
    }

    private fun getSearchHistoryStorage(context: Context): SearchHistoryStorage {
        return SearchHistoryStorageImpl(
            context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE),
            Gson()
        )
    }

    private fun provideSearchHistoryRepository(context: Context): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(
            getSearchHistoryStorage(context),
            maxHistorySize = 10
        )
    }

    fun provideSearchHistoryInteractor(context: Context): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(
            provideSearchHistoryRepository(context)
        )
    }

    fun provideMainExternalNavigator(context: Context): MainExternalNavigator {
        return MainExternalNavigatorImpl(context)
    }

    fun provideMainInteractor(externalNavigator: MainExternalNavigator): MainInteractor {
        return MainInteractorImpl(externalNavigator)
    }

    private fun provideAudioPlayerRepository(): AudioPlayerRepository {
        return AudioPlayerRepositoryImpl()
    }

    fun provideAudioPlayerInteractor(): AudioPlayerInteractor {
        return AudioPlayerInteractor(provideAudioPlayerRepository())
    }

    private fun provideSettingsRepository(context: Context): SettingsRepository {
        return SettingsRepositoryImpl(
            context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        )
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(
            provideSettingsRepository(context)
        )
    }

    private fun provideExternalNavigator(context: Context): ExternalNavigator {
        return ExternalNavigatorImpl(context)
    }

    fun provideSharingInteractor(context: Context): SharingInteractor {
        return SharingInteractorImpl(
            externalNavigator = provideExternalNavigator(context)
        )
    }

    fun provideThemeSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(
            SettingsRepositoryImpl(
                appContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            )
        )
    }

    private fun provideNetworkChecker(context: Context): NetworkChecker {
        return NetworkCheckerImpl(context)
    }
}
