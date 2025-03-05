package com.example.android.playlistmaker

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.playlistmaker.MainActivity
import com.example.playlistmaker.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {
    private var editTextValue: String = TEXT_VALUE
    private var tracks = ArrayList<Track>()
    private val tracksAdapter = TracksAdapter()

    private lateinit var toolbarButton: Toolbar
    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var trackList: RecyclerView
    private lateinit var placeholderMessage: LinearLayout
    private lateinit var placeholderImage: ImageView
    private lateinit var placeholderText: TextView
    private lateinit var updateButton: Button

    companion object {
        const val TEXT_KEY = "TEXT_KEY"
        const val TEXT_VALUE = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        toolbarButton = findViewById(R.id.toolbar)
        inputEditText = findViewById(R.id.search_bar)
        clearButton = findViewById(R.id.clear_text)
        trackList = findViewById(R.id.tracks_list)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        placeholderImage = findViewById(R.id.placeholderImage)
        placeholderText = findViewById(R.id.placeholderText)
        updateButton = findViewById(R.id.updateButton)

        updateButton.visibility = View.GONE

        tracksAdapter.tracks = tracks

        trackList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        trackList.adapter = tracksAdapter

        // Отключение скроллинга программно
        inputEditText.movementMethod = null // <--- Добавлено для отключения скроллинга

        if (savedInstanceState != null) {
            editTextValue = savedInstanceState.getString(TEXT_KEY, TEXT_VALUE)
            inputEditText.setText(editTextValue)
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                editTextValue = s.toString()
                if (s.isNullOrEmpty()) {
                    showMessage("") // Скрыть заглушку, если текст пустой
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            hideSoftKeyboard(it)
            tracks.clear()
            showMessage("") // Добавлено для скрытия заглушки
        }

        toolbarButton.setNavigationOnClickListener {
            val returnIntent = Intent(this, MainActivity::class.java)
            startActivity(returnIntent)
            finish()
        }

        updateButton.setOnClickListener {
            search()
        }
    }

    private fun search() {
        if (!isInternetAvailable()) {
            showMessage(getString(R.string.something_went_wrong))
            return
        }

        RetrofitClient.iTunesService.search(inputEditText.text.toString())
            .enqueue(object : Callback<ITunesResponse> {
                override fun onResponse(
                    call: Call<ITunesResponse>, response: Response<ITunesResponse>
                ) {
                    if (response.code() == 200) {
                        tracks.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            tracks.addAll(response.body()?.results!!)
                            tracksAdapter.notifyDataSetChanged()
                            showMessage("")
                        } else {
                            showMessage(getString(R.string.nothing_found))
                        }
                    } else {
                        showMessage(getString(R.string.something_went_wrong))
                    }
                }

                override fun onFailure(call: Call<ITunesResponse>, t: Throwable) {
                    showMessage(getString(R.string.something_went_wrong))
                }
            })
    }

    private fun showMessage(text: String) {
        if (text.isNotEmpty()) {
            placeholderMessage.visibility = View.VISIBLE
            tracks.clear()
            tracksAdapter.notifyDataSetChanged()
            placeholderText.text = text
            when (text) {
                getString(R.string.something_went_wrong) -> {
                    placeholderImage.setImageResource(R.drawable.ic_error)
                    updateButton.visibility = View.VISIBLE
                }
                else -> {
                    placeholderImage.setImageResource(R.drawable.ic_no_results)
                    updateButton.visibility = View.GONE
                }
            }
        } else {
            placeholderMessage.visibility = View.GONE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TEXT_KEY, editTextValue)
    }

    private fun hideSoftKeyboard(view: View) {
        val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    // Проверка доступности интернета
    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}