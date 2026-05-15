package com.nightlight.app.audio

import kotlin.math.PI
import kotlin.math.sin

class LullabyGenerator {
    private val notes = listOf(
        Note(261.63f),
        Note(329.63f),
        Note(392.00f),
        Note(523.25f),
        Note(392.00f),
        Note(329.63f),
        Note(261.63f),
        Note(329.63f)
    )

    private val samplesPerNote = AudioConstants.SAMPLE_RATE
    private val amplitude = 0.3f

    private var currentNoteIndex = 0
    private var sampleIndexInNote = 0

    fun generate(buffer: ShortArray) {
        for (i in buffer.indices) {
            val note = notes[currentNoteIndex]
            val sample = (amplitude * sin(2.0f * PI.toFloat() * note.frequency * sampleIndexInNote / AudioConstants.SAMPLE_RATE.toFloat())).toFloat()
            buffer[i] = (sample * 32767).toInt().toShort()

            sampleIndexInNote++
            if (sampleIndexInNote >= samplesPerNote) {
                sampleIndexInNote = 0
                currentNoteIndex = (currentNoteIndex + 1) % notes.size
            }
        }
    }

    private data class Note(val frequency: Float)
}
