package com.example.android.playlistmaker.app.extensions

import android.view.View

var View.isGone: Boolean
    get() = this.visibility == View.GONE
    set(value) {
        this.visibility = if (value) View.GONE else View.VISIBLE
    }

var View.isVisible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }