package com.example.android.playlistmaker.domain.search

sealed class ErrorType {
    data object NoInternet : ErrorType()
    data object ServerError : ErrorType()
}