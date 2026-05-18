package com.nightwhite.app.audio

import com.nightwhite.app.model.LullabySong
import kotlin.math.PI
import kotlin.math.sin

class LullabyGenerator(private val song: LullabySong = LullabySong.TWINKLE) {

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

    private val beatsPerSecond = if (song == LullabySong.TWINKLE) 4.5f else 4.0f
    private val amplitude = 0.12f
    private val envelopeSamples = (AudioConstants.SAMPLE_RATE / beatsPerSecond * 0.15f).toInt()

    private val reverbBuffer = FloatArray(AudioConstants.SAMPLE_RATE / 4)
    private var reverbIndex = 0
    private val reverbDecay = 0.6f

    private var currentNoteIndex = 0
    private var sampleIndexInNote = 0
    private var totalSampleCount = 0L

    fun generate(buffer: ShortArray) {
        for (i in buffer.indices) {
            val note = notes[currentNoteIndex]
            val samplesPerBeat = AudioConstants.SAMPLE_RATE.toFloat() / beatsPerSecond
            val samplesForNote = (samplesPerBeat * note.beats).toInt()

            val phase = 2.0f * PI.toFloat() * note.frequency * totalSampleCount / AudioConstants.SAMPLE_RATE.toFloat()

            val fundamental = sin(phase)
            val harmonic2 = sin(2.0f * phase) * 0.5f
            val harmonic3 = sin(3.0f * phase) * 0.25f
            val drySample = amplitude * (fundamental + harmonic2 + harmonic3)

            val envelope = calculateEnvelope(sampleIndexInNote, samplesForNote)
            val dryWithEnvelope = drySample * envelope

            val delayIndex = (reverbIndex - reverbBuffer.size / 4 + reverbBuffer.size) % reverbBuffer.size
            val wetSample = reverbBuffer[delayIndex] * reverbDecay

            reverbBuffer[reverbIndex] = dryWithEnvelope
            reverbIndex = (reverbIndex + 1) % reverbBuffer.size

            val finalSample = dryWithEnvelope + wetSample

            buffer[i] = (finalSample * 32767).toInt().toShort()

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
