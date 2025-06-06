package com.example.android.playlistmaker.di

import android.media.MediaPlayer
import com.example.android.playlistmaker.data.network.SearchHistoryRepositoryImpl
import com.example.android.playlistmaker.data.player.impl.AudioPlayerRepositoryImpl
import com.example.android.playlistmaker.data.search.SearchRepositoryImpl
import com.example.android.playlistmaker.data.settings.impl.SettingsRepositoryImpl
import com.example.android.playlistmaker.domain.api.SearchHistoryRepository
import com.example.android.playlistmaker.domain.player.AudioPlayerRepository
import com.example.android.playlistmaker.domain.search.SearchRepository
import com.example.android.playlistmaker.domain.settings.SettingsRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<SearchRepository> {
        SearchRepositoryImpl(get(), get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(
            storage = get(),
            maxHistorySize = 10
        )
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    single<MediaPlayer> {
        MediaPlayer()
    }

    single<AudioPlayerRepository> {
        AudioPlayerRepositoryImpl(get())
    }
}