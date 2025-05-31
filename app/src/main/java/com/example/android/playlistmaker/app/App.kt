package com.example.android.playlistmaker.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.android.playlistmaker.Creator
import com.example.android.playlistmaker.domain.api.ThemeInteractor

class App : Application() {

    private lateinit var themeInteractor: ThemeInteractor

    override fun onCreate() {
        super.onCreate()

        // Инициализация Creator с application context
        Creator.init(this)

        // Получаем ThemeInteractor
        themeInteractor = Creator.provideThemeInteractor()

        // Получаем сохраненное состояние темы из интерактора
        val darkTheme = themeInteractor.isDarkThemeEnabled()

        // Применяем тему
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        // Сохраняем состояние темы через интерактор
        themeInteractor.setDarkThemeEnabled(darkThemeEnabled)

        // Применяем тему
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
