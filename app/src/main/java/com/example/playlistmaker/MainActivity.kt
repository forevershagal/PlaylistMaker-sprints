package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.android.playlistmaker.LibraryActivity
import com.example.android.playlistmaker.SearchActivity
import com.example.android.playlistmaker.SettingsActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        setButtonActions()
    }

    private fun setButtonActions() {
        setSearchButtonAction()
        setLibButtonAction()
        setSettingsButtonAction()
    }

    private fun setSearchButtonAction() {
        val button = findViewById<Button>(R.id.search_button)
        button.setOnClickListener(object: View.OnClickListener {
            override fun onClick(view: View) {
                val searchIntent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(searchIntent)
            }
        })
    }

    private fun setLibButtonAction() {
        val button = findViewById<Button>(R.id.button_library)
        button.setOnClickListener {
            val libraryIntent = Intent(this, LibraryActivity::class.java)
            startActivity(libraryIntent)
        }
    }

    private fun setSettingsButtonAction() {
        val button = findViewById<Button>(R.id.button_settings)
        button.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
    }
}
