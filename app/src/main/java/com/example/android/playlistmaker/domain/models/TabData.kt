package com.example.android.playlistmaker.domain.models

import androidx.fragment.app.Fragment

data class TabData(
    val titleResId: Int,
    val fragmentCreator: () -> Fragment
)