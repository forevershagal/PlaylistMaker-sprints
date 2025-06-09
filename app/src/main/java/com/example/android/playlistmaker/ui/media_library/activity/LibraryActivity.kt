package com.example.android.playlistmaker.ui.media_library.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.android.playlistmaker.ui.media_library.adapter.MediaLibraryPagerAdapter
import com.example.android.playlistmaker.ui.media_library.view_model.MediaLibraryViewModel
import com.example.playlistmaker.databinding.ActivityMediaLibraryBinding
import com.google.android.material.tabs.TabLayoutMediator
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaLibraryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMediaLibraryBinding
    private var tabMediator: TabLayoutMediator? = null
    private val viewModel by viewModel<MediaLibraryViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediaLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupViewPager()
        setupBackButton()


    }

    private fun setupViewPager() {
        val tabsData = viewModel.getTabsData()
        binding.viewPager.adapter = MediaLibraryPagerAdapter(this)

        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = getString(tabsData[position].titleResId)
        }.also {
            it.attach()
        }
    }

    private fun setupBackButton() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        tabMediator?.detach()
        tabMediator = null
        super.onDestroy()
    }
}