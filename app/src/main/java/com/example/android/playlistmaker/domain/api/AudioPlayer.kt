package com.example.android.playlistmaker.domain.api

import com.example.android.playlistmaker.data.PlayerState

interface AudioPlayer {
    fun prepare(previewUrl: String)
    fun play()
    fun pause()
    fun release()
    fun isPlaying(): Boolean
    fun getCurrentPosition(): Int
    fun setOnPreparedListener(listener: () -> Unit)
    fun setOnCompletionListener(listener: () -> Unit)
    fun getPlayerState(): PlayerState
}



