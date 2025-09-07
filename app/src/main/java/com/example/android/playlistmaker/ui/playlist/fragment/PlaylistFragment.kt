package com.example.android.playlistmaker.ui.playlist.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.ui.playlist.adapter.PlaylistAdapter
import com.example.android.playlistmaker.ui.playlist.view_model.PlaylistViewModel
import com.example.android.playlistmaker.domain.models.Playlist
import com.example.android.playlistmaker.utils.GridSpacingItemDecoration
import com.example.android.playlistmaker.ui.media_library.fragment.MediaLibraryFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    companion object {
        private const val ARGS_PLAYLIST_ID = "playlist_id"
        private const val SPAN_COUNT = 2

        fun createArgs(playlistId: Long): Bundle =
            bundleOf(ARGS_PLAYLIST_ID to playlistId)

        fun newInstance() = PlaylistFragment()
    }

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<PlaylistViewModel>()
    private lateinit var adapter: PlaylistAdapter

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

        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        viewModel.loadPlaylists()
    }

    private fun setupRecyclerView() {
        adapter = PlaylistAdapter { playlist ->
            (requireParentFragment() as? MediaLibraryFragment)?.findNavController()?.navigate(
                R.id.action_mediaLibraryFragment_to_playlistInfoFragment,
                bundleOf("playlist_id" to playlist.id.toLong())
            )
        }

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.grid_spacing)
        binding.playlistsRecyclerView.addItemDecoration(
            GridSpacingItemDecoration(
                spanCount = SPAN_COUNT,
                spacing = spacingInPixels,
                includeEdge = true
            )
        )

        binding.playlistsRecyclerView.layoutManager = GridLayoutManager(requireContext(), SPAN_COUNT)
        binding.playlistsRecyclerView.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.playlists.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                showEmptyState()
            } else {
                showPlaylists(it)
            }
        }
    }

    private fun setupClickListeners() {
        binding.createPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.newPlaylistFragment)
        }
    }

    private fun showEmptyState() {
        binding.playlistsRecyclerView.visibility = View.GONE
        binding.playlistsEmptyPlaceholder.visibility = View.VISIBLE
        binding.textView.visibility = View.VISIBLE
        binding.createPlaylistButton.visibility = View.VISIBLE
    }

    private fun showPlaylists(playlists: List<Playlist>) {
        binding.playlistsRecyclerView.visibility = View.VISIBLE
        binding.playlistsEmptyPlaceholder.visibility = View.GONE
        binding.textView.visibility = View.GONE
        binding.createPlaylistButton.visibility = View.VISIBLE

        adapter.submitList(playlists)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openPlaylistInfo(playlistId: Long) {
        parentFragment?.findNavController()?.navigate(
            R.id.action_mediaLibraryFragment_to_playlistInfoFragment,
            bundleOf("playlist_id" to playlistId)
        )
    }
}
