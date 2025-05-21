package com.example.android.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.Group
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val REFRESH_SECONDS_VALUE_MILLIS = 300L
    }

    private lateinit var toolbar: Toolbar
    private lateinit var trackImage: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var trackDuration: TextView
    private lateinit var collection: TextView
    private lateinit var releaseDate: TextView
    private lateinit var genre: TextView
    private lateinit var country: TextView
    private lateinit var constraintGroup: Group
    private lateinit var playTrackButton: ImageView
    private lateinit var trackProgress: TextView

    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private var mainThreadHandler: Handler? = null
    private val timerRunnable: Runnable = Runnable { refreshTrackTimer() }
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    private lateinit var track: Track

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        initPlayer()
        setListeners()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar_player)
        trackImage = findViewById(R.id.track_image)
        trackName = findViewById(R.id.track_name)
        artistName = findViewById(R.id.artist_name)
        trackDuration = findViewById(R.id.track_duration_value)
        collection = findViewById(R.id.collection_value)
        releaseDate = findViewById(R.id.release_date_value)
        genre = findViewById(R.id.genre_value)
        country = findViewById(R.id.country_value)
        constraintGroup = findViewById(R.id.info_group)
        playTrackButton = findViewById(R.id.play_button)
        trackProgress = findViewById(R.id.time_play)

        mainThreadHandler = Handler(Looper.getMainLooper())

        // Устанавливаем начальное значение прогресса
        trackProgress.text = dateFormat.format(0)
    }

    private fun initPlayer() {
        val json = intent.getStringExtra("TRACK")
        track = Gson().fromJson(json, Track::class.java)

        displayTrackInfo()

        // Подготавливаем плеер к воспроизведению
        preparePlayer(track.previewUrl)
    }

    private fun displayTrackInfo() {
        Glide.with(applicationContext)
            .load(track.getCoverArtwork())
            .transform(CenterCrop(), RoundedCorners(16))
            .placeholder(R.drawable.track_avatar)
            .into(trackImage)

        trackName.text = track.trackName
        artistName.text = track.artistName
        trackDuration.text = dateFormat.format(track.trackTimeMillis)

        if (track.collectionName.isEmpty()) {
            constraintGroup.visibility = View.GONE
        } else {
            collection.text = track.collectionName
        }

        releaseDate.text = track.releaseDate.substring(0, 4)
        genre.text = track.primaryGenreName
        country.text = track.country
    }

    private fun setListeners() {
        toolbar.setNavigationOnClickListener {
            finish()
        }

        playTrackButton.setOnClickListener {
            playbackControl()
        }
    }

    private fun preparePlayer(trackUrl: String) {
        mediaPlayer.setDataSource(trackUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playTrackButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playTrackButton.setImageResource(R.drawable.play_button)
            playerState = STATE_PREPARED
            mainThreadHandler?.removeCallbacks(timerRunnable)
            trackProgress.text = dateFormat.format(0)
        }
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playTrackButton.setImageResource(R.drawable.pause_button)
        playerState = STATE_PLAYING
        mainThreadHandler?.post(timerRunnable)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        mainThreadHandler?.removeCallbacks(timerRunnable)
        playTrackButton.setImageResource(R.drawable.play_button)
        playerState = STATE_PAUSED
    }

    private fun refreshTrackTimer() {
        if (playerState == STATE_PLAYING) {
            trackProgress.text = dateFormat.format(mediaPlayer.currentPosition)
            mainThreadHandler?.postDelayed(timerRunnable, REFRESH_SECONDS_VALUE_MILLIS)
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        mainThreadHandler?.removeCallbacks(timerRunnable)
    }
}