package com.example.android.playlistmaker.di

import com.example.android.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.android.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.example.android.playlistmaker.domain.media_library.MediaLibraryInteractor
import com.example.android.playlistmaker.domain.db.favourite_track.FavouriteTrackInteractor
import com.example.android.playlistmaker.domain.db.playlist.PlaylistInteractor
import com.example.android.playlistmaker.domain.impl.FavouriteTrackInteractorImpl
import com.example.android.playlistmaker.domain.impl.PlaylistInteractorImpl
import com.example.android.playlistmaker.domain.player.AudioPlayerInteractor
import com.example.android.playlistmaker.domain.search.SearchInteractor
import com.example.android.playlistmaker.domain.search.impl.SearchInteractorImpl
import com.example.android.playlistmaker.domain.settings.SettingsInteractor
import com.example.android.playlistmaker.domain.settings.impl.SettingsInteractorImpl
import com.example.android.playlistmaker.domain.sharing.SharingInteractor
import com.example.android.playlistmaker.domain.sharing.impl.SharingInteractorImpl
import org.koin.dsl.module




val interactorModule = module {
    single<SearchInteractor> {
        SearchInteractorImpl(
            repository = get(),
            historyInteractor = get()
        )
    }

    single<SearchHistoryInteractor> {
        SearchHistoryInteractorImpl(
            repository = get()
        )
    }


    single<AudioPlayerInteractor> {
        AudioPlayerInteractor(
            repository = get()
        )
    }


    single<SettingsInteractor> {
        SettingsInteractorImpl(
            settingsRepository = get()
        )
    }

    single<SharingInteractor> {
        SharingInteractorImpl(
            externalNavigator = get()
        )
    }

    single<MediaLibraryInteractor> {
        MediaLibraryInteractor(get())
    }

    single<FavouriteTrackInteractor> {
        FavouriteTrackInteractorImpl(get())
    }

    single<PlaylistInteractor> {
        PlaylistInteractorImpl(get(),get())
    }
}