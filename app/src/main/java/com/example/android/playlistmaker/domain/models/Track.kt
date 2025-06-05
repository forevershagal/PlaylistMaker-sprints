package com.example.android.playlistmaker.domain.models


data class Track(
    val trackId: String,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String) {


    fun getFormattedDuration(): String {
        return trackTimeMillis
    }

    fun getCoverArtwork(): String {
        return artworkUrl100.replaceAfterLast('/', "512x512bb.jpg")
    }

    fun getReleaseYear(): String {
        return java.time.OffsetDateTime.parse(releaseDate).year.toString()
    }
}

