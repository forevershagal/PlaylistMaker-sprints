package com.example.android.playlistmaker.app

sealed interface ToastState {
    object None: ToastState
    data class Show(val additionalMessage: String): ToastState
}