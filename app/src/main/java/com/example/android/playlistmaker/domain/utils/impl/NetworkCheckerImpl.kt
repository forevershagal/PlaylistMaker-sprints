package com.example.android.playlistmaker.data.utils.impl

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.android.playlistmaker.domain.utils.NetworkChecker

class NetworkCheckerImpl(private val context: Context) : NetworkChecker {
    override fun isConnected(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
