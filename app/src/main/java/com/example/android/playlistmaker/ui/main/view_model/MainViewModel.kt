package com.example.android.playlistmaker.ui.main.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.playlistmaker.creator.Creator
import com.example.android.playlistmaker.domain.main.MainExternalNavigator
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

    companion object {

        fun getViewModelFactory(externalNavigator: MainExternalNavigator): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MainViewModel(
                        Creator.provideMainInteractor(externalNavigator)
                    ) as T
                }
            }
        }
    }
}