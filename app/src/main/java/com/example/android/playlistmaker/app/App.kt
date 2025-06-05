package com.example.android.playlistmaker.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.android.playlistmaker.creator.Creator
import com.example.android.playlistmaker.domain.settings.SettingsInteractor

class App : Application() {

    private lateinit var settingsInteractor: SettingsInteractor

    override fun onCreate() {
        super.onCreate()

        // Инициализация Creator с application context
        Creator.init(this)

        // Получаем SettingsInteractor
        settingsInteractor = Creator.provideThemeSettingsInteractor()

        // Получаем сохраненное состояние темы из интерактора
        val darkTheme = settingsInteractor.getThemeSettings().isDarkTheme

        // Применяем тему
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        // Сохраняем состояние темы через интерактор
        settingsInteractor.updateThemeSetting(
            settings = com.example.android.playlistmaker.domain.settings.model.ThemeSettings(darkThemeEnabled)
        )

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
