package com.example.android.playlistmaker.domain.settings

import com.example.android.playlistmaker.domain.settings.model.ThemeSettings

interface SettingsInteractor {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(settings: ThemeSettings)
}