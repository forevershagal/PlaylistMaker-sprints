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



    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"

    }
}