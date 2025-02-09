package com.example.android.playlistmaker

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setButtonActions()
    }

    private fun setButtonActions() {
        setBackButtonAction()
    }

    private fun setBackButtonAction() {
        val button = findViewById<Button>(R.id.backButton)
        button.setOnClickListener(object: View.OnClickListener {
            override fun onClick(view: View) {
                finish()
            }
        })
    }
}