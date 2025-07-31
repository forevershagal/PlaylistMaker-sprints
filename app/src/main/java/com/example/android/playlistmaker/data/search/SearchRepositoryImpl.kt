package com.example.android.playlistmaker.data.search

import android.content.Context
import com.example.android.playlistmaker.app.Resource
import com.example.android.playlistmaker.app.TrackMapper
import com.example.android.playlistmaker.data.NetworkClient
import com.example.android.playlistmaker.data.dto.SearchRequest
import com.example.android.playlistmaker.data.dto.SearchResponse
import com.example.android.playlistmaker.domain.models.Track
import com.example.android.playlistmaker.domain.search.SearchRepository
import com.example.playlistmaker.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val trackMapper: TrackMapper,
    private val context: Context
) : SearchRepository {
    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        try {
            val response = networkClient.doRequest(SearchRequest(expression))

            when (response.resultCode) {
                -1 -> {
                    emit(Resource.Error(context.getString(R.string.error_network_connection)))
                }

                200 -> {
                    val trackList = (response as SearchResponse).results.map { trackDto ->
                        trackMapper.map(trackDto)
                    }
                    emit(Resource.Success(trackList))
                }

                else -> {
                    val errorMessage = context.getString(R.string.error_server, response.resultCode)
                    emit(Resource.Error(errorMessage))
                }
            }
        } catch (e: Exception) {
            emit(Resource.Error(context.getString(R.string.error_unknown)))
        }
    }
}