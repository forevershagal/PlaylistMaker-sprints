package com.example.android.playlistmaker.ui.playlist.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.android.playlistmaker.ui.playlist.view_model.PlaylistViewModel
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.core.os.bundleOf

class PlaylistFragment : Fragment() {


    companion object {
        private const val ARGS_PLAYLIST_ID = "playlist_id"

        fun createArgs(playlistId: String): Bundle =
            bundleOf(ARGS_PLAYLIST_ID to playlistId)

        fun newInstance() = PlaylistFragment()
    }


    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<PlaylistViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.playlistsEmptyPlaceholder.visibility = View.VISIBLE
        binding.createPlaylistButton.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}