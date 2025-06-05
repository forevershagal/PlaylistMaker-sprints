package com.example.android.playlistmaker.domain.sharing

import com.example.android.playlistmaker.domain.sharing.model.EmailData

interface ExternalNavigator {
    fun shareLink(link: String)
    fun openLink(url: String)
    fun openEmail(emailData: EmailData)
}