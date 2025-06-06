package com.example.android.playlistmaker.ui.audio_player.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.android.playlistmaker.domain.models.Track
import androidx.core.view.WindowInsetsCompat.Type
import com.example.android.playlistmaker.ui.audio_player.screen_state.AudioPlayerScreenState
import com.example.android.playlistmaker.ui.audio_player.view_model.AudioPlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private val viewModel by viewModel<AudioPlayerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val track = intent?.let {
            Track(
                trackId = it.getStringExtra("trackId") ?: "Unknown Id",
                trackName = it.getStringExtra("trackName") ?: "Unknown Track",
                artistName = it.getStringExtra("artistName") ?: "Unknown Artist",
                trackTimeMillis = it.getStringExtra("trackDuration") ?: "",
                artworkUrl100 = it.getStringExtra("artworkUrl") ?: "",
                collectionName = it.getStringExtra("collectionName") ?: "Unknown Album",
                releaseDate = it.getStringExtra("releaseDate") ?: "Unknown Year",
                primaryGenreName = it.getStringExtra("primaryGenreName") ?: "Unknown Genre",
                country = it.getStringExtra("country") ?: "Unknown Country",
                previewUrl = it.getStringExtra("previewUrl") ?: ""
            )
        }

        track?.let { viewModel.setTrackData(it) }

        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.toolbarPlayer.setOnClickListener { finish() }

        binding.playButton.setOnClickListener {
            viewModel.playbackControl()

        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

    }

    private fun setupObservers() {
        viewModel.trackData.observe(this) { data ->
            updateUI(data)
        }

        viewModel.audioPlayerScreenState.observe(this) { state ->
            updatePlayerState(state)
        }
    }

    private fun updateUI(data: Track) {
        binding.trackName.text = data.trackName
        binding.artistName.text = data.artistName
        binding.trackDurationValue.text = data.getFormattedDuration()
        binding.collectionValue.text = data.collectionName
        binding.releaseDateValue.text = data.getReleaseYear()
        binding.genreValue.text = data.primaryGenreName
        binding.countryValue.text = data.country

        Glide.with(this)
            .load(data.getCoverArtwork())
            .placeholder(R.drawable.track_avatar)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(binding.trackImage)
    }

    private fun updatePlayerState(state: AudioPlayerScreenState) {
        when (state) {
            is AudioPlayerScreenState.Playing -> {
                binding.playButton.setImageResource(R.drawable.pause_button)
                binding.timePlay.text = state.currentPosition
            }
            is AudioPlayerScreenState.Paused -> {
                binding.playButton.setImageResource(R.drawable.play_button)
            }
            is AudioPlayerScreenState.Prepared -> {
                binding.playButton.setImageResource(R.drawable.play_button)
                binding.timePlay.text = "00:00"
            }
            else -> Unit
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.trackData.value?.let { data ->
            outState.putString("trackId", data.trackId)
            outState.putString("trackName", data.trackName)
            outState.putString("artistName", data.artistName)
            outState.putString("trackDuration", data.trackTimeMillis)
            outState.putString("artworkUrl", data.artworkUrl100)
            outState.putString("collectionName", data.collectionName)
            outState.putString("releaseDate", data.releaseDate)
            outState.putString("primaryGenreName", data.primaryGenreName)
            outState.putString("country", data.country)
            outState.putString("previewUrl", data.previewUrl)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val restoredTrack = Track(
            trackId = savedInstanceState.getString("trackId") ?: "Unknown Id",
            trackName = savedInstanceState.getString("trackName") ?: "Unknown Track",
            artistName = savedInstanceState.getString("artistName") ?: "Unknown Artist",
            trackTimeMillis = savedInstanceState.getString("trackDuration") ?: "",
            artworkUrl100 = savedInstanceState.getString("artworkUrl") ?: "",
            collectionName = savedInstanceState.getString("collectionName") ?: "Unknown Album",
            releaseDate = savedInstanceState.getString("releaseDate") ?: "Unknown Year",
            primaryGenreName = savedInstanceState.getString("primaryGenreName") ?: "Unknown Genre",
            country = savedInstanceState.getString("country") ?: "Unknown Country",
            previewUrl = savedInstanceState.getString("previewUrl") ?: ""
        )

        viewModel.setTrackData(restoredTrack)
    }
}

