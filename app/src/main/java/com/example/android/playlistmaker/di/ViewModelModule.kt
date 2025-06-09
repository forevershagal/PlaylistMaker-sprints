package com.example.android.playlistmaker.di

import com.example.android.playlistmaker.ui.audio_player.view_model.AudioPlayerViewModel
import com.example.android.playlistmaker.ui.main.view_model.MainViewModel
import com.example.android.playlistmaker.ui.media_library.fragments.favourite_fragment.FavouriteViewModel
import com.example.android.playlistmaker.ui.media_library.fragments.playlist_fragment.PlaylistViewModel
import com.example.android.playlistmaker.ui.media_library.view_model.MediaLibraryViewModel
import com.example.android.playlistmaker.ui.search.view_model.SearchViewModel
import com.example.android.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        AudioPlayerViewModel(
            audioPlayerInteractor = get()
        )
    }

    viewModel {
        MainViewModel(
            mainInteractor = get()
        )
    }

    viewModel {
        SearchViewModel(
            application = androidApplication(),
            interactor = get(),
            historyInteractor = get()
        )
    }

    viewModel {
        SettingsViewModel(
            sharingInteractor = get(),
            settingsInteractor = get()
        )
    }

    viewModel {
        MediaLibraryViewModel()
    }

    viewModel {
        FavouriteViewModel()
    }

    viewModel {
        PlaylistViewModel()
    }
}