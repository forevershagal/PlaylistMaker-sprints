package com.example.android.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {
    lateinit var sharedPrefs: SharedPreferences
    private var darkTheme = false
    companion object {
        const val PLAYLISTMAKER_PREF = "switchTheme"
        const val NIGHT_MODE = "themeMode"
    }

    override fun onCreate() {
        super.onCreate()
        sharedPrefs = getSharedPreferences(PLAYLISTMAKER_PREF, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(NIGHT_MODE, false)
        switchTheme(darkTheme)

    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        sharedPrefs.edit()
            .putBoolean(NIGHT_MODE, darkTheme)
            .apply()

    }

}
