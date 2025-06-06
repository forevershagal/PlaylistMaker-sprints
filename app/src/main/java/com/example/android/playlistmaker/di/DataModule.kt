package com.example.android.playlistmaker.di

import android.content.Context
import com.example.android.playlistmaker.app.Constants
import com.example.android.playlistmaker.app.TrackMapper
import com.example.android.playlistmaker.data.NetworkClient
import com.example.android.playlistmaker.data.locale.SearchHistoryStorage
import com.example.android.playlistmaker.data.locale.SearchHistoryStorageImpl
import com.example.android.playlistmaker.data.main.impl.MainExternalNavigatorImpl
import com.example.android.playlistmaker.data.network.ITunesApiService
import com.example.android.playlistmaker.data.network.RetrofitNetworkClient
import com.example.android.playlistmaker.data.network.SearchHistoryRepositoryImpl
import com.example.android.playlistmaker.data.search.SearchRepositoryImpl
import com.example.android.playlistmaker.data.settings.impl.SettingsRepositoryImpl
import com.example.android.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import com.example.android.playlistmaker.domain.api.SearchHistoryRepository
import com.example.android.playlistmaker.domain.main.MainExternalNavigator
import com.example.android.playlistmaker.domain.search.SearchRepository
import com.example.android.playlistmaker.domain.settings.SettingsRepository
import com.example.android.playlistmaker.domain.sharing.ExternalNavigator
import com.example.android.playlistmaker.data.utils.impl.NetworkCheckerImpl
import com.example.android.playlistmaker.domain.utils.NetworkChecker
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<ITunesApiService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApiService::class.java)
    }

    single {
        androidContext()
            .getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    factory { Gson() }

    single {
        TrackMapper()
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }

    single<SearchHistoryStorage> {
        SearchHistoryStorageImpl(get(), get())
    }

    single<MainExternalNavigator> {
        MainExternalNavigatorImpl(androidContext())
    }

    single<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(
            storage = get(),
            maxHistorySize = 10
        )
    }

    single<SearchRepository> {
        SearchRepositoryImpl(get(), get())
    }

    single<NetworkChecker> {
        NetworkCheckerImpl(androidContext())
    }
}
