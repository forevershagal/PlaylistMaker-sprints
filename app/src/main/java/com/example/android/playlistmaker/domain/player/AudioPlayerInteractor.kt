package com.example.android.playlistmaker.domain.player

class AudioPlayerInteractor(private val repository: AudioPlayerRepository) {

    fun preparePlayer(url: String) = repository.preparePlayer(url)
    fun startPlayer() = repository.startPlayer()
    fun pausePlayer() = repository.pausePlayer()
    fun releasePlayer() = repository.releasePlayer()
    fun getCurrentPosition() = repository.getCurrentPosition()
    fun isPlaying() = repository.isPlaying()
    fun isPrepared() = repository.isPrepared()

    fun setOnPreparedListener(listener: () -> Unit) {
        repository.setOnPreparedListener(listener)
    }

    fun setOnCompletionListener(listener: () -> Unit) {
        repository.setOnCompletionListener(listener)
    }
}