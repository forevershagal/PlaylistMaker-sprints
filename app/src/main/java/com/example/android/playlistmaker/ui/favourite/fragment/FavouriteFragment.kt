package com.example.android.playlistmaker.ui.favourite.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.FragmentFavouriteTracksBinding
import com.example.android.playlistmaker.ui.favourite.state.FavouriteTrackState
import com.example.android.playlistmaker.ui.favourite.view_model.FavouriteViewModel
import com.example.android.playlistmaker.ui.media_library.fragment.MediaLibraryFragment
import com.example.android.playlistmaker.ui.search.adapter.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavouriteFragment : Fragment() {

    private lateinit var adapter: TrackAdapter
    private var _binding: FragmentFavouriteTracksBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<FavouriteViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouriteTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TrackAdapter(emptyList()) { track ->
            (parentFragment as? MediaLibraryFragment)?.navigateToPlayer(track)
        }

        binding.trackList.adapter = adapter
        binding.trackList.layoutManager = LinearLayoutManager(requireContext())

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is FavouriteTrackState.Empty -> {
                    binding.noFavouriteTrackLayout.visibility = View.VISIBLE
                    binding.trackList.visibility = View.GONE
                }
                is FavouriteTrackState.Content -> {
                    binding.noFavouriteTrackLayout.visibility = View.GONE
                    binding.trackList.visibility = View.VISIBLE
                    adapter.updateTracks(state.tracks)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshFavorites()
    }

    companion object {
        fun newInstance() = FavouriteFragment()
    }
}