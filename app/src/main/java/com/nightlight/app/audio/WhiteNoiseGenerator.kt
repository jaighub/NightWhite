package com.nightlight.app.audio

import kotlin.random.Random

class WhiteNoiseGenerator : NoiseGenerator {
    override fun generate(buffer: ShortArray) {
        for (i in buffer.indices) {
            val sample = ((Random.nextInt() shr 16) * 0.25f).toInt()
            buffer[i] = sample.toShort()
        }
    }
}
