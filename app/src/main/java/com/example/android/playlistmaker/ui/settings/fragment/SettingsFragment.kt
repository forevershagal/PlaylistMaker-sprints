package com.example.android.playlistmaker.ui.settings.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.android.playlistmaker.app.MyApplication
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.android.playlistmaker.domain.sharing.model.EmailData
import com.example.android.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

//class SettingsFragment : AppCompatActivity() {
//
//    private lateinit var binding: FragmentSettingsBinding
//    private val viewModel by viewModel<SettingsViewModel>()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = FragmentSettingsBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        setupViews()
//        observeViewModel()
//    }
//
//    private fun setupViews() {
//        binding.toolbar.setOnClickListener { finish() }
//
//        binding.switchThemeButton.setOnCheckedChangeListener { _, checked ->
//            viewModel.updateThemeSettings(checked)
//        }
//
//        binding.shareIcon.setOnClickListener {
//            viewModel.shareApp(getString(R.string.practicum_url))
//        }
//
//        binding.supportIcon.setOnClickListener {
//            viewModel.openSupport(
//                EmailData(
//                    email = getString(R.string.target_email),
//                    subject = getString(R.string.mail_subject),
//                    body = getString(R.string.mail_body)
//                )
//            )
//        }
//
//        binding.userAgreementButton.setOnClickListener {
//            viewModel.openTerms(getString(R.string.practicum_offer))
//        }
//    }
//
//    private fun observeViewModel() {
//        viewModel.themeLiveData.observe(this) { themeSettings ->
//            binding.switchThemeButton.isChecked = themeSettings.isDarkTheme
//            (applicationContext as MyApplication).switchTheme(themeSettings.isDarkTheme)
//        }
//    }
//}

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<SettingsViewModel>()

    companion object {
        fun newInstance() = SettingsFragment()
        const val TAG = "SettingsFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {

        binding.switchThemeButton.setOnCheckedChangeListener { _, checked ->
            viewModel.updateThemeSettings(checked)
        }

        binding.shareButton.setOnClickListener {
            viewModel.shareApp(getString(R.string.practicum_url))
        }

        binding.supportButton.setOnClickListener {
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
        viewModel.themeLiveData.observe(viewLifecycleOwner) { themeSettings ->
            binding.switchThemeButton.isChecked = themeSettings.isDarkTheme
            (requireActivity().applicationContext as MyApplication).switchTheme(themeSettings.isDarkTheme)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}