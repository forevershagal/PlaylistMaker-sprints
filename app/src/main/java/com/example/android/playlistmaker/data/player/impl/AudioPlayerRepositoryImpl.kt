package com.example.android.playlistmaker.data.player.impl

import android.media.MediaPlayer
import com.example.android.playlistmaker.domain.player.AudioPlayerRepository

class AudioPlayerRepositoryImpl : AudioPlayerRepository {
    private var mediaPlayer: MediaPlayer = MediaPlayer()
    private var prepared = false
    private var onPreparedListener: (() -> Unit)? = null
    private var onCompletionListener: (() -> Unit)? = null

    override fun preparePlayer(url: String) {
        releasePlayer()
        mediaPlayer.setDataSource(url)
        mediaPlayer.setOnPreparedListener {
            prepared = true
            onPreparedListener?.invoke()
        }
        mediaPlayer.setOnCompletionListener {
            onCompletionListener?.invoke()
        }
        mediaPlayer.prepareAsync()
    }

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }

    override fun releasePlayer() {
        mediaPlayer.reset()
        prepared = false
    }

    override fun getCurrentPosition(): Int = mediaPlayer.currentPosition

    override fun isPlaying(): Boolean = mediaPlayer.isPlaying

    override fun isPrepared(): Boolean = prepared

    override fun setOnPreparedListener(listener: () -> Unit) {
        onPreparedListener = listener
    }

    override fun setOnCompletionListener(listener: () -> Unit) {
        onCompletionListener = listener
    }
}