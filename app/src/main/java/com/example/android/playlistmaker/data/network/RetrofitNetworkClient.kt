package com.example.android.playlistmaker.data.network

import android.content.Context
import android.net.ConnectivityManager
import com.example.android.playlistmaker.data.NetworkClient
import com.example.android.playlistmaker.data.dto.Response
import com.example.android.playlistmaker.data.dto.SearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class RetrofitNetworkClient(
    private val itunesService: ITunesApiService,
    private val context: Context,
) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = -1 }
        }

        if (dto !is SearchRequest) {
            return Response().apply{ resultCode = 400 }
        }

        return withContext(Dispatchers.IO) {
            try {
                val response = itunesService.search(dto.expression)
                response.apply { resultCode = 200 }
            } catch (e: Exception) {
                Response().apply {
                    resultCode = when (e) {
                        is HttpException -> e.code()
                        is IOException -> -1
                        else -> 500
                    }
                }
            }
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo

        return networkInfo != null && networkInfo.isConnected
    }
}