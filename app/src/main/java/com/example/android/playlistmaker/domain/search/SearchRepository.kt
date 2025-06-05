package com.example.android.playlistmaker.domain.search

import com.example.android.playlistmaker.app.Resource
import com.example.android.playlistmaker.domain.models.Track

interface SearchRepository {
    fun searchTracks(expression: String): Resource<List<Track>>
}