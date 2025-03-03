package com.example.android.playlistmaker

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R

class SearchActivity : PLMakerActivityWithToolbar() {

    private var searchText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setStatusBar()
        setToolbar()
        setButtonActions()
        setRecyclerView()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        val textEdit = findViewById<EditText>(R.id.search_bar)
        val text = savedInstanceState.getString(SEARCH_TEXT)
        textEdit.setText(text)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
    }

    override fun setButtonActions() {
        setClearButton()
    }

    private fun setClearButton() {
        val button = findViewById<ImageView>(R.id.clear_text)
        button.isVisible = false

        val textEdit = findViewById<EditText>(R.id.search_bar)

        button.setOnClickListener {
            textEdit.setText("")
            textEdit.clearFocus()
        }

        textEdit.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus.not()) {
                hideKeyboard(view)
            }
        }

        textEdit.doOnTextChanged { text, start, before, count ->
            if (text.isNullOrEmpty()) {
                button.isVisible = false
            } else {
                searchText = text.toString()
                button.isVisible = true
            }
        }
    }

    private fun setRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.tracks_list)
        val adapter = TracksAdapter()
        recyclerView.adapter = adapter
        adapter.updateData(tracksStab)
    }

    companion object {
        private val SEARCH_TEXT = "SEARCH_TEXT"

        val tracksStab = listOf(
            Track("Smells Like Teen Spirit (Remastered 2009)", "Nirvana", "5:01", "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"),
            Track("Billie Jean", "Michael Jackson", "4:35", "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"),
            Track("Stayin' Alive", "Bee Gees", "4:10", "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"),
            Track("Whole Lotta Love", "Led Zeppelin", "5:33", "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"),
            Track("Sweet Child O'Mine", "Guns N' Roses", "5:03", "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg")
        )
    }
}