package com.example.android.playlistmaker.data

import android.media.MediaPlayer
import com.example.android.playlistmaker.domain.api.AudioPlayer

enum class PlayerState {
    DEFAULT, PREPARED, PLAYING, PAUSED
}

class MediaPlayerManager: AudioPlayer {

    private var mediaPlayer: MediaPlayer? = null
    private var playerState: PlayerState = PlayerState.DEFAULT

    private var onPreparedListener: (() -> Unit)? = null
    private var onCompletionListener: (() -> Unit)? = null

    override fun setOnPreparedListener(listener: () -> Unit) {
        onPreparedListener = listener
    }

    override fun setOnCompletionListener(listener: () -> Unit) {
        onCompletionListener = listener
    }

    override fun prepare(url: String) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener {
                playerState = PlayerState.PREPARED
                onPreparedListener?.invoke()
            }
            setOnCompletionListener {
                playerState = PlayerState.PREPARED
                onCompletionListener?.invoke()
            }
            prepareAsync()
        }
        playerState = PlayerState.DEFAULT
    }

    override fun play() {
        mediaPlayer?.start()
        playerState = PlayerState.PLAYING
    }

    override fun pause() {
        mediaPlayer?.pause()
        playerState = PlayerState.PAUSED
    }

    override fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        playerState = PlayerState.DEFAULT
    }

    override fun getCurrentPosition(): Int = mediaPlayer?.currentPosition ?: 0
    override fun isPlaying(): Boolean = mediaPlayer?.isPlaying ?: false

    override fun getPlayerState(): PlayerState = playerState
}
