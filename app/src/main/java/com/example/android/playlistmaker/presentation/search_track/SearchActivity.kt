package com.example.android.playlistmaker.presentation.search_track

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.android.playlistmaker.app.Constants
import com.example.android.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.android.playlistmaker.domain.api.TracksInteractor
import com.example.android.playlistmaker.domain.models.Track
import com.example.android.playlistmaker.presentation.audio_player.PlayerActivity
import com.example.android.playlistmaker.presentation.extensions.isGone
import com.example.android.playlistmaker.presentation.extensions.isVisible


class SearchActivity : AppCompatActivity() {
    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    private var currentQuery: String? = null

    private val searchRunnable = Runnable {
        val query = currentQuery?.trim()
        if (!query.isNullOrEmpty()) {
            performSearch(query)
        }
    }

    private lateinit var inputEditText: EditText
    private var inputText: String? = null
    private lateinit var backButton: ImageView
    private lateinit var clearIcon: ImageView
    private lateinit var clearHistory: com.google.android.material.button.MaterialButton
    private lateinit var trackList: RecyclerView
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var updateButton: Button
    private var lastSearchQuery: String? = null
    private lateinit var noResultsLayout: ConstraintLayout
    private lateinit var noInternetLayout: ConstraintLayout
    private lateinit var resultsHistoryLayout: LinearLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var tracksInteractor: TracksInteractor
    private lateinit var searchHistoryInteractor: SearchHistoryInteractor

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // UI
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        resultsHistoryLayout = findViewById(R.id.resultsHistoryLayout)
        backButton = findViewById(R.id.back_button)
        clearHistory = findViewById(R.id.clear_history)
        Log.d("SearchActivity", "Initial button state:")
        Log.d("SearchActivity", "Button found: ${::clearHistory.isInitialized}")
        Log.d("SearchActivity", "Button visibility: ${clearHistory.visibility}")

        inputEditText = findViewById(R.id.inputEditText)
        clearIcon = findViewById(R.id.clearIcon)
        trackList = findViewById(R.id.trackList)
        updateButton = findViewById(R.id.update_button)
        noInternetLayout = findViewById(R.id.noInternetLayout)
        noResultsLayout = findViewById(R.id.noResultsLayout)
        progressBar = findViewById(R.id.progressBar)

        // Интеракторы
        tracksInteractor = Creator.provideTracksInteractor()
        searchHistoryInteractor = Creator.provideSearchHistoryInteractor()

        // Адаптеры
        trackAdapter = TrackAdapter(emptyList(), ::onTrackClicked)
        historyAdapter = TrackAdapter(emptyList(), ::onTrackClicked)

        trackList.layoutManager = LinearLayoutManager(this)
        trackList.adapter = trackAdapter

        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter

        // Инициализация истории
        setupHistory()

        backButton.setOnClickListener { finish() }

        clearHistory.setOnClickListener {
            searchHistoryInteractor.clearHistory()
            historyAdapter.updateTracks(emptyList())
            resultsHistoryLayout.isVisible = false
            clearHistory.isGone = true
        }

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                inputText = s.toString()
                currentQuery = inputText
                clearIcon.visibility = if (s.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE
                updateUI()
                searchDebounce()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        inputEditText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                inputEditText.clearFocus()
                val query = inputEditText.text.toString().trim()
                if (query.isNotEmpty()) {
                    performSearch(query)
                }
                true
            } else {
                false
            }
        }

        clearIcon.setOnClickListener {
            inputEditText.text.clear()
            inputEditText.clearFocus()
            trackList.isGone = true
            clearIcon.visibility = View.INVISIBLE
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            updateUI()
        }

        updateButton.setOnClickListener {
            lastSearchQuery?.let { query -> performSearch(query) }
        }

        inputEditText.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(inputEditText, InputMethodManager.SHOW_IMPLICIT)

        savedInstanceState?.let {
            inputText = it.getString(Constants.SEARCH_TEXT_KEY)
            inputEditText.setText(inputText)
        }

        updateUI()
    }

    override fun onResume() {
        super.onResume()
        inputEditText.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(inputEditText, InputMethodManager.SHOW_IMPLICIT)
        setupHistory() // Обновляем историю при возвращении в активность
    }

    private fun updateUI() {
        val query = inputEditText.text.toString().trim()
        Log.d("SearchActivity", "=== updateUI called with query: '$query' ===")

        if (query.isEmpty()) {
            trackList.isGone = true
            noResultsLayout.isGone = true
            noInternetLayout.isGone = true
            progressBar.isGone = true

            val history = searchHistoryInteractor.getHistory()
            Log.d("SearchActivity", "updateUI - history size: ${history.size}")

            if (history.isNotEmpty()) {
                resultsHistoryLayout.isVisible = true
                historyAdapter.updateTracks(history)
                clearHistory.isVisible = true
                Log.d("SearchActivity", "updateUI - set button visible")
            } else {
                resultsHistoryLayout.isVisible = false
                clearHistory.isGone = true
                Log.d("SearchActivity", "updateUI - set button gone")
            }
        } else {
            resultsHistoryLayout.isGone = true
            clearHistory.isGone = true
            Log.d("SearchActivity", "updateUI - query not empty, hiding button")
        }
    }

    private fun performSearch(query: String) {
        if (query.isBlank()) return

        lastSearchQuery = query
        trackAdapter.updateTracks(emptyList())

        trackList.isGone = true
        noInternetLayout.isGone = true
        noResultsLayout.isGone = true
        resultsHistoryLayout.isGone = true
        progressBar.isVisible = true

        tracksInteractor.searchTracks(query, object : TracksInteractor.TrackConsumer {
            override fun consume(foundTracks: List<Track>) {
                runOnUiThread {
                    progressBar.isGone = true
                    if (foundTracks.isNotEmpty()) {
                        trackAdapter.updateTracks(foundTracks)
                        trackList.isVisible = true
                        noResultsLayout.isGone = true
                        noInternetLayout.isGone = true
                    } else {
                        trackList.isGone = true
                        noResultsLayout.isVisible = true
                    }
                }
            }

            override fun onError(error: Throwable) {
                runOnUiThread {
                    progressBar.isGone = true
                    trackList.isGone = true
                    noResultsLayout.isGone = true
                    noInternetLayout.isVisible = true
                }
            }
        })
    }

    private fun onTrackClicked(track: Track) {
        if (clickDebounce()) {
            searchHistoryInteractor.addTrack(track)
            setupHistory()

            val playerActivity = Intent(this, PlayerActivity::class.java).apply {
                putExtra("trackName", track.trackName)
                putExtra("artistName", track.artistName)
                putExtra("trackDuration", track.trackTimeMillis)
                putExtra("artworkUrl", track.artworkUrl100)
                putExtra("collectionName", track.collectionName)
                putExtra("releaseDate", track.releaseDate)
                putExtra("primaryGenreName", track.primaryGenreName)
                putExtra("country", track.country)
                putExtra("previewUrl", track.previewUrl)
            }
            startActivity(playerActivity)
        }
    }

    private fun setupHistory() {
        val history = searchHistoryInteractor.getHistory()
        Log.d("SearchActivity", "setupHistory - history size: ${history.size}")
        if (history.isNotEmpty()) {
            resultsHistoryLayout.isVisible = true
            historyAdapter.updateTracks(history)
            clearHistory.isVisible = true
            Log.d("SearchActivity", "setupHistory - set button visible")
        } else {
            resultsHistoryLayout.isGone = true
            clearHistory.isGone = true
            Log.d("SearchActivity", "setupHistory - set button gone")
        }
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(Constants.SEARCH_TEXT_KEY, inputText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputText = savedInstanceState.getString(Constants.SEARCH_TEXT_KEY)
        inputEditText.setText(inputText)
    }
}