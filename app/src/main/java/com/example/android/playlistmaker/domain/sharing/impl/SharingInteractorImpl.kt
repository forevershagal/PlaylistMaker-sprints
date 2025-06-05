package com.example.android.playlistmaker.domain.sharing.impl

import com.example.android.playlistmaker.domain.sharing.ExternalNavigator
import com.example.android.playlistmaker.domain.sharing.SharingInteractor
import com.example.android.playlistmaker.domain.sharing.model.EmailData

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {

    override fun shareApp(shareMessage: String) {
        externalNavigator.shareLink(shareMessage)
    }

    override fun openTerms(termsUrl: String) {
        externalNavigator.openLink(termsUrl)
    }

    override fun openSupport(emailData: EmailData) {
        externalNavigator.openEmail(emailData)
    }
}