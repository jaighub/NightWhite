package com.nightlight.app.audio

import com.nightlight.app.model.LullabySong
import kotlin.math.PI
import kotlin.math.sin

class LullabyGenerator(private val song: LullabySong = LullabySong.BRAHMS) {

    private val notes = when (song) {
        LullabySong.BRAHMS -> listOf(
            Note(392.00f, 6), Note(392.00f, 6), Note(440.00f, 6),
            Note(392.00f, 6), Note(329.63f, 6), Note(392.00f, 6),
            Note(440.00f, 6), Note(392.00f, 6),
            Note(349.23f, 6), Note(329.63f, 6), Note(293.66f, 6),
            Note(261.63f, 6), Note(329.63f, 6), Note(392.00f, 6),
            Note(440.00f, 6), Note(392.00f, 6),
            Note(349.23f, 6), Note(329.63f, 6), Note(293.66f, 6),
            Note(261.63f, 6), Note(329.63f, 6), Note(392.00f, 6),
            Note(440.00f, 6), Note(392.00f, 6),
            Note(349.23f, 6), Note(329.63f, 6), Note(293.66f, 6),
            Note(261.63f, 6), Note(261.63f, 6), Note(261.63f, 6),
            Note(261.63f, 6), Note(261.63f, 6),
        )
        LullabySong.TWINKLE -> listOf(
            Note(261.63f, 6), Note(261.63f, 6), Note(392.00f, 6),
            Note(392.00f, 6), Note(440.00f, 6), Note(440.00f, 6),
            Note(392.00f, 12),
            Note(349.23f, 6), Note(349.23f, 6), Note(329.63f, 6),
            Note(329.63f, 6), Note(293.66f, 6), Note(293.66f, 6),
            Note(261.63f, 12),
            Note(392.00f, 6), Note(392.00f, 6), Note(349.23f, 6),
            Note(349.23f, 6), Note(329.63f, 6), Note(329.63f, 6),
            Note(293.66f, 12),
            Note(392.00f, 6), Note(392.00f, 6), Note(349.23f, 6),
            Note(349.23f, 6), Note(329.63f, 6), Note(329.63f, 6),
            Note(293.66f, 12),
            Note(261.63f, 6), Note(261.63f, 6), Note(392.00f, 6),
            Note(392.00f, 6), Note(440.00f, 6), Note(440.00f, 6),
            Note(392.00f, 12),
            Note(349.23f, 6), Note(349.23f, 6), Note(329.63f, 6),
            Note(329.63f, 6), Note(293.66f, 6), Note(293.66f, 6),
            Note(261.63f, 12),
        )
    }

    private val beatsPerSecond = if (song == LullabySong.TWINKLE) 4.5f else 3.2f
    private val amplitude = 0.2f
    private val envelopeSamples = (AudioConstants.SAMPLE_RATE / beatsPerSecond * 0.15f).toInt()

    private var currentNoteIndex = 0
    private var sampleIndexInNote = 0
    private var totalSampleCount = 0L

    fun generate(buffer: ShortArray) {
        for (i in buffer.indices) {
            val note = notes[currentNoteIndex]
            val samplesPerBeat = AudioConstants.SAMPLE_RATE.toFloat() / beatsPerSecond
            val samplesForNote = (samplesPerBeat * note.beats).toInt()

            val phase = 2.0f * PI.toFloat() * note.frequency * totalSampleCount / AudioConstants.SAMPLE_RATE.toFloat()
            val rawSample = sin(phase).toFloat()

            val envelope = calculateEnvelope(sampleIndexInNote, samplesForNote)
            val sample = amplitude * rawSample * envelope

            buffer[i] = (sample * 32767).toInt().toShort()

            sampleIndexInNote++
            totalSampleCount++
            if (sampleIndexInNote >= samplesForNote) {
                sampleIndexInNote = 0
                currentNoteIndex = (currentNoteIndex + 1) % notes.size
            }
        }
    }

    private fun calculateEnvelope(pos: Int, total: Int): Float {
        if (total <= envelopeSamples * 2) return 1f
        val fadeIn = (pos.toFloat() / envelopeSamples).coerceAtMost(1f)
        val fadeOut = ((total - pos).toFloat() / envelopeSamples).coerceAtMost(1f)
        return fadeIn * fadeOut
    }

    private data class Note(val frequency: Float, val beats: Int)
}
