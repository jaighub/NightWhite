package com.nightlight.app.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicReference

class NoisePlayer {
    private val audioTrack = createAudioTrack()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default + CoroutineExceptionHandler { _, e ->
        Log.e("NoisePlayer", "Audio coroutine failed", e)
    })
    private var playJob: Job? = null
    private val generatorRef = AtomicReference<NoiseGenerator>(WhiteNoiseGenerator())

    fun start() {
        playJob?.cancel()
        audioTrack.play()
        playJob = scope.launch {
            val buffer = ShortArray(AudioConstants.BUFFER_SIZE)
            while (isActive) {
                generatorRef.get().generate(buffer)
                audioTrack.write(buffer, 0, buffer.size)
            }
        }
    }

    fun stop() {
        playJob?.cancel()
        playJob = null
        audioTrack.stop()
        audioTrack.flush()
    }

    fun release() {
        stop()
        audioTrack.release()
    }

    fun setVolume(gain: Float) {
        audioTrack.setVolume(gain)
    }

    fun setNoiseColor(generator: NoiseGenerator) {
        generatorRef.set(generator)
    }

    private fun createAudioTrack(): AudioTrack {
        val format = AudioFormat.Builder()
            .setEncoding(AudioConstants.AUDIO_FORMAT)
            .setSampleRate(AudioConstants.SAMPLE_RATE)
            .setChannelMask(AudioConstants.CHANNEL_CONFIG)
            .build()

        val bufferSize = AudioTrack.getMinBufferSize(
            AudioConstants.SAMPLE_RATE,
            AudioConstants.CHANNEL_CONFIG,
            AudioConstants.AUDIO_FORMAT
        )

        val attributes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        } else {
            null
        }

        return if (attributes != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AudioTrack.Builder()
                .setAudioAttributes(attributes)
                .setAudioFormat(format)
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()
        } else {
            AudioTrack(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build(),
                format,
                bufferSize,
                AudioTrack.MODE_STREAM,
                0
            )
        }
    }
}
