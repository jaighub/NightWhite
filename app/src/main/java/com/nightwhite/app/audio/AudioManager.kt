package com.nightwhite.app.audio

import android.content.Context
import android.media.AudioManager as SysAudioManager
import com.nightwhite.app.model.AudioMode
import com.nightwhite.app.model.LullabySong
import com.nightwhite.app.model.NoiseColor

class AudioManager(context: Context) {
    private var noisePlayer: NoisePlayer? = null
    private var lullabyPlayer: LullabyPlayer? = null
    private var currentMode: AudioMode? = null

    private var lastMode: AudioMode = AudioMode.NOISE
    private var lastNoiseColor: NoiseColor = NoiseColor.BROWN
    private var lastBrownNoiseDepth: Float = 0.02f
    private var lastLullabySong: LullabySong = LullabySong.TWINKLE

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
                    createAndStartPlayer(lastMode, lastNoiseColor, lastBrownNoiseDepth, lastLullabySong)
                    currentMode = lastMode
                }
            }
        }
    }

    fun play(mode: AudioMode, noiseColor: NoiseColor, brownNoiseDepth: Float, lullabySong: LullabySong = LullabySong.TWINKLE) {
        lastMode = mode
        lastNoiseColor = noiseColor
        lastBrownNoiseDepth = brownNoiseDepth
        lastLullabySong = lullabySong

        val modeChanged = currentMode != mode

        if (modeChanged) {
            stopPlayers()
            if (!requestFocus()) return
            currentMode = mode
            createAndStartPlayer(mode, noiseColor, brownNoiseDepth, lullabySong)
        } else {
            updatePlayer(noiseColor, brownNoiseDepth, lullabySong)
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
        brownNoiseDepth: Float,
        lullabySong: LullabySong
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
                val player = LullabyPlayer(lullabySong)
                player.start()
                lullabyPlayer = player
            }
        }
    }

    private fun updatePlayer(
        noiseColor: NoiseColor,
        brownNoiseDepth: Float,
        lullabySong: LullabySong
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
            AudioMode.LULLABY -> {
                lullabyPlayer?.let { player ->
                    player.release()
                }
                val newPlayer = LullabyPlayer(lullabySong)
                newPlayer.start()
                lullabyPlayer = newPlayer
            }
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
