package com.example.android.playlistmaker.ui.new_playlist.view_model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.playlistmaker.data.db.entity.PlaylistEntity
import com.example.android.playlistmaker.domain.db.playlist.PlaylistInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class NewPlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val _isCreateButtonEnabled = MutableStateFlow(false)
    val isCreateButtonEnabled: StateFlow<Boolean> = _isCreateButtonEnabled

    private val _editingPlaylistId = MutableStateFlow(0L)
    val editingPlaylistId: StateFlow<Long> = _editingPlaylistId

    private val _playlistData = MutableStateFlow<PlaylistEntity?>(null)
    val playlistData: StateFlow<PlaylistEntity?> = _playlistData

    var hasUnsavedChanges = false
    var coverPath: String? = null

    var playlistName: String = ""
        set(value) {
            field = value
            _isCreateButtonEnabled.value = value.trim().isNotEmpty()
        }

    var playlistDescription: String = ""

    fun initEditingMode(playlistId: Long) {
        _editingPlaylistId.value = playlistId
        viewModelScope.launch {
            playlistInteractor.getPlaylist(playlistId)?.let { playlist ->
                _playlistData.value = playlist
                playlistName = playlist.name
                playlistDescription = playlist.description ?: ""
                coverPath = playlist.coverPath
            }
        }
    }

    fun savePlaylist(
        name: String,
        description: String,
        onSuccess: (Long) -> Unit,
        onError: (String) -> Unit
    ) {
        if (name.trim().isEmpty()) {
            onError("playlist_name_required")
            return
        }

        viewModelScope.launch {
            try {
                val playlistId = if (_editingPlaylistId.value != 0L) {
                    val existingPlaylist = playlistInteractor.getPlaylist(_editingPlaylistId.value)
                    playlistInteractor.updatePlaylist(
                        PlaylistEntity(
                            id = editingPlaylistId.value,
                            name = name,
                            description = description,
                            coverPath = coverPath,
                            trackIds = existingPlaylist?.trackIds ?: ""
                        )
                    )
                    editingPlaylistId.value
                } else {
                    playlistInteractor.createPlaylist(
                        name = name,
                        description = description,
                        coverPath = coverPath
                    )
                }
                onSuccess(playlistId)
            } catch (e: Exception) {
                e.printStackTrace()
                onError("playlist_save_error")
            }
        }
    }

    fun saveImageToPrivateStorage(uri: Uri, picturesDir: File, requireContext: Context): String? {
        val filePath = File(picturesDir, "playlist_covers")
        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val file = File(filePath, "playlist_cover_${System.currentTimeMillis()}.jpg")

        return try {
            val inputStream = requireContext.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            BitmapFactory.decodeStream(inputStream)
                ?.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            inputStream?.close()
            outputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }
}
