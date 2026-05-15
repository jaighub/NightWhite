package com.nightlight.app.audio

import kotlin.random.Random

class PinkNoiseGenerator : NoiseGenerator {
    private val numOctaves = 16
    private val values = FloatArray(numOctaves) { Random.nextFloat() * 2 - 1 }
    private var counter = 1

    override fun generate(buffer: ShortArray) {
        for (i in buffer.indices) {
            var oct = 0
            var c = counter
            while ((c and 1) == 0 && oct < numOctaves - 1) {
                c = c shr 1
                oct++
            }
            values[oct] = Random.nextFloat() * 2 - 1
            counter = (counter + 1) and Int.MAX_VALUE

            var sum = 0f
            for (v in values) sum += v
            val sample = (sum / numOctaves * 32767 * 0.35f).toInt()
            buffer[i] = sample.toShort()
        }
    }
}
