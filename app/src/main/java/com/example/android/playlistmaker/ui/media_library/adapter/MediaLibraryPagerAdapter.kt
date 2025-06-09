package com.example.android.playlistmaker.ui.media_library.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.android.playlistmaker.ui.media_library.fragments.favourite_fragment.FavouriteFragment
import com.example.android.playlistmaker.ui.media_library.fragments.playlist_fragment.PlaylistFragment

class MediaLibraryPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> FavouriteFragment.newInstance()
            else -> PlaylistFragment.newInstance()
        }
    }
}