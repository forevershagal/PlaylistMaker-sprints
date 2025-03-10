package com.example.android.playlistmaker

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://itunes.apple.com"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.SECONDS) // Тайм-аут соединения
        .readTimeout(1, TimeUnit.SECONDS)    // Тайм-аут чтения
        .writeTimeout(1, TimeUnit.SECONDS)   // Тайм-аут записи
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val iTunesService: ITunesApi = retrofit.create(ITunesApi::class.java)
}