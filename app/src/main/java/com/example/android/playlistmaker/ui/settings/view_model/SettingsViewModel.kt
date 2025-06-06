package com.example.android.playlistmaker.ui.settings.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.playlistmaker.domain.settings.SettingsInteractor
import com.example.android.playlistmaker.domain.settings.model.ThemeSettings
import com.example.android.playlistmaker.domain.sharing.SharingInteractor
import com.example.android.playlistmaker.domain.sharing.model.EmailData

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : ViewModel() {

    private val _themeLiveData = MutableLiveData<ThemeSettings>()
    val themeLiveData: LiveData<ThemeSettings> = _themeLiveData

    init {
        _themeLiveData.value = settingsInteractor.getThemeSettings()
    }

    fun updateThemeSettings(enabled: Boolean) {
        val newSettings = ThemeSettings(enabled)
        settingsInteractor.updateThemeSetting(newSettings)
        _themeLiveData.value = newSettings
    }

    fun shareApp(shareMessage: String) {
        sharingInteractor.shareApp(shareMessage)
    }

    fun openTerms(termsUrl: String) {
        sharingInteractor.openTerms(termsUrl)
    }

    fun openSupport(emailData: EmailData) {
        sharingInteractor.openSupport(emailData)
    }

}
