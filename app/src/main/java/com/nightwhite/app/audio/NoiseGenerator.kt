package com.nightwhite.app.audio

interface NoiseGenerator {
    fun generate(buffer: ShortArray)
}
