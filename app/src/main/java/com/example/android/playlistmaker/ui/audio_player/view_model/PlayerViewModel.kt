package com.example.android.playlistmaker.ui.audio_player.view_model

import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.playlistmaker.ui.audio_player.screen_state.AudioPlayerScreenState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.os.Handler
import com.example.android.playlistmaker.domain.models.Track
import com.example.android.playlistmaker.domain.player.AudioPlayerInteractor


class AudioPlayerViewModel(
    private val audioPlayerInteractor: AudioPlayerInteractor
) : ViewModel() {

    private val _audioPlayerScreenState =
        MutableLiveData<AudioPlayerScreenState>(AudioPlayerScreenState.Default)
    val audioPlayerScreenState: LiveData<AudioPlayerScreenState> = _audioPlayerScreenState

    private val _trackData = MutableLiveData<Track>()
    val trackData: LiveData<Track> = _trackData

    private var mainThreadHandler: Handler? = null
    private var updateRunnable: Runnable? = null
    private var currentPosition = 0


    init {
        audioPlayerInteractor.setOnPreparedListener {
            _audioPlayerScreenState.postValue(AudioPlayerScreenState.Prepared)
        }
        audioPlayerInteractor.setOnCompletionListener {
            _audioPlayerScreenState.postValue(AudioPlayerScreenState.Prepared)
        }
    }

    fun setTrackData(data: Track) {
        _trackData.value = data
        audioPlayerInteractor.preparePlayer(data.previewUrl)
    }

    fun playbackControl() {
        when(_audioPlayerScreenState.value) {
            is AudioPlayerScreenState.Playing -> pausePlayer()
            is AudioPlayerScreenState.Prepared, is AudioPlayerScreenState.Paused -> startPlayer()
            else -> {}
        }
    }

    private fun startPlayer() {
        if (audioPlayerInteractor.isPrepared()) {
            audioPlayerInteractor.startPlayer()
            _audioPlayerScreenState.postValue(AudioPlayerScreenState.Playing(formatTime(0)))
            startUpdatingTime()
        }
    }

    fun pausePlayer() {
        if (audioPlayerInteractor.isPlaying()) {
            currentPosition = audioPlayerInteractor.getCurrentPosition()
            audioPlayerInteractor.pausePlayer()
            _audioPlayerScreenState.postValue(AudioPlayerScreenState.Paused)
            stopUpdatingTime()
        }
    }

    private fun startUpdatingTime() {
        stopUpdatingTime()

        if (mainThreadHandler == null) {
            mainThreadHandler = Handler(Looper.getMainLooper())
        }
        updateRunnable = object : Runnable {
            override fun run() {
                if (audioPlayerInteractor.isPlaying()) {
                    _audioPlayerScreenState.postValue(
                        AudioPlayerScreenState.Playing(formatTime(audioPlayerInteractor.getCurrentPosition()))
                    )
                    mainThreadHandler?.postDelayed(this, 300)
                }
            }
        }
        updateRunnable?.let { mainThreadHandler?.post(it) }
    }

    private fun stopUpdatingTime() {
        updateRunnable?.let { mainThreadHandler?.removeCallbacks(it) }
    }

    private fun formatTime(millis: Int): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(Date(millis.toLong()))
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayerInteractor.releasePlayer()
        stopUpdatingTime()
    }
}