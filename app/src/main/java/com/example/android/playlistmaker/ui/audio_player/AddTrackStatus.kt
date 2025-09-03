package com.example.android.playlistmaker.ui.audio_player

sealed class AddTrackStatus {
    class Success (val playlistName: String) : AddTrackStatus()
    data class AlreadyExists(val playlistName: String) : AddTrackStatus()
    class Error(val message: String) : AddTrackStatus()
}