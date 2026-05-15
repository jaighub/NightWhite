package com.nightlight.app.audio

import android.content.Context
import android.media.AudioManager as SysAudioManager
import com.nightlight.app.model.AudioMode
import com.nightlight.app.model.NoiseColor

class AudioManager(context: Context) {
    private var noisePlayer: NoisePlayer? = null
    private var lullabyPlayer: LullabyPlayer? = null
    private var currentMode: AudioMode? = null

    private var lastMode: AudioMode = AudioMode.NOISE
    private var lastNoiseColor: NoiseColor = NoiseColor.WHITE
    private var lastBrownNoiseDepth: Float = 0.02f

    private val sysAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as SysAudioManager
    private var wasPlaying = false

    private val focusChangeListener = SysAudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            SysAudioManager.AUDIOFOCUS_LOSS -> {
                wasPlaying = currentMode != null
                stopPlayers()
            }
            SysAudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                wasPlaying = currentMode != null
                stopPlayers()
            }
            SysAudioManager.AUDIOFOCUS_GAIN -> {
                if (wasPlaying) {
                    wasPlaying = false
                    createAndStartPlayer(lastMode, lastNoiseColor, lastBrownNoiseDepth)
                    currentMode = lastMode
                }
            }
        }
    }

    fun play(mode: AudioMode, noiseColor: NoiseColor, brownNoiseDepth: Float) {
        lastMode = mode
        lastNoiseColor = noiseColor
        lastBrownNoiseDepth = brownNoiseDepth

        val modeChanged = currentMode != mode

        if (modeChanged) {
            stopPlayers()
            if (!requestFocus()) return
            currentMode = mode
            createAndStartPlayer(mode, noiseColor, brownNoiseDepth)
        } else {
            updatePlayer(noiseColor, brownNoiseDepth)
        }
    }

    fun stop() {
        stopPlayers()
        sysAudioManager.abandonAudioFocus(focusChangeListener)
        wasPlaying = false
        currentMode = null
    }

    private fun createAndStartPlayer(
        mode: AudioMode,
        noiseColor: NoiseColor,
        brownNoiseDepth: Float
    ) {
        when (mode) {
            AudioMode.NOISE -> {
                val player = NoisePlayer()
                val generator = when (noiseColor) {
                    NoiseColor.WHITE -> WhiteNoiseGenerator()
                    NoiseColor.PINK -> PinkNoiseGenerator()
                    NoiseColor.BROWN -> BrownNoiseGenerator(brownNoiseDepth)
                }
                player.setNoiseColor(generator)
                player.start()
                noisePlayer = player
            }
            AudioMode.LULLABY -> {
                val player = LullabyPlayer()
                player.start()
                lullabyPlayer = player
            }
        }
    }

    private fun updatePlayer(
        noiseColor: NoiseColor,
        brownNoiseDepth: Float
    ) {
        when (currentMode) {
            AudioMode.NOISE -> {
                noisePlayer?.let { player ->
                    val generator = when (noiseColor) {
                        NoiseColor.WHITE -> WhiteNoiseGenerator()
                        NoiseColor.PINK -> PinkNoiseGenerator()
                        NoiseColor.BROWN -> BrownNoiseGenerator(brownNoiseDepth)
                    }
                    player.setNoiseColor(generator)
                }
            }
            AudioMode.LULLABY -> {}
            null -> {}
        }
    }

    private fun stopPlayers() {
        noisePlayer?.stop()
        noisePlayer?.release()
        noisePlayer = null

        lullabyPlayer?.stop()
        lullabyPlayer?.release()
        lullabyPlayer = null
    }

    private fun requestFocus(): Boolean {
        val result = sysAudioManager.requestAudioFocus(
            focusChangeListener,
            SysAudioManager.STREAM_MUSIC,
            SysAudioManager.AUDIOFOCUS_GAIN
        )
        return result == SysAudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }
}
