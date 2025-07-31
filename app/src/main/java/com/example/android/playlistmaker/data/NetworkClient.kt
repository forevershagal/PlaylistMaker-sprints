package com.example.android.playlistmaker.data

import com.example.android.playlistmaker.data.dto.Response

interface NetworkClient {
    suspend fun doRequest(dto: Any): Response
}