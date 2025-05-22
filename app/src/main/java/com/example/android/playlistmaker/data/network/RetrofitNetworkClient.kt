package com.example.android.playlistmaker.data.network

import com.example.android.playlistmaker.data.NetworkClient
import com.example.android.playlistmaker.data.dto.Response
import com.example.android.playlistmaker.data.dto.SearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ITunesApiService::class.java)


    override fun doRequest(dto: Any): Response {
        try {
            if (dto !is SearchRequest) {
                return Response().apply { resultCode = 400 }
            }

            val resp = itunesService.search(dto.expression).execute()

            val body = resp.body()
            return if (resp.isSuccessful && body != null) {
                body.apply {
                    resultCode = resp.code()
                }
            } else {
                Response().apply {
                    resultCode = resp.code()
                }
            }
        } catch (e: Exception) {
            return Response().apply { resultCode = -1 }
        }
    }
    }
