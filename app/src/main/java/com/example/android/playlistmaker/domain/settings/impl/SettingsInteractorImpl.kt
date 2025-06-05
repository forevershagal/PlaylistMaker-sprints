package com.example.android.playlistmaker.domain.settings.impl

import com.example.android.playlistmaker.domain.settings.SettingsInteractor
import com.example.android.playlistmaker.domain.settings.model.ThemeSettings
import com.example.android.playlistmaker.domain.settings.SettingsRepository

class SettingsInteractorImpl(
    private val settingsRepository: SettingsRepository
) : SettingsInteractor {

    override fun getThemeSettings(): ThemeSettings {
        return ThemeSettings(
            isDarkTheme = settingsRepository.isDarkThemeEnabled()
        )
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        settingsRepository.setDarkThemeEnabled(settings.isDarkTheme)
    }
}