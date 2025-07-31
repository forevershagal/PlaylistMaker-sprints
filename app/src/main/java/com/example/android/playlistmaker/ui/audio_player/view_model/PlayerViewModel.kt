package com.example.android.playlistmaker.ui.audio_player.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.playlistmaker.ui.audio_player.screen_state.AudioPlayerScreenState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.os.Handler
import androidx.lifecycle.viewModelScope
import com.example.android.playlistmaker.domain.models.Track
import com.example.android.playlistmaker.domain.player.AudioPlayerInteractor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class AudioPlayerViewModel(
    private val audioPlayerInteractor: AudioPlayerInteractor
) : ViewModel() {

    private val _audioPlayerScreenState =
        MutableLiveData<AudioPlayerScreenState>(AudioPlayerScreenState.Default)
    val audioPlayerScreenState: LiveData<AudioPlayerScreenState> = _audioPlayerScreenState

    private val _trackData = MutableLiveData<Track>()
    val trackData: LiveData<Track> = _trackData

    private var currentPosition = 0
    private var timerJob: Job? = null


    init {
        audioPlayerInteractor.setOnPreparedListener {
            _audioPlayerScreenState.postValue(AudioPlayerScreenState.Prepared)
        }
        audioPlayerInteractor.setOnCompletionListener {
            stopUpdatingTime()
            audioPlayerInteractor.seekTo(0)
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
            if (audioPlayerInteractor.getCurrentPosition() >= audioPlayerInteractor.getDuration()) {
                audioPlayerInteractor.seekTo(0)
            }
            audioPlayerInteractor.startPlayer()
            startUpdatingTime()
        }
    }

    fun pausePlayer() {
        if (audioPlayerInteractor.isPlaying()) {
            currentPosition = audioPlayerInteractor.getCurrentPosition()
            audioPlayerInteractor.pausePlayer()
            _audioPlayerScreenState.postValue(AudioPlayerScreenState.Paused(formatTime(currentPosition)))
            stopUpdatingTime()
        }
    }

    private fun startUpdatingTime() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var currentPosition = audioPlayerInteractor.getCurrentPosition()
            _audioPlayerScreenState.postValue(
                AudioPlayerScreenState.Playing(formatTime(currentPosition))
            )

            while(audioPlayerInteractor.isPlaying()) {
                delay(TIMER_DEBOUNCE_DELAY)
                currentPosition = audioPlayerInteractor.getCurrentPosition()
                _audioPlayerScreenState.postValue(
                    AudioPlayerScreenState.Playing(formatTime(currentPosition))
                )
            }
        }
    }

    private fun stopUpdatingTime() {
        timerJob?.cancel()
    }

    private fun formatTime(millis: Int): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(Date(millis.toLong()))
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayerInteractor.releasePlayer()
        stopUpdatingTime()
    }

    companion object {
        private const val TIMER_DEBOUNCE_DELAY = 300L
    }
}