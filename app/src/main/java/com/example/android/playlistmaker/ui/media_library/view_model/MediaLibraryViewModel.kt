package com.example.android.playlistmaker.ui.media_library.view_model

import androidx.lifecycle.ViewModel
import com.example.playlistmaker.R
import com.example.android.playlistmaker.ui.media_library.TabData

class MediaLibraryViewModel : ViewModel() {

    fun getTabsData(): List<TabData> {
        return listOf(
            TabData(R.string.favourites, R.layout.fragment_favourite_tracks),
            TabData(R.string.playlists, R.layout.fragment_playlist)
        )
    }
}
