package com.example.android.playlistmaker.domain.main.impl

import com.example.android.playlistmaker.domain.main.MainExternalNavigator
import com.example.android.playlistmaker.domain.main.MainInteractor

class MainInteractorImpl(
    private val mainExternalNavigator: MainExternalNavigator,
) : MainInteractor {

    override fun searchButton() {
        mainExternalNavigator.openSearch()
    }

    override fun mediaLibraryButton() {
        mainExternalNavigator.openMediaLibrary()
    }

    override fun settingsButton() {
        mainExternalNavigator.openSettings()
    }
}