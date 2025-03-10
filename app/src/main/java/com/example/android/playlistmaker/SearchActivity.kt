package com.example.android.playlistmaker

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.playlistmaker.App.Companion.PLAYLISTMAKER_PREF
import com.example.playlistmaker.R
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {
    private lateinit var inputEditText: EditText
    private lateinit var searchAdapter: TracksAdapter
    private lateinit var historyAdapter: TracksAdapter
    private lateinit var searchHistory: SearchHistory
    private lateinit var sharedPref: SharedPreferences
    private lateinit var recycler: RecyclerView

    private var searchTextValue: String = SEARCH_TEXT_VALUE

    companion object {
        const val SEARCH_TEXT_VALUE = ""
        const val SEARCH_TEXT_KEY = "SEARCH TEXT"
    }
    enum class CodeError {
        GOOD,
        NORESULT,
        BADCONNECTION
    }

    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(ITunesApi::class.java)
    private val trackList: MutableList<Track> = mutableListOf()
    private lateinit var errorPage: LinearLayout
    private lateinit var errorImage: ImageView
    private lateinit var errorText: TextView
    private lateinit var errorButton: Button
    private lateinit var searchHistoryHeader: TextView
    private lateinit var searchHistoryClearButton: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        inputEditText = findViewById(R.id.search_bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val clearButton = findViewById<ImageView>(R.id.clear_button)
        val backButton = findViewById<Toolbar>(R.id.toolbar)
        recycler = findViewById(R.id.tracks_list)
        errorPage = findViewById(R.id.placeholderMessage)
        errorImage = findViewById(R.id.placeholderImage)
        errorText = findViewById(R.id.placeholderText)
        errorButton = findViewById(R.id.updateButton)
        sharedPref = getSharedPreferences(PLAYLISTMAKER_PREF, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPref)
        searchHistory.getSavedHistory()
        searchHistoryClearButton = findViewById(R.id.clearStoryTracksButton)
        searchHistoryHeader = findViewById(R.id.search_history_header)

        recycler.layoutManager = LinearLayoutManager(this)
        searchAdapter = TracksAdapter { setTrack(it) }
        historyAdapter = TracksAdapter {
            setTrack(it)
        }
        recycler.adapter = searchAdapter

        backButton.setNavigationOnClickListener {
            finish()
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }

        searchHistoryClearButton.setOnClickListener {
            searchHistory.clearTrackHistory()
            historyAdapter.updateData(searchHistory.getHistoryTracks())
            inputEditText.clearFocus()
        }

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchHistory.getHistoryTracks().isNotEmpty()) {
                setHistoryVisibility(true)
            } else {
                setHistoryVisibility(false)
            }
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            inputEditText.clearFocus()
            errorPage.isVisible = false
            trackList.clear()
            searchAdapter.updateData(trackList)
        }

        errorButton.setOnClickListener {
            search()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                setHistoryVisibility(inputEditText.hasFocus() && s?.isEmpty() == true)
            }

            override fun afterTextChanged(s: Editable?) {
                searchTextValue = s.toString()

            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchTextValue)


    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchTextValue = savedInstanceState.getString(SEARCH_TEXT_KEY, SEARCH_TEXT_VALUE)
        inputEditText.setText(searchTextValue)


    }

    private fun search() {
        iTunesService.search(inputEditText.text.toString())
            .enqueue(object : Callback<SearchResponse> {
                override fun onResponse(
                    call: Call<SearchResponse>,
                    response: Response<SearchResponse>
                ) {

                    if (response.isSuccessful) {
                        val responseTracks = response.body()?.results
                        if (responseTracks.isNullOrEmpty()) {
                            showPlaceholder(CodeError.NORESULT)
                            searchHistoryHeader.visibility = View.GONE
                            searchHistoryClearButton.visibility = View.GONE
                            recycler.visibility = View.GONE

                        } else {
                            showPlaceholder(CodeError.GOOD)
                            responseTracks.let { searchAdapter.updateData(it) }
                            recycler.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                    showPlaceholder(CodeError.BADCONNECTION)
                }
            })
    }

    private fun showPlaceholder(code: CodeError) {
        when (code) {
            CodeError.GOOD -> errorPage.isVisible = false
            CodeError.NORESULT -> {
                trackList.clear()
                searchAdapter.updateData(trackList)
                errorPage.isVisible = true
                errorImage.setImageResource(R.drawable.ic_no_results)
                errorText.text = getString(R.string.nothing_found)
                errorButton.isVisible = false
            }

            CodeError.BADCONNECTION -> {
                trackList.clear()
                searchAdapter.updateData(trackList)
                errorPage.isVisible = true
                errorImage.setImageResource(R.drawable.ic_error)
                errorText.text = getString(R.string.something_went_wrong)
                errorButton.isVisible = true
            }

        }
    }

    private fun setTrack(track: Track) {
        val json = Gson().toJson(track)
        val trackIntent = Intent(this, PlayerActivity::class.java)
        trackIntent.putExtra("TRACK", json)
        startActivity(trackIntent)
        searchHistory.addTrackToHistory(track)
    }

    private fun setHistoryVisibility(isSearchFieldEmpty: Boolean) {
        if (isSearchFieldEmpty) {
            searchHistoryHeader.visibility = View.VISIBLE
            searchHistoryClearButton.visibility = View.VISIBLE
            historyAdapter.updateData(searchHistory.getHistoryTracks())
            recycler.adapter = historyAdapter
        } else {
            searchHistoryHeader.visibility = View.GONE
            searchHistoryClearButton.visibility = View.GONE
            historyAdapter.updateData(mutableListOf())
            recycler.adapter = searchAdapter
        }
    }


}