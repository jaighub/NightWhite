package com.nightlight.app.audio

interface NoiseGenerator {
    fun generate(buffer: ShortArray)
}
