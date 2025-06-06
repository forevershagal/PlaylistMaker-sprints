package com.example.android.playlistmaker.ui.main.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.android.playlistmaker.ui.main.view_model.MainViewModel
import com.example.playlistmaker.databinding.ActivityMainBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
    }

    private fun setupViews() = with(binding) {
        searchButton.setOnClickListener{ viewModel.searchButton() }
        libraryButton.setOnClickListener { viewModel.mediaLibraryButton() }
        settingsButton.setOnClickListener{ viewModel.settingsButton() }
    }


}
