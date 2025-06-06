package com.example.android.playlistmaker.ui.search.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.playlistmaker.app.Constants
import com.example.android.playlistmaker.app.extensions.isGone
import com.example.android.playlistmaker.app.extensions.isVisible
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.android.playlistmaker.domain.models.Track
import com.example.android.playlistmaker.ui.audio_player.activity.AudioPlayerActivity
import com.example.android.playlistmaker.ui.search.adapter.TrackAdapter
import com.example.android.playlistmaker.ui.search.screen_state.SearchScreenState
import com.example.android.playlistmaker.ui.search.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private lateinit var binding: ActivitySearchBinding
    private val viewModel by viewModel<SearchViewModel>()

    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true
    private var inputText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBarsInsets.left,
                systemBarsInsets.top,
                systemBarsInsets.right,
                systemBarsInsets.bottom
            )
            insets
        }


        setupAdapters()
        setupSearchInput()
        setupClickListeners()
        observeViewModel()

        savedInstanceState?.let {
            inputText = it.getString(Constants.SEARCH_TEXT_KEY)
            binding.inputEditText.setText(inputText)
        }
    }

    override fun onResume() {
        super.onResume()
        if (inputText.isNullOrEmpty()) {
            viewModel.loadSearchHistory()
        } else {
            viewModel.searchDebounce(inputText!!)
        }
    }

    private fun setupAdapters() {
        trackAdapter = TrackAdapter(emptyList()) { track ->
            if (clickDebounce()) onTrackClicked(track)
        }
        historyAdapter = TrackAdapter(emptyList()) { track ->
            if (clickDebounce()) onTrackClicked(track)
        }

        binding.trackList.layoutManager = LinearLayoutManager(this)
        binding.trackList.adapter = trackAdapter

        binding.historyRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.historyRecyclerView.adapter = historyAdapter
    }

    private fun setupSearchInput() {
        binding.inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                inputText = s?.toString()
                binding.clearIcon.visibility =
                    if (s.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE

                if (s.isNullOrEmpty()) {
                    viewModel.loadSearchHistory()
                } else {
                    viewModel.searchDebounce(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding.inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                true
            } else {
                false
            }
        }
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener { finish() }

        binding.clearHistory.setOnClickListener {
            viewModel.clearSearchHistory()
            binding.resultsHistoryLayout.isGone = true
        }

        binding.clearIcon.setOnClickListener {
            binding.inputEditText.text.clear()
            hideKeyboard()
            viewModel.loadSearchHistory()
        }

        binding.updateButton.setOnClickListener {
            if (inputText.isNullOrEmpty()) {
                viewModel.loadSearchHistory()
            } else {
                viewModel.searchDebounce(inputText!!, force = true)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.observeState().observe(this) { state ->
            renderState(state)
        }

        viewModel.observeShowToast().observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun renderState(state: SearchScreenState) {
        when (state) {
            is SearchScreenState.Loading -> {
                binding.progressBar.isVisible = true
                binding.trackList.isGone = true
                binding.noResultsLayout.isGone = true
                binding.noInternetLayout.isGone = true
                binding.resultsHistoryLayout.isGone = true
            }

            is SearchScreenState.Content -> {
                binding.progressBar.isGone = true
                binding.trackList.isVisible = true
                binding.noResultsLayout.isGone = true
                binding.noInternetLayout.isGone = true
                binding.resultsHistoryLayout.isGone = true

                trackAdapter.updateTracks(state.tracks)
            }

            is SearchScreenState.Empty -> {
                binding.progressBar.isGone = true
                binding.trackList.isGone = true
                binding.noResultsLayout.isVisible = true
                binding.noResultsText.text = state.message
                binding.noInternetLayout.isGone = true
                binding.resultsHistoryLayout.isGone = true
            }

            is SearchScreenState.Error -> {
                binding.progressBar.isGone = true
                binding.trackList.isGone = true
                binding.noResultsLayout.isGone = true
                binding.noInternetLayout.isVisible = true
                binding.resultsHistoryLayout.isGone = true
            }

            is SearchScreenState.History -> {
                binding.progressBar.isGone = true
                binding.trackList.isGone = true
                binding.noResultsLayout.isGone = true
                binding.noInternetLayout.isGone = true

                if (state.tracks.isNotEmpty()) {
                    binding.resultsHistoryLayout.isVisible = true
                    historyAdapter.updateTracks(state.tracks)
                } else {
                    binding.resultsHistoryLayout.isGone = true
                }
            }
        }
    }

    private fun onTrackClicked(track: Track) {
        viewModel.addToHistory(track)
        val intent = Intent(this, AudioPlayerActivity::class.java).apply {
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
        startActivity(intent)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.inputEditText.windowToken, 0)
        binding.inputEditText.clearFocus()
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
}