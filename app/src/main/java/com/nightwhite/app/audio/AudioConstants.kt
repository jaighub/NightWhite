package com.nightwhite.app.audio

import android.media.AudioFormat

object AudioConstants {
    const val SAMPLE_RATE = 44100
    val CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_MONO
    val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    val BUFFER_SIZE = 4410
}
