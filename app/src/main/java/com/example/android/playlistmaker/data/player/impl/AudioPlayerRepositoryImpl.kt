package com.example.android.playlistmaker.data.player.impl

import android.media.MediaPlayer
import com.example.android.playlistmaker.domain.player.AudioPlayerRepository

class AudioPlayerRepositoryImpl(
    private val mediaPlayer: MediaPlayer
) : AudioPlayerRepository {
    private var prepared = false
    private var onPreparedListener: (() -> Unit)? = null
    private var onCompletionListener: (() -> Unit)? = null

    override fun preparePlayer(url: String) {
        releasePlayer()
        mediaPlayer.apply {
            setDataSource(url)
            setOnPreparedListener {
                prepared = true
                onPreparedListener?.invoke()
            }
            setOnCompletionListener {
                onCompletionListener?.invoke()
            }
            prepareAsync()
        }
    }

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }

    override fun releasePlayer() {
        mediaPlayer.apply{
            reset()
        }
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

    override fun seekTo(position: Int) {
        mediaPlayer.seekTo(position)
    }

    override fun getDuration(): Int {
        return mediaPlayer.duration
    }
}