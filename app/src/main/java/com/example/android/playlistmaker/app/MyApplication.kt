package com.example.android.playlistmaker.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.android.playlistmaker.di.dataModule
import com.example.android.playlistmaker.di.interactorModule
import com.example.android.playlistmaker.di.repositoryModule
import com.example.android.playlistmaker.di.viewModelModule
import com.example.android.playlistmaker.domain.settings.SettingsInteractor
import com.example.android.playlistmaker.domain.settings.model.ThemeSettings
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.get

class MyApplication : Application() {

    private lateinit var settingsInteractor: SettingsInteractor

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MyApplication)
            modules(dataModule, repositoryModule, interactorModule, viewModelModule)
        }

        settingsInteractor = get(SettingsInteractor::class.java)

        val isDarkTheme = settingsInteractor.getThemeSettings().isDarkTheme

        switchTheme(isDarkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        settingsInteractor.updateThemeSetting(ThemeSettings(darkThemeEnabled))

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
