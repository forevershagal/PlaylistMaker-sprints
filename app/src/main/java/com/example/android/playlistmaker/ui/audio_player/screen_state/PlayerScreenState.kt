package com.example.android.playlistmaker.ui.audio_player.screen_state

sealed class AudioPlayerScreenState(
    val isPlayButtonEnabled: Boolean,
    val progress: String
) {
    data object Default : AudioPlayerScreenState(false, "00:00")
    data object Prepared : AudioPlayerScreenState(true, "00:00")
    data class Playing(val currentPosition: String) : AudioPlayerScreenState(true, currentPosition)
    data class Paused(val currentPosition: String) : AudioPlayerScreenState(true, currentPosition)
}