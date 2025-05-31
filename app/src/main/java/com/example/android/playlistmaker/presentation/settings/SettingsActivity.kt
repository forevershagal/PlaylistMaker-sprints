package com.example.android.playlistmaker.presentation.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import com.example.playlistmaker.R
import androidx.core.net.toUri
import com.example.android.playlistmaker.app.Constants
import com.example.android.playlistmaker.presentation.main.PLMakerActivityWithToolbar
import com.example.android.playlistmaker.Creator
import com.example.android.playlistmaker.app.App
import com.example.android.playlistmaker.domain.api.ThemeInteractor

class SettingsActivity : PLMakerActivityWithToolbar() {

    private lateinit var switchThemeButton: SwitchCompat
    private lateinit var themeInteractor: ThemeInteractor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setStatusBar()
        setToolbar()
        setButtonActions()

        // Получаем интерактор темы из Creator
        themeInteractor = Creator.provideThemeInteractor()

        switchThemeButton = findViewById(R.id.switchThemeButton)

        // Восстановление состояния темы из интерактора
        switchThemeButton.isChecked = themeInteractor.isDarkThemeEnabled()

        // Обработка переключения темы — вызываем интерактор
        switchThemeButton.setOnCheckedChangeListener { _, checked ->
            themeInteractor.setDarkThemeEnabled(checked)
            (applicationContext as App).switchTheme(checked)
            recreate()
        }
    }

    override fun setButtonActions() {
        setShareAction()
        setSupportAction()
        showUserAgreementAction()
    }

    private fun setShareAction() {
        val button = findViewById<LinearLayout>(R.id.share_button)
        val url = getString(R.string.practicum_url)

        button.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, url)
            }

            startActivity(intent)
        }
    }

    private fun setSupportAction() {
        val button = findViewById<LinearLayout>(R.id.support_button)
        val email = getString(R.string.target_email)
        val subject = getString(R.string.mail_subject)
        val body = getString(R.string.mail_body)
        val clientNotFoundError = getString(R.string.mail_client_not_found)

        button.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
            }

            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, clientNotFoundError, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showUserAgreementAction() {
        val button = findViewById<LinearLayout>(R.id.user_agreement_button)
        val url = getString(R.string.practicum_offer)

        button.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            startActivity(intent)
        }
    }
}
