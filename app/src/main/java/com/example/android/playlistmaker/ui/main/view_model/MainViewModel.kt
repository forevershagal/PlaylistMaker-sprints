package com.example.android.playlistmaker.ui.main.view_model

import androidx.lifecycle.ViewModel
import com.example.android.playlistmaker.domain.main.MainInteractor

class MainViewModel (
    private val mainInteractor: MainInteractor,
) : ViewModel () {
    fun searchButton() {
        mainInteractor.searchButton()
    }

    fun mediaLibraryButton() {
        mainInteractor.mediaLibraryButton()
    }

    fun settingsButton() {
        mainInteractor.settingsButton()
    }
}