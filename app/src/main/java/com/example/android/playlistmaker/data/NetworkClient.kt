package com.example.android.playlistmaker.data

import com.example.android.playlistmaker.data.dto.Response

interface NetworkClient {
    fun doRequest(dto: Any): Response
}