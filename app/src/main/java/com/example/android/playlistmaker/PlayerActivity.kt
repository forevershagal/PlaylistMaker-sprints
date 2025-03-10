package com.example.android.playlistmaker

import android.os.Bundle
import android.view.View
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
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


        toolbar.setNavigationOnClickListener {
            finish()
        }

        val json = intent.getStringExtra("TRACK")

        val track: Track = Gson().fromJson(json, Track::class.java)

        Glide.with(applicationContext)
            .load(track.getCoverArtwork())
            .transform(CenterCrop(), RoundedCorners(16))
            .placeholder(R.drawable.track_avatar)
            .into(trackImage)

        trackName.text = track.trackName
        artistName.text = track.artistName
        trackDuration.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis).toString()
        if (track.collectionName.isEmpty()) {
            constraintGroup.visibility = View.GONE
        } else {
            collection.text = track.collectionName
        }
        releaseDate.text = track.releaseDate.substring(0, 4)
        genre.text = track.primaryGenreName
        country.text = track.country


    }
}