package com.example.android.playlistmaker.ui.playlist_info.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistInfoBinding
import com.example.android.playlistmaker.domain.models.Playlist
import com.example.android.playlistmaker.domain.models.Track
import com.example.android.playlistmaker.ui.audio_player.fragment.AudioPlayerFragment
import com.example.android.playlistmaker.ui.playlist_info.adapter.TrackInPlaylistAdapter
import com.example.android.playlistmaker.ui.playlist_info.view_model.PlaylistInfoViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlaylistInfoFragment : Fragment() {
    private var _binding: FragmentPlaylistInfoBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<PlaylistInfoViewModel>()
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<NestedScrollView>
    private lateinit var menuBottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var adapter: TrackInPlaylistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistInfoBinding.inflate(inflater, container, false)
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
                0
            )
            insets

        }

        parentFragmentManager.setFragmentResultListener("playlist_updated", viewLifecycleOwner) { _, bundle ->
            val playlistId = bundle.getLong("playlist_id")
            if (playlistId == arguments?.getLong(ARG_PLAYLIST_ID)) {
                viewModel.loadPlaylist(playlistId)
            }
        }

        val playlistId = arguments?.getLong(ARG_PLAYLIST_ID) ?: 0L
        viewModel.loadPlaylist(playlistId)

        val bundle = savedInstanceState ?: arguments
        val track = Track(
            trackId = bundle?.getString(ARG_TRACK_ID) ?: "Unknown Id",
            trackName = bundle?.getString(ARG_TRACK_NAME) ?: "Unknown Track",
            artistName = bundle?.getString(ARG_ARTIST_NAME) ?: "Unknown Artist",
            trackTimeMillis = bundle?.getString(ARG_TRACK_TIME) ?: "",
            artworkUrl100 = bundle?.getString(ARG_ARTWORK_URL) ?: "",
            collectionName = bundle?.getString(ARG_COLLECTION_NAME) ?: "Unknown Album",
            releaseDate = bundle?.getString(ARG_RELEASE_DATE) ?: "Unknown Year",
            primaryGenreName = bundle?.getString(ARG_GENRE_NAME) ?: "Unknown Genre",
            country = bundle?.getString(ARG_COUNTRY) ?: "Unknown Country",
            previewUrl = bundle?.getString(ARG_PREVIEW_URL) ?: ""
        )

        setupObservers()
        setupClickListeners()
        setupAdapter()

        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsInfoBottomSheet)
        bottomSheetBehavior.isHideable = false
        bottomSheetBehavior.peekHeight = resources.getDimensionPixelSize(R.dimen.bottom_sheet_peek_height)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (!isAdded) return
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }
                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (!isAdded) return
                binding.overlay.alpha = slideOffset.coerceAtLeast(0f)
            }
        })

        setupMenuBottomSheet()

        binding.playlistsRecyclerView.adapter = adapter
        binding.playlistsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

    }

    private fun setupAdapter() {
        adapter = TrackInPlaylistAdapter(
            emptyList(),
            onTrackClick = { track ->
                if (track.previewUrl.isBlank()) {
                    Toast.makeText(requireContext(),
                        getString(R.string.error),
                        Toast.LENGTH_SHORT).show()
                    return@TrackInPlaylistAdapter
                }

                findNavController().navigate(
                    R.id.action_global_audioPlayerFragment,
                    AudioPlayerFragment.createArgs(track)
                )
            },
            onTrackLongClick = { track ->
                showDeleteDialog(track)
            }
        )
    }

    private fun showDeleteDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.want_to_delete))
            .setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                val playlistId = arguments?.getLong("playlist_id") ?: 0L
                viewModel.deleteTrack(playlistId, track.trackId)
                dialog.dismiss()
            }
            .show()
    }

    private fun setupObservers() {
        viewModel.playlist.observe(viewLifecycleOwner) { playlist ->
            binding.playlistName.text = playlist.name
            binding.yearOfPlaylist.text = playlist.description
            binding.trackCount.text = getTrackCountString(playlist.trackCount)

            binding.playlistNameBsh.text = playlist.name
            binding.playlistTracksCount.text = getTrackCountString(playlist.trackCount)

            playlist.coverPath?.let { path ->
                Glide.with(this)
                    .load(path)
                    .into(binding.placeholderNewPlaylist)

                Glide.with(this)
                    .load(path)
                    .into(binding.playlistCover)
            } ?: run {
                binding.placeholderNewPlaylist.setImageResource(R.drawable.placeholder4)
                binding.playlistCover.setImageResource(R.drawable.placeholder4)
            }

            viewModel.calculateDuration(playlist.trackIds)
        }

        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            adapter.updateTracks(tracks)
        }

        viewModel.duration.observe(viewLifecycleOwner) { duration ->
            binding.timeDuration.text = duration
        }

        viewModel.refreshTrigger.observe(viewLifecycleOwner) {
            arguments?.getLong(ARG_PLAYLIST_ID)?.let { playlistId ->
                viewModel.loadPlaylist(playlistId)
            }
        }
    }

    fun getTrackCountString(count: Int): String {
        val lastDigit = count % 10
        val lastTwoDigits = count % 100

        return when {
            lastTwoDigits in 11..14 -> "$count треков"
            lastDigit == 1 -> "$count трек"
            lastDigit in 2..4 -> "$count трека"
            else -> "$count треков"
        }
    }

    private fun setupClickListeners() {
        binding.backButtonPlayer.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.editInfoButton.setOnClickListener {
            val playlistId = arguments?.getLong(ARG_PLAYLIST_ID) ?: 0L
            findNavController().navigate(
                R.id.action_playlistInfoFragment_to_newPlaylistFragment,
                bundleOf("edit_playlist_id" to playlistId)
            )
        }

        binding.ShareButton.setOnClickListener {
            viewModel.tracks.value?.let { tracks ->
                if (tracks.isEmpty()) {
                    showToast(getString(R.string.no_tracks_to_share))
                } else {
                    val playlist = viewModel.playlist.value
                    val shareText = buildShareText(playlist, tracks)
                    sharePlaylist(shareText)
                }
            }
        }
    }

    private fun buildShareText(playlist: Playlist?, tracks: List<Track>): String {
        return StringBuilder().apply {
            append("${playlist?.name}\n")
            append("${playlist?.description}\n")
            append("[${tracks.size}] треков\n\n")
            tracks.forEachIndexed { index, track ->
                append("${index + 1}. ${track.artistName} - ${track.trackName} (${track.trackTimeMillis})\n")
            }
        }.toString()
    }

    private fun sharePlaylist(text: String) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(sendIntent, null))
    }

    private fun setupMenuBottomSheet() {
        menuBottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet)
        menuBottomSheetBehavior.isHideable = true
        menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        menuBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (!isAdded) return
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
//                    BottomSheetBehavior.STATE_HIDDEN -> {
//                        binding.overlay.visibility = View.GONE
//                    }
                    else -> {
                        binding.overlay.visibility = View.GONE
                    }
                }
            }


            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (!isAdded) return
                binding.overlay.alpha = slideOffset.coerceAtLeast(0f)
            }
        })

        binding.menuButton.setOnClickListener {
            arguments?.getLong(ARG_PLAYLIST_ID)?.let { playlistId ->
                viewModel.forceRefresh()
                menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        binding.shareButton.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            viewModel.tracks.value?.let { tracks ->
                if (tracks.isEmpty()) {
                    showToast(getString(R.string.no_tracks_to_share))
                } else {
                    val playlist = viewModel.playlist.value
                    val shareText = buildShareText(playlist, tracks)
                    sharePlaylist(shareText)
                }
            }
        }

        binding.deletePlaylistButton.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            showDeletePlaylistDialog()
        }

    }

    private fun showDeletePlaylistDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_playlist))
            .setMessage(getString(R.string.want_to_delete_playlist))
            .setNegativeButton(getString(R.string.no)) { dialog, _ -> dialog.dismiss() }
            .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                val playlistId = arguments?.getLong(ARG_PLAYLIST_ID) ?: 0L
                viewModel.deletePlaylist(playlistId)
                findNavController().navigateUp()
            }
            .show()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        arguments?.getLong(ARG_PLAYLIST_ID)?.let { playlistId ->
            viewModel.loadPlaylist(playlistId)
        }
    }

    companion object {
        private const val ARG_PLAYLIST_ID = "playlist_id"

        const val ARG_TRACK_ID = "track_id"
        const val ARG_TRACK_NAME = "track_name"
        const val ARG_ARTIST_NAME = "artist_name"
        const val ARG_TRACK_TIME = "track_time"
        const val ARG_ARTWORK_URL = "artwork_url"
        const val ARG_COLLECTION_NAME = "collection_name"
        const val ARG_RELEASE_DATE = "release_date"
        const val ARG_GENRE_NAME = "genre_name"
        const val ARG_COUNTRY = "country"
        const val ARG_PREVIEW_URL = "preview_url"

        fun createArgs(track: Track): Bundle {
            return bundleOf(
                ARG_TRACK_ID to track.trackId,
                ARG_TRACK_NAME to track.trackName,
                ARG_ARTIST_NAME to track.artistName,
                ARG_TRACK_TIME to track.trackTimeMillis,
                ARG_ARTWORK_URL to track.artworkUrl100,
                ARG_COLLECTION_NAME to track.collectionName,
                ARG_RELEASE_DATE to track.releaseDate,
                ARG_GENRE_NAME to track.primaryGenreName,
                ARG_COUNTRY to track.country,
                ARG_PREVIEW_URL to track.previewUrl
            )
        }
    }
}