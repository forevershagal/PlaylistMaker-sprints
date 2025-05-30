package com.example.android.playlistmaker.presentation.audio_player

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.android.playlistmaker.data.PlayerState
import com.example.playlistmaker.R
import com.example.android.playlistmaker.data.MediaPlayerManager
import com.example.android.playlistmaker.domain.api.AudioPlayer
import com.example.android.playlistmaker.presentation.search_track.SearchActivity
import java.util.Locale
import java.time.OffsetDateTime

class PlayerActivity : AppCompatActivity() {

    private val audioPlayer: AudioPlayer = MediaPlayerManager()
    private val mainHandler = Handler(Looper.getMainLooper())
    private var updateRunnable: Runnable? = null

    private lateinit var playButton: ImageView
    private lateinit var timePlay: TextView

    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var trackDurationValue: TextView
    private lateinit var collectionValue: TextView
    private lateinit var releaseDateValue: TextView
    private lateinit var genreValue: TextView
    private lateinit var countryValue: TextView
    private lateinit var trackImage: ImageView

    private lateinit var trackNameText: String
    private lateinit var artistNameText: String
    private var trackDurationMillis: Long = 0
    private lateinit var artworkUrl: String
    private lateinit var collectionName: String
    private lateinit var releaseDate: String
    private lateinit var primaryGenreName: String
    private lateinit var country: String
    private lateinit var previewUrl: String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        playButton = findViewById(R.id.play_button)
        timePlay = findViewById(R.id.time_play)

        trackName = findViewById(R.id.track_name)
        artistName = findViewById(R.id.artist_name)
        trackDurationValue = findViewById(R.id.track_duration_value)
        collectionValue = findViewById(R.id.collection_value)
        releaseDateValue = findViewById(R.id.release_date_value)
        genreValue = findViewById(R.id.genre_value)
        countryValue = findViewById(R.id.country_value)
        trackImage = findViewById(R.id.track_image)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_player)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Получение данных из Intent или savedInstanceState
        trackNameText = intent.getStringExtra("trackName") ?: "Unknown Track"
        artistNameText = intent.getStringExtra("artistName") ?: "Unknown Artist"
        val durationString = intent.getStringExtra("trackDuration") ?: "00:00"
        trackDurationMillis = parseTimeStringToMillis(durationString)
        artworkUrl = intent.getStringExtra("artworkUrl") ?: ""
        collectionName = intent.getStringExtra("collectionName") ?: "Unknown Album"
        releaseDate = intent.getStringExtra("releaseDate") ?: "Unknown Year"
        primaryGenreName = intent.getStringExtra("primaryGenreName") ?: "Unknown Genre"
        country = intent.getStringExtra("country") ?: "Unknown Country"
        previewUrl = intent.getStringExtra("previewUrl") ?: ""

        updateUI()

        audioPlayer.setOnPreparedListener {
            playButton.isEnabled = true
            updatePlayButtonIcon()
        }

        audioPlayer.setOnCompletionListener {
            stopUpdatingTime()
            timePlay.text = "00:00"
            updatePlayButtonIcon()
        }

        audioPlayer.prepare(previewUrl)

        playButton.setOnClickListener {
            when (audioPlayer.getPlayerState()) {
                PlayerState.PLAYING -> pausePlayer()
                PlayerState.PREPARED, PlayerState.PAUSED -> startPlayer()
                else -> audioPlayer.prepare(previewUrl)
            }
        }

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    private fun updateUI() {
        trackName.text = trackNameText
        artistName.text = artistNameText
        trackDurationValue.text = formatDuration(trackDurationMillis)
        collectionValue.text = collectionName
        releaseDateValue.text = try {
            OffsetDateTime.parse(releaseDate).year.toString()
        } catch (e: Exception) {
            releaseDate
        }
        genreValue.text = primaryGenreName
        countryValue.text = country

        Glide.with(this)
            .load(getCoverArtwork(artworkUrl))
            .placeholder(R.drawable.track_avatar)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .into(trackImage)
    }

    private fun getCoverArtwork(artworkUrl: String): String {
        return artworkUrl.replaceAfterLast('/', "512x512bb.jpg")
    }

    private fun formatDuration(durationMillis: Long): String {
        val seconds = durationMillis / 1000
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, remainingSeconds)
    }

    private fun startPlayer() {
        audioPlayer.play()
        updatePlayButtonIcon()
        startUpdatingTime()
    }

    private fun pausePlayer() {
        audioPlayer.pause()
        updatePlayButtonIcon()
        stopUpdatingTime()
    }

    private fun updatePlayButtonIcon() {
        val state = audioPlayer.getPlayerState()
        playButton.setImageResource(
            if (state == PlayerState.PLAYING) R.drawable.pause_button else R.drawable.play_button
        )
    }

    private fun startUpdatingTime() {
        updateRunnable = object : Runnable {
            override fun run() {
                if (audioPlayer.isPlaying()) {
                    val currentPosition = audioPlayer.getCurrentPosition()
                    timePlay.text = formatDuration(currentPosition.toLong())
                    mainHandler.postDelayed(this, 300)
                }
            }
        }
        mainHandler.post(updateRunnable!!)
    }

    private fun stopUpdatingTime() {
        updateRunnable?.let { mainHandler.removeCallbacks(it) }
    }

    private fun parseTimeStringToMillis(timeString: String): Long {
        val parts = timeString.split(":")
        return if (parts.size == 2) {
            val minutes = parts[0].toIntOrNull() ?: 0
            val seconds = parts[1].toIntOrNull() ?: 0
            ((minutes * 60 + seconds) * 1000).toLong()
        } else {
            0
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        stopUpdatingTime()
        audioPlayer.release()
        super.onDestroy()
    }
}
