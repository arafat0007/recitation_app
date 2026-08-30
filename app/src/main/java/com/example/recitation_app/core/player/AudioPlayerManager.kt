package com.example.recitation_app.core.player

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AudioPlayerManager(private val context: Context) {

    private var exoPlayer: ExoPlayer? = null
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _currentRepeat = MutableStateFlow(0)
    val currentRepeat = _currentRepeat.asStateFlow()

    private val _totalRepeats = MutableStateFlow(0)
    val totalRepeats = _totalRepeats.asStateFlow()

    private val _isFinished = MutableStateFlow(false)
    val isFinished = _isFinished.asStateFlow()

    private var targetRepeats = 0

    init {
        initializePlayer()
    }

    private fun initializePlayer() {
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        handlePlaybackEnded()
                    }
                }
            })
        }
    }

    private fun handlePlaybackEnded() {
        _currentRepeat.value += 1
        if (_currentRepeat.value < targetRepeats) {
            exoPlayer?.seekTo(0)
            exoPlayer?.play()
        } else {
            _isFinished.value = true
            _isPlaying.value = false
        }
    }

    fun playRawResource(resourceName: String, repeats: Int) {
        val resId = context.resources.getIdentifier(resourceName, "raw", context.packageName)
        if (resId == 0) {
            // Fallback for debugging if file not found
            _isFinished.value = true
            return
        }

        val uri = Uri.parse("android.resource://${context.packageName}/$resId")
        val mediaItem = MediaItem.fromUri(uri)

        targetRepeats = repeats
        _totalRepeats.value = repeats
        _currentRepeat.value = 0
        _isFinished.value = false

        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare()
        exoPlayer?.play()
    }

    fun pause() {
        exoPlayer?.pause()
    }

    fun resume() {
        if (_isFinished.value) {
            _currentRepeat.value = 0
            _isFinished.value = false
            exoPlayer?.seekTo(0)
        }
        exoPlayer?.play()
    }

    fun setSpeed(speed: Float) {
        exoPlayer?.setPlaybackSpeed(speed)
    }

    fun stop() {
        exoPlayer?.stop()
        _isPlaying.value = false
    }

    fun release() {
        exoPlayer?.release()
        exoPlayer = null
    }
}
