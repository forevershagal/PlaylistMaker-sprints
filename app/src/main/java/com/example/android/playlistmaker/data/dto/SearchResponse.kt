package com.example.android.playlistmaker.data.dto

data class SearchResponse (
    val resultCount: Int,
    var results: List<TrackDto>
): Response()