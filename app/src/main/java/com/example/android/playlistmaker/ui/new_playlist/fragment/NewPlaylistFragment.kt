package com.example.android.playlistmaker.ui.new_playlist.fragment

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.android.playlistmaker.ui.new_playlist.view_model.NewPlaylistViewModel
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<NewPlaylistViewModel>()

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                try {
                    binding.placeholderNewPlaylist.setImageURI(uri)
                    val picturesDir =
                        requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    viewModel.coverPath = viewModel.saveImageToPrivateStorage(
                        uri,
                        picturesDir!!,
                        requireContext()
                    )
                    viewModel.hasUnsavedChanges = true
                } catch (e: Exception) {
                    // Handle error
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
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

        val playlistId = arguments?.getLong("edit_playlist_id") ?: 0L
        if (playlistId != 0L) {
            viewModel.initEditingMode(playlistId)
            binding.textView2.text = getString(R.string.edit)
            binding.createNewPlaylistButton.text = getString(R.string.save)

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.playlistData.collectLatest { playlist ->
                    playlist?.let {
                        binding.nameNewPlaylist.setText(it.name)
                        binding.descriptionNewPlaylist.setText(it.description ?: "")

                        if (!it.coverPath.isNullOrEmpty()) {
                            Glide.with(requireContext())
                                .load(it.coverPath)
                                .into(binding.placeholderNewPlaylist)
                        } else {
                            binding.placeholderNewPlaylist.setImageResource(R.drawable.placeholder4)
                        }
                    }
                }
            }
        }

        setupTextWatchers()
        setupClickListeners()
        setupBackPressHandler()
        setupButtonStateObserver()
    }

    private fun setupTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.hasUnsavedChanges = true
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        binding.nameNewPlaylist.addTextChangedListener(textWatcher)
        binding.descriptionNewPlaylist.addTextChangedListener(textWatcher)
    }

    private fun setupClickListeners() {
        binding.coverNewPlaylistLayout.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            viewModel.hasUnsavedChanges = true
        }

        binding.backButtonPlayer.setOnClickListener {
            lifecycleScope.launch {
                val isEditing = viewModel.editingPlaylistId.value != 0L
                if (isEditing && !viewModel.hasUnsavedChanges) {
                    findNavController().navigateUp()
                } else {
                    checkUnsavedChangesAndNavigate()
                }
            }
        }

        binding.createNewPlaylistButton.setOnClickListener {
            viewModel.playlistName = binding.nameNewPlaylist.text.toString()
            viewModel.playlistDescription = binding.descriptionNewPlaylist.text.toString()

            val isEditing = viewModel.editingPlaylistId.value != 0L
            viewModel.savePlaylist(
                viewModel.playlistName,
                viewModel.playlistDescription,
                onSuccess = { playlistId ->
                    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                        val message = if (isEditing) {
                            getString(R.string.playlist_edited)
                        } else {
                            getString(R.string.playlist_created)
                        }
                        showToast(message)
                        parentFragmentManager.setFragmentResult(
                            "playlist_updated",
                            bundleOf("playlist_id" to playlistId)
                        )
                        findNavController().navigateUp()
                    }
                },
                onError = { errorKey ->
                    requireActivity().runOnUiThread {
                        when (errorKey) {
                            "playlist_name_required" -> {
                                binding.nameNewPlaylist.error =
                                    getString(R.string.playlist_name_required)
                            }

                            else -> {
                                showToast(getString(R.string.playlist_save_error))
                            }
                        }
                    }
                }
            )
        }
    }

    private fun setupBackPressHandler() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            checkUnsavedChangesAndNavigate()
        }
    }

    private fun setupButtonStateObserver() {
        lifecycleScope.launch {
            viewModel.isCreateButtonEnabled.collect { isEnabled ->
                binding.createNewPlaylistButton.isEnabled = isEnabled
                binding.createNewPlaylistButton.backgroundTintList = ColorStateList.valueOf(
                    if (isEnabled) {
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.switch_thumb_active_color
                        )
                    } else {
                        ContextCompat.getColor(requireContext(), R.color.text_color_hint)
                    }
                )
            }
        }

        binding.nameNewPlaylist.doAfterTextChanged { text ->
            viewModel.playlistName = text?.toString() ?: ""
        }
    }

    private fun checkUnsavedChangesAndNavigate() {
        if (viewModel.hasUnsavedChanges) {
            showExitDialog()
        } else {
            findNavController().navigateUp()
        }
    }

    private fun showExitDialog() {
        MaterialAlertDialogBuilder(requireContext(), R.style.MyAlertDialogTheme)
            .setTitle(getString(R.string.exit_dialog_title))
            .setMessage(getString(R.string.exit_dialog_message))
            .setNegativeButton(getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(getString(R.string.exit)) { dialog, _ ->
                dialog.dismiss()
                findNavController().navigateUp()
            }
            .show()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        if (requireActivity().intent?.getBooleanExtra("playlist_updated", false) == true) {
            requireActivity().intent.removeExtra("playlist_updated")
        }
    }

    companion object {
        fun newInstance() = NewPlaylistFragment()
    }
}
