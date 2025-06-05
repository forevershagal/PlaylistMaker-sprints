package com.example.android.playlistmaker.data.search

import com.example.android.playlistmaker.app.Resource
import com.example.android.playlistmaker.app.TrackMapper
import com.example.android.playlistmaker.data.NetworkClient
import com.example.android.playlistmaker.data.dto.SearchRequest
import com.example.android.playlistmaker.data.dto.SearchResponse
import com.example.android.playlistmaker.domain.models.Track
import com.example.android.playlistmaker.domain.search.SearchRepository

class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val trackMapper: TrackMapper
) : SearchRepository {

    override fun searchTracks(expression: String): Resource<List<Track>> {
        return try {
            val response = networkClient.doRequest(SearchRequest(expression))

            val tracksList = (response as? SearchResponse)?.results?.map { trackDto ->
                trackMapper.map(trackDto)
            }.orEmpty()

            Resource.Success(tracksList)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }
}