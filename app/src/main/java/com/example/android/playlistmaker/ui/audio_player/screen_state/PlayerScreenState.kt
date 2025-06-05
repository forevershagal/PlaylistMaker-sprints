package com.example.android.playlistmaker.ui.audio_player.screen_state

sealed class AudioPlayerScreenState {
    data object Default : AudioPlayerScreenState()
    data object Prepared : AudioPlayerScreenState()
    data class Playing(val currentPosition: String) : AudioPlayerScreenState()
    data object Paused : AudioPlayerScreenState()
}