package com.example.android.playlistmaker.data.settings.impl

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.android.playlistmaker.app.Constants
import com.example.android.playlistmaker.domain.settings.SettingsRepository

class SettingsRepositoryImpl(
    private val preferences: SharedPreferences
) : SettingsRepository {

    override fun isDarkThemeEnabled(): Boolean {
        return preferences.getBoolean(Constants.DARK_THEME, false)
    }

    override fun setDarkThemeEnabled(enabled: Boolean) {
        preferences.edit {
            putBoolean(Constants.DARK_THEME, enabled)
        }
    }
}