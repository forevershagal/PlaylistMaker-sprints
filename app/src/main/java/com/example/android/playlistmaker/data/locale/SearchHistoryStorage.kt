package com.example.android.playlistmaker.data.locale

import com.example.android.playlistmaker.domain.models.Track

interface SearchHistoryStorage{
    fun saveHistory(tracks: List<Track>)
    fun getHistory(): List<Track>
    fun clearHistory()
}