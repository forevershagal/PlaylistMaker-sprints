package com.example.android.playlistmaker.ui.settings.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.android.playlistmaker.app.App
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.android.playlistmaker.domain.sharing.model.EmailData
import com.example.android.playlistmaker.ui.settings.view_model.SettingsViewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            SettingsViewModel.getViewModelFactory(this@SettingsActivity)
        )[SettingsViewModel::class.java]

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        binding.toolbar.setOnClickListener { finish() }

        binding.switchThemeButton.setOnCheckedChangeListener { _, checked ->
            viewModel.updateThemeSettings(checked)
        }

        binding.shareIcon.setOnClickListener {
            viewModel.shareApp(getString(R.string.practicum_url))
        }

        binding.supportIcon.setOnClickListener {
            viewModel.openSupport(
                EmailData(
                    email = getString(R.string.target_email),
                    subject = getString(R.string.mail_subject),
                    body = getString(R.string.mail_body)
                )
            )
        }

        binding.userAgreementButton.setOnClickListener {
            viewModel.openTerms(getString(R.string.practicum_offer))
        }
    }

    private fun observeViewModel() {
        viewModel.themeLiveData.observe(this) { themeSettings ->
            binding.switchThemeButton.isChecked = themeSettings.isDarkTheme
            (applicationContext as App).switchTheme(themeSettings.isDarkTheme)
        }
    }
}