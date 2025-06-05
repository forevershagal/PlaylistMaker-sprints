package com.example.android.playlistmaker.data.main.impl

import android.content.Context
import android.content.Intent
import com.example.android.playlistmaker.domain.main.MainExternalNavigator
import com.example.android.playlistmaker.ui.search.activity.SearchActivity
import com.example.android.playlistmaker.ui.settings.activity.SettingsActivity
import com.example.android.playlistmaker.ui.media_library.LibraryActivity

class MainExternalNavigatorImpl  (
    private val context: Context
) : MainExternalNavigator {
    override fun openSearch() {
        val searchIntent = Intent(context, SearchActivity::class.java)
        context.startActivity(searchIntent)
    }

    override fun openMediaLibrary() {
        val mediaLibraryIntent = Intent(context, LibraryActivity::class.java)
        context.startActivity(mediaLibraryIntent)
    }

    override fun openSettings() {
        val settingsIntent = Intent(context, SettingsActivity::class.java)
        context.startActivity(settingsIntent)
    }
}