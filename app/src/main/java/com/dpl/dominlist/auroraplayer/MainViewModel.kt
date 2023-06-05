package com.dpl.dominlist.auroraplayer

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val player: Player,
    private val savedStateHandle: SavedStateHandle,
    private val metaDataReader: MetaDataReader,
) : ViewModel() {

    private val musicUris = savedStateHandle.getStateFlow("audioUris", metaDataReader.getAudioUris())

    val musicItems = musicUris.map { uris ->
        uris.map { uri ->
            AudioItem(
                contentUri = uri,
                mediaItem = MediaItem.fromUri(uri),
                name = metaDataReader.getMetaDataFromUri(uri)?.songName ?: "no name"
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L),
        emptyList()
    )


    init {
        player.prepare()
    }

    fun addUri(uri: Uri) {
        savedStateHandle["audioUris"] = musicUris.value + uri
        player.addMediaItem(MediaItem.fromUri(uri))
    }

    fun setMediaItem(uri: Uri) {
        player.setMediaItem(
            musicItems.value.find { it.contentUri == uri }?.mediaItem ?: return
        )
    }

    fun pause() {
        player.pause()
    }

    fun play() {
        player.play()
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}