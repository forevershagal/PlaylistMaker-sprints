package com.example.android.playlistmaker.domain.models

data class Playlist(
    val id: Long,
    val name: String,
    val description: String,
    val coverPath: String?,
    val trackCount: Int,
    val trackIds: String
)