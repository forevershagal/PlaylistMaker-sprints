package com.example.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson

class SearchHistory(private var sharedPref: SharedPreferences) {
    companion object{
        const val HISTORY_LIST = "history_list"
        const val HISTORY_SIZE = 10
    }

    private var trackList = mutableListOf<Track>()
    private val gson = Gson()

    fun getSavedHistory(): MutableList<Track> {
        val str = sharedPref.getString(HISTORY_LIST, null)
        if (str != null) {
            trackList = mutableListOf<Track>().apply {
                addAll( gson.fromJson(str,Array<Track>::class.java ))}
        }
        return trackList
    }

    fun addTrackToHistory(track: Track) {
        if (getIndexOfTrack(track.trackId) != null)
            trackList.remove(track)
        else if (trackList.size >= HISTORY_SIZE)
            trackList.removeAt(trackList.lastIndex)
        trackList.add(0, track)
        saveTrackHistory()

    }

    fun clearTrackHistory() {
        trackList.clear()
        sharedPref.edit()
            .remove(HISTORY_LIST)
            .apply()
    }

    private fun saveTrackHistory() {
        val str = gson.toJson(trackList)
        sharedPref.edit()
            .putString(HISTORY_LIST, str)
            .apply()
    }

    fun getHistoryTracks(): MutableList<Track>{
        return trackList
    }

    private fun getIndexOfTrack(trackId: Int): Int? {
        for ((index, track) in trackList.withIndex()) {
            if (track.trackId == trackId) return index
        }
        return null
    }
}