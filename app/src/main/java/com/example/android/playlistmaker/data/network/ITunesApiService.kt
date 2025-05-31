package com.example.android.playlistmaker.data.network

import com.example.android.playlistmaker.data.dto.SearchResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ITunesApiService {
    @GET("search")
    fun search(
        @Query("term") text: String,
        @Query("entity") entity: String = "song"
    ): Call<SearchResponse>
}