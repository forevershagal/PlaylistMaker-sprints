package com.example.android.playlistmaker.domain.media_library

import com.example.android.playlistmaker.domain.models.TabData

class MediaLibraryInteractor(private val repository: MediaLibraryRepository) {
    fun getTabsData(): List<TabData> = repository.getTabData()
}