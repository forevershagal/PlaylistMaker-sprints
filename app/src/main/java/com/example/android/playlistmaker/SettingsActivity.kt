package com.example.android.playlistmaker

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.google.android.material.appbar.MaterialToolbar

class SettingsActivity : PLMakerActivityWithToolbar() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setStatusBar()
        setToolbar()
        setButtonActions()
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
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }
}