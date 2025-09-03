package com.example.android.playlistmaker.ui.new_playlist.view_model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.playlistmaker.domain.db.playlist.PlaylistInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class NewPlaylistViewModel(private val playlistInteractor: PlaylistInteractor) : ViewModel() {

    private val _isCreateButtonEnabled = MutableStateFlow(false)
    val isCreateButtonEnabled: StateFlow<Boolean> = _isCreateButtonEnabled

    var hasUnsavedChanges = false
    var coverPath: String? = null
    var playlistName: String = ""
        set(value) {
            field = value
            _isCreateButtonEnabled.value = value.isNotEmpty()
        }
    var playlistDescription: String = ""

    fun savePlaylist(
        name: String,
        description: String,
        onSuccess: (Long) -> Unit,
        onError: (String) -> Unit
    ) {
        if (name.isEmpty()) {
            onError("playlist_name_required")
            return
        }

        viewModelScope.launch {
            try {
                val playlistId  = playlistInteractor.createPlaylist(
                    name = name,
                    description = description,
                    coverPath = coverPath
                )
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
            BitmapFactory.decodeStream(inputStream)?.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            inputStream?.close()
            outputStream.close()

            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }
}