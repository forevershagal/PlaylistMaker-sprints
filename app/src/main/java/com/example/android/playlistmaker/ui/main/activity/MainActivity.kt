package com.example.android.playlistmaker.ui.main.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.android.playlistmaker.ui.main.view_model.MainViewModel
import com.example.android.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        setStatusBar()

        viewModel = ViewModelProvider(
            this,
            MainViewModel.getViewModelFactory(Creator.provideMainExternalNavigator(this))
        )[MainViewModel::class.java]

        setupViews()
    }

    private fun setupViews() = with(binding) {
        searchButton.setOnClickListener{ viewModel.searchButton() }
        libraryButton.setOnClickListener { viewModel.mediaLibraryButton() }
        settingsButton.setOnClickListener{ viewModel.settingsButton() }
    }


}
