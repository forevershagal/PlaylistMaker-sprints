package com.example.android.playlistmaker.domain.impl

import android.content.SharedPreferences
import com.example.android.playlistmaker.domain.api.ThemeInteractor

class ThemeInteractorImpl(
    private val sharedPreferences: SharedPreferences
) : ThemeInteractor {

    companion object {
        private const val THEME_KEY = "dark_theme"
    }

    override fun isDarkThemeEnabled(): Boolean {
        return sharedPreferences.getBoolean(THEME_KEY, false)
    }

    override fun setDarkThemeEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(THEME_KEY, enabled).apply()
    }
}
