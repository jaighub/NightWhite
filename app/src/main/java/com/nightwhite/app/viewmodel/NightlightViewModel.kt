package com.nightwhite.app.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioManager as SysAudioManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nightwhite.app.model.AudioMode
import com.nightwhite.app.model.LullabySong
import com.nightwhite.app.model.NoiseColor
import com.nightwhite.app.ui.util.kelvinToColor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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

    private val _lullabySong = MutableStateFlow(LullabySong.TWINKLE)
    val lullabySong: StateFlow<LullabySong> = _lullabySong.asStateFlow()

    private val _isSoundOn = MutableStateFlow(false)
    val isSoundOn: StateFlow<Boolean> = _isSoundOn.asStateFlow()

    private val _noiseColor = MutableStateFlow(NoiseColor.BROWN)
    val noiseColor: StateFlow<NoiseColor> = _noiseColor.asStateFlow()

    private val _brownNoiseDepth = MutableStateFlow(0.02f)
    val brownNoiseDepth: StateFlow<Float> = _brownNoiseDepth.asStateFlow()

    private val _volume = MutableStateFlow(0.5f)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    private val _proximityTriggered = MutableStateFlow(false)
    val proximityTriggered: StateFlow<Boolean> = _proximityTriggered.asStateFlow()

    private val _showControls = MutableStateFlow(true)
    val showControls: StateFlow<Boolean> = _showControls.asStateFlow()

    private var controlsTimerJob: Job? = null
    private var sleepTimerJob: Job? = null
    private var fadeJob: Job? = null

    private val _sleepTimerMinutes = MutableStateFlow(0)
    val sleepTimerMinutes: StateFlow<Int> = _sleepTimerMinutes.asStateFlow()

    val effectiveBrightness: StateFlow<Float> = combine(
        proximityTriggered, isPoweredOn, brightness
    ) { triggered, powered, bright ->
        if (triggered && powered) 0.01f else bright
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0.5f)

    val effectiveColor: StateFlow<androidx.compose.ui.graphics.Color> = colorTemp
        .map { kelvinToColor(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, kelvinToColor(3000))

    fun initPrefs(context: Context) {
        val appContext = context.applicationContext
        prefs = appContext.getSharedPreferences("nightlight_prefs", Context.MODE_PRIVATE)
        sysAudioManager = appContext.getSystemService(Context.AUDIO_SERVICE) as SysAudioManager

        _isPoweredOn.value = prefs.getBoolean("isPoweredOn", false)
        _brightness.value = prefs.getFloat("brightness", 0.5f)
        _colorTemp.value = prefs.getInt("colorTemp", 3000)
        _sleepTimerMinutes.value = prefs.getInt("sleepTimer", 0)
        _volume.value = prefs.getFloat("volume", 0.3f)
        _audioMode.value = AudioMode.valueOf(prefs.getString("audioMode", AudioMode.NOISE.name) ?: AudioMode.NOISE.name)
        _lullabySong.value = LullabySong.valueOf(prefs.getString("lullabySong", LullabySong.TWINKLE.name) ?: LullabySong.TWINKLE.name)
        _isSoundOn.value = prefs.getBoolean("isSoundOn", false)
        _noiseColor.value = NoiseColor.valueOf(prefs.getString("noiseColor", NoiseColor.BROWN.name) ?: NoiseColor.BROWN.name)
        _brownNoiseDepth.value = prefs.getFloat("brownNoiseDepth", 0.02f)
    }

    fun togglePower() {
        if (_isPoweredOn.value) {
            fadeOutAndTurnOff()
        } else {
            fadeInAndTurnOn()
        }
        resetControlsTimer()
    }

    private fun fadeInAndTurnOn() {
        _isPoweredOn.value = true
        prefs.edit().putBoolean("isPoweredOn", true).apply()
        startSleepTimerIfNeeded()
    }

    private fun fadeOutAndTurnOff() {
        sleepTimerJob?.cancel()
        _sleepTimerMinutes.value = 0
        fadeJob?.cancel()
        _isPoweredOn.value = false
        prefs.edit().putBoolean("isPoweredOn", false).apply()
    }

    fun setSleepTimer(minutes: Int) {
        _sleepTimerMinutes.value = minutes
        prefs.edit().putInt("sleepTimer", minutes).apply()
        startSleepTimerIfNeeded()
    }

    private fun startSleepTimerIfNeeded() {
        sleepTimerJob?.cancel()
        val minutes = _sleepTimerMinutes.value
        if (minutes > 0 && _isPoweredOn.value) {
            sleepTimerJob = viewModelScope.launch {
                delay(minutes * 60 * 1000L)
                if (_isPoweredOn.value) {
                    fadeOutAndTurnOff()
                }
            }
        }
    }

    fun resetControlsTimer() {
        _showControls.value = true
        controlsTimerJob?.cancel()
        controlsTimerJob = viewModelScope.launch {
            delay(20000)
            _showControls.value = false
        }
    }

    fun setBrightness(value: Float) {
        _brightness.value = value.coerceIn(0f, 1f)
        prefs.edit().putFloat("brightness", _brightness.value).apply()
        resetControlsTimer()
    }

    fun setColorTemp(value: Int) {
        _colorTemp.value = value.coerceIn(1900, 6500)
        prefs.edit().putInt("colorTemp", _colorTemp.value).apply()
        resetControlsTimer()
    }

    fun setAudioMode(mode: AudioMode) {
        _audioMode.value = mode
        prefs.edit().putString("audioMode", mode.name).apply()
        resetControlsTimer()
    }

    fun setLullabySong(song: LullabySong) {
        _lullabySong.value = song
        prefs.edit().putString("lullabySong", song.name).apply()
        resetControlsTimer()
    }

    fun toggleSound() {
        _isSoundOn.value = !_isSoundOn.value
        prefs.edit().putBoolean("isSoundOn", _isSoundOn.value).apply()
        resetControlsTimer()
    }

    fun setNoiseColor(color: NoiseColor) {
        _noiseColor.value = color
        prefs.edit().putString("noiseColor", color.name).apply()
        resetControlsTimer()
    }

    fun setBrownNoiseDepth(depth: Float) {
        _brownNoiseDepth.value = depth.coerceIn(0.01f, 0.1f)
        prefs.edit().putFloat("brownNoiseDepth", _brownNoiseDepth.value).apply()
        resetControlsTimer()
    }

    fun setVolume(gain: Float) {
        _volume.value = gain.coerceIn(0f, 1f)
        prefs.edit().putFloat("volume", _volume.value).apply()
        sysAudioManager?.let { am ->
            val maxVol = am.getStreamMaxVolume(SysAudioManager.STREAM_MUSIC)
            val vol = (gain * maxVol).toInt()
            am.setStreamVolume(SysAudioManager.STREAM_MUSIC, vol, 0)
        }
        resetControlsTimer()
    }

    fun setProximityTriggered(triggered: Boolean) {
        _proximityTriggered.value = triggered
    }
}
