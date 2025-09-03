package com.example.android.playlistmaker.di

import android.content.Context
import androidx.room.Room
import com.example.android.playlistmaker.app.Constants
import com.example.android.playlistmaker.app.TrackMapper
import com.example.android.playlistmaker.data.NetworkClient
import com.example.android.playlistmaker.data.db.AppDatabase
import com.example.android.playlistmaker.data.locale.SearchHistoryStorage
import com.example.android.playlistmaker.data.locale.SearchHistoryStorageImpl
import com.example.android.playlistmaker.data.network.ITunesApiService
import com.example.android.playlistmaker.data.network.RetrofitNetworkClient
import com.example.android.playlistmaker.data.network.SearchHistoryRepositoryImpl
import com.example.android.playlistmaker.data.playlist.impl.PlaylistRepositoryImpl
import com.example.android.playlistmaker.data.search.SearchRepositoryImpl
import com.example.android.playlistmaker.data.settings.impl.SettingsRepositoryImpl
import com.example.android.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import com.example.android.playlistmaker.domain.api.SearchHistoryRepository
import com.example.android.playlistmaker.domain.search.SearchRepository
import com.example.android.playlistmaker.domain.settings.SettingsRepository
import com.example.android.playlistmaker.domain.sharing.ExternalNavigator
import com.example.android.playlistmaker.data.utils.impl.NetworkCheckerImpl
import com.example.android.playlistmaker.domain.db.playlist.PlaylistInteractor
import com.example.android.playlistmaker.domain.db.playlist.PlaylistRepository
import com.example.android.playlistmaker.domain.impl.PlaylistInteractorImpl
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
        RetrofitNetworkClient(get(), get())
    }

    single<SearchHistoryStorage> {
        SearchHistoryStorageImpl(get(), get())
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
        SearchRepositoryImpl(get(), get(), get(), get())
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    single { get<AppDatabase>().playlistDao() }
    single { get<AppDatabase>().favouriteTrackDao() }


    single<PlaylistRepository> { PlaylistRepositoryImpl(get(),get()) }
    single<PlaylistInteractor> { PlaylistInteractorImpl(get(),get()) }

    single<NetworkChecker> {
        NetworkCheckerImpl(androidContext())
    }
}

