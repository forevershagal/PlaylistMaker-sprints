package com.example.android.playlistmaker.domain.sharing

import com.example.android.playlistmaker.domain.sharing.model.EmailData

interface SharingInteractor {
    fun shareApp(shareMessage: String)
    fun openTerms(termsUrl: String)
    fun openSupport(emailData: EmailData)
}