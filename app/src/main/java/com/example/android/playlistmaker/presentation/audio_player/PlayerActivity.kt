package com.example.android.playlistmaker.presentation.audio_player

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.playlistmaker.R
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.util.Date
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()
    private var prepared = false

    private lateinit var timePlay: TextView
    private var mainThreadHandler: Handler? = null
    private var updateRunnable: Runnable? = null

    private lateinit var toolbarPlayer: androidx.appcompat.widget.Toolbar
    private lateinit var playButton: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var trackDuration: TextView
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

    @SuppressLint("WrongViewCast", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        toolbarPlayer = findViewById(R.id.toolbar_player)
        playButton = findViewById(R.id.play_button)
        timePlay = findViewById(R.id.time_play)
        mainThreadHandler = Handler(Looper.getMainLooper())

        mediaPlayer = MediaPlayer().apply {
            setOnPreparedListener {
                prepared = true
                playButton.isEnabled = true
                playerState = STATE_PREPARED
                updatePlayButtonIcon()
            }
            setOnCompletionListener {
                playerState = STATE_PREPARED
                stopUpdatingTime()
                timePlay.text = "00:00"
                updatePlayButtonIcon()
            }
        }

        // Инициализация TextView
        trackName = findViewById(R.id.track_name)
        artistName = findViewById(R.id.artist_name)
        trackDurationValue = findViewById(R.id.track_duration_value)
        collectionValue = findViewById(R.id.collection_value)
        releaseDateValue = findViewById(R.id.release_date_value)
        genreValue = findViewById(R.id.genre_value)
        countryValue = findViewById(R.id.country_value)
        trackImage = findViewById(R.id.track_image)

        // Получение данных из Intent
        trackNameText = intent.getStringExtra("trackName") ?: "Unknown Track"
        artistNameText = intent.getStringExtra("artistName") ?: "Unknown Artist"
//      rackDurationMillis = intent.getStringExtra("trackDuration")?.toLongOrNull() ?: 0
        val durationString = intent.getStringExtra("trackDuration") ?: "00:00"
        trackDurationMillis = parseTimeStringToMillis(durationString)
        artworkUrl = intent.getStringExtra("artworkUrl") ?: ""
        collectionName = intent.getStringExtra("collectionName") ?: "Unknown Album"
        releaseDate = intent.getStringExtra("releaseDate") ?: "Unknown Year"
        primaryGenreName = intent.getStringExtra("primaryGenreName") ?: "Unknown Genre"
        country = intent.getStringExtra("country") ?: "Unknown Country"
        previewUrl = intent.getStringExtra("previewUrl") ?: ""

        Log.d("PlayerDebug", "Data from Intent -> " +
                "Track: $trackNameText, " +
                "Artist: $artistNameText, " +
                "Duration (ms): $trackDurationMillis, " +
                "Artwork: $artworkUrl"
        )

        Log.d("PlayerDebug", "Intent extras: ${intent.extras?.keySet()?.joinToString()}")
        Log.d("PlayerDebug", "trackDuration value: ${intent.getStringExtra("trackDuration")}")

        updateUI()
        preparePlayer()

        playButton.setOnClickListener {
            playbackControl()
        }

        toolbarPlayer.setNavigationOnClickListener {
            finish()
        }
    }

    private fun updateUI() {
        trackName.text = trackNameText
        artistName.text = artistNameText
        trackDurationValue.text = formatDuration(trackDurationMillis)
        collectionValue.text = collectionName
        releaseDateValue.text = OffsetDateTime.parse(releaseDate).year.toString()
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("trackName", trackNameText)
        outState.putString("artistName", artistNameText)
        outState.putLong("trackDuration", trackDurationMillis)
        outState.putString("artworkUrl", artworkUrl)
        outState.putString("collectionName", collectionName)
        outState.putString("releaseDate", releaseDate)
        outState.putString("primaryGenreName", primaryGenreName)
        outState.putString("country", country)
        outState.putString("previewUrl", previewUrl)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        trackNameText = savedInstanceState.getString("trackName") ?: "Unknown Track"
        artistNameText = savedInstanceState.getString("artistName") ?: "Unknown Artist"
        trackDurationMillis = savedInstanceState.getLong("trackDuration", 0)

        artworkUrl = savedInstanceState.getString("artworkUrl") ?: ""
        collectionName = savedInstanceState.getString("collectionName") ?: "Unknown Album"
        releaseDate = savedInstanceState.getString("releaseDate") ?: "Unknown Year"
        primaryGenreName = savedInstanceState.getString("primaryGenreName") ?: "Unknown Genre"
        country = savedInstanceState.getString("country") ?: "Unknown Country"
        previewUrl = savedInstanceState.getString("previewUrl") ?: ""
        updateUI()
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        stopUpdatingTime()
        updateRunnable?.let { mainThreadHandler?.removeCallbacks(it) }
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = STATE_PREPARED
            updatePlayButtonIcon()
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            stopUpdatingTime()
            timePlay.text = "00:00"
            updatePlayButtonIcon()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        updatePlayButtonIcon()
        createUpdateTimeRunnable()
        updateRunnable?.let { mainThreadHandler?.post(it) }
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        updatePlayButtonIcon()
        stopUpdatingTime()
    }

    private fun updatePlayButtonIcon() {
        playButton.setImageResource(
            when (playerState) {
                STATE_PLAYING -> R.drawable.pause_button // Убедитесь, что у вас есть этот drawable
                else -> R.drawable.play_button
            }
        )
    }

    private fun createUpdateTimeRunnable() {
        updateRunnable = object : Runnable {
            override fun run() {
                if (mediaPlayer.isPlaying) {
                    val currentPosition = mediaPlayer.currentPosition
                    val formattedTime = SimpleDateFormat("mm:ss", Locale.getDefault()).format(Date(currentPosition.toLong()))
                    timePlay.text = formattedTime
                    mainThreadHandler?.postDelayed(this, 300)
                }
            }
        }
    }

    private fun stopUpdatingTime() {
        updateRunnable?.let { mainThreadHandler?.removeCallbacks(it) }
    }

    private fun parseTimeStringToMillis(timeString: String): Long {
        val parts = timeString.split(":")
        if (parts.size == 2) {
            val minutes = parts[0].toInt()
            val seconds = parts[1].toInt()
            return ((minutes * 60 + seconds) * 1000).toLong()
        }
        return 0
    }
}


