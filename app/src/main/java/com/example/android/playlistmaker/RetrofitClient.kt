package com.example.android.playlistmaker

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://itunes.apple.com"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS) // Тайм-аут соединения
        .readTimeout(3, TimeUnit.SECONDS)    // Тайм-аут чтения
        .writeTimeout(3, TimeUnit.SECONDS)   // Тайм-аут записи
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val iTunesService: ITunesAPI = retrofit.create(ITunesAPI::class.java)
}