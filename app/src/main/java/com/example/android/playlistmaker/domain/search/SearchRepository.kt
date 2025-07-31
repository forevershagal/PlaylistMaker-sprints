package com.example.android.playlistmaker.domain.search

import com.example.android.playlistmaker.app.Resource
import com.example.android.playlistmaker.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun searchTracks(expression: String): Flow<Resource<List<Track>>>
}