package com.nightwhite.app.audio

import kotlin.random.Random

class BrownNoiseGenerator(private var a: Float = 0.02f) : NoiseGenerator {
    private var prev = 0f

    fun setDepth(newA: Float) {
        a = newA.coerceIn(0.001f, 0.5f)
    }

    override fun generate(buffer: ShortArray) {
        for (i in buffer.indices) {
            val white = Random.nextFloat() * 2 - 1
            prev = (a * white + (1 - a) * prev)
            val sample = (prev * 32767 * 0.4f).toInt()
            buffer[i] = sample.toShort()
        }
    }
}
