package com.example.android.playlistmaker.utils

import android.content.Context

fun dpToPx(dp: Float, context: Context): Int {
    return (dp * context.resources.displayMetrics.density).toInt()
}