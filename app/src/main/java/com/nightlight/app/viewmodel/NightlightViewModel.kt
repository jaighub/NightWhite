package com.nightlight.app.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioManager as SysAudioManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nightlight.app.model.AudioMode
import com.nightlight.app.model.NoiseColor
import com.nightlight.app.ui.util.kelvinToColor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class NightlightViewModel : ViewModel() {

    private lateinit var prefs: SharedPreferences
    private var sysAudioManager: SysAudioManager? = null

    private val _isPoweredOn = MutableStateFlow(false)
    val isPoweredOn: StateFlow<Boolean> = _isPoweredOn.asStateFlow()

    private val _brightness = MutableStateFlow(0.5f)
    val brightness: StateFlow<Float> = _brightness.asStateFlow()

    private val _colorTemp = MutableStateFlow(3000)
    val colorTemp: StateFlow<Int> = _colorTemp.asStateFlow()

    private val _audioMode = MutableStateFlow(AudioMode.NOISE)
    val audioMode: StateFlow<AudioMode> = _audioMode.asStateFlow()

    private val _isSoundOn = MutableStateFlow(false)
    val isSoundOn: StateFlow<Boolean> = _isSoundOn.asStateFlow()

    private val _noiseColor = MutableStateFlow(NoiseColor.WHITE)
    val noiseColor: StateFlow<NoiseColor> = _noiseColor.asStateFlow()

    private val _brownNoiseDepth = MutableStateFlow(0.02f)
    val brownNoiseDepth: StateFlow<Float> = _brownNoiseDepth.asStateFlow()

    private val _volume = MutableStateFlow(0.5f)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    private val _proximityTriggered = MutableStateFlow(false)
    val proximityTriggered: StateFlow<Boolean> = _proximityTriggered.asStateFlow()

    val effectiveBrightness: StateFlow<Float> = combine(
        proximityTriggered, isPoweredOn, brightness
    ) { triggered, powered, bright ->
        if (triggered && powered) 0.01f else bright
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0.5f)

    val effectiveColor: StateFlow<androidx.compose.ui.graphics.Color> = colorTemp
        .map { kelvinToColor(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, kelvinToColor(3000))

    fun initPrefs(context: Context) {
        prefs = context.getSharedPreferences("nightlight_prefs", Context.MODE_PRIVATE)
        sysAudioManager = context.getSystemService(Context.AUDIO_SERVICE) as SysAudioManager

        _volume.value = if (prefs.contains("volume")) {
            prefs.getFloat("volume", 0.3f)
        } else {
            0.3f
        }

        _isPoweredOn.value = prefs.getBoolean("isPoweredOn", false)
        _brightness.value = prefs.getFloat("brightness", 0.5f)
        _colorTemp.value = prefs.getInt("colorTemp", 3000)
    }

    fun togglePower() {
        _isPoweredOn.value = !_isPoweredOn.value
        prefs.edit().putBoolean("isPoweredOn", _isPoweredOn.value).apply()
    }

    fun setBrightness(value: Float) {
        _brightness.value = value.coerceIn(0f, 1f)
        prefs.edit().putFloat("brightness", _brightness.value).apply()
    }

    fun setColorTemp(value: Int) {
        _colorTemp.value = value.coerceIn(1900, 6500)
        prefs.edit().putInt("colorTemp", _colorTemp.value).apply()
    }

    fun setAudioMode(mode: AudioMode) {
        _audioMode.value = mode
    }

    fun toggleSound() {
        _isSoundOn.value = !_isSoundOn.value
    }

    fun setNoiseColor(color: NoiseColor) {
        _noiseColor.value = color
    }

    fun setBrownNoiseDepth(depth: Float) {
        _brownNoiseDepth.value = depth.coerceIn(0.01f, 0.1f)
    }

    fun setVolume(gain: Float) {
        _volume.value = gain.coerceIn(0f, 1f)
        prefs.edit().putFloat("volume", _volume.value).apply()
        sysAudioManager?.let { am ->
            val maxVol = am.getStreamMaxVolume(SysAudioManager.STREAM_MUSIC)
            val vol = (gain * maxVol).toInt()
            am.setStreamVolume(SysAudioManager.STREAM_MUSIC, vol, 0)
        }
    }

    fun setProximityTriggered(triggered: Boolean) {
        _proximityTriggered.value = triggered
    }
}
