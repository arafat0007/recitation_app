package com.example.recitation_app.feature_zikir.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.recitation_app.data.repository.ZikirRepositoryImpl
import com.example.recitation_app.domain.model.Zikir
import com.example.recitation_app.domain.repository.ZikirRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ZikirUiState(
    val items: List<Zikir> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val currentlyPlayingId: String? = null,
    val isPlaying: Boolean = false,
    val errorMessage: String? = null
)

class ZikirViewModel(
    private val repository: ZikirRepository = ZikirRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ZikirUiState(loading = true))
    val uiState: StateFlow<ZikirUiState> = _uiState.asStateFlow()

    private var exoPlayer: ExoPlayer? = null
    private var appContext: Context? = null

    fun loadZikirs() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }
            repository.getZikirs()
                .catch { e ->
                    _uiState.update { it.copy(loading = false, error = e.message ?: "ত্রুটি হয়েছে") }
                }
                .collect { items ->
                    _uiState.update {
                        it.copy(
                            loading = false,
                            items = items,
                            error = if (items.isEmpty() && it.error == null) null else it.error
                        )
                    }
                }
        }
    }

    fun initializePlayer(context: Context) {
        appContext = context.applicationContext
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build().apply {
                addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        _uiState.update { it.copy(isPlaying = isPlaying) }
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_ENDED) {
                            _uiState.update {
                                it.copy(isPlaying = false, currentlyPlayingId = null)
                            }
                        }
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        _uiState.update {
                            it.copy(
                                isPlaying = false,
                                currentlyPlayingId = null,
                                errorMessage = "প্লে করতে সমস্যা হয়েছে"
                            )
                        }
                    }
                })
            }
        }
    }

    fun togglePlayPause(zikir: Zikir) {
        val player = exoPlayer ?: return
        val context = appContext ?: return
        val currentState = _uiState.value

        // Same item already loaded — just toggle.
        if (currentState.currentlyPlayingId == zikir.id) {
            if (currentState.isPlaying) player.pause() else player.play()
            return
        }

        val resId = resolveRawResourceId(context, zikir.fileName)
        if (resId == 0) {
            _uiState.update {
                it.copy(
                    currentlyPlayingId = null,
                    isPlaying = false,
                    errorMessage = "অডিও ফাইল পাওয়া যায়নি"
                )
            }
            return
        }

        val uri = Uri.parse("android.resource://${context.packageName}/$resId")
        player.setMediaItem(MediaItem.fromUri(uri))
        player.prepare()
        player.play()
        _uiState.update {
            it.copy(currentlyPlayingId = zikir.id, isPlaying = true, errorMessage = null)
        }
    }

    private fun resolveRawResourceId(context: Context, fileName: String): Int {
        if (fileName.isBlank()) return 0
        // Strip the extension (e.g. "salawat_azimiyya_23689.mp3" -> "salawat_azimiyya_23689").
        val name = fileName.substringBefore('.', fileName)
        return context.resources.getIdentifier(name, "raw", context.packageName)
    }

    fun stopPlayback() {
        exoPlayer?.stop()
        exoPlayer?.clearMediaItems()
        _uiState.update {
            it.copy(currentlyPlayingId = null, isPlaying = false)
        }
    }

    fun releasePlayer() {
        exoPlayer?.release()
        exoPlayer = null
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}