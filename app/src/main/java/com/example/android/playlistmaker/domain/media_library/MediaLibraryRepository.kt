package com.example.android.playlistmaker.domain.media_library

import com.example.android.playlistmaker.domain.models.TabData

interface MediaLibraryRepository {
    fun getTabData(): List<TabData>
}