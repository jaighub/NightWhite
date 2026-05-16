package com.nightlight.app

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.nightlight.app.sensors.BatteryMonitor
import com.nightlight.app.sensors.FaceDownDetector
import com.nightlight.app.service.AudioService
import com.nightlight.app.ui.MainScreen
import com.nightlight.app.ui.theme.NightlightTheme
import com.nightlight.app.viewmodel.NightlightViewModel

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: NightlightViewModel
    private var faceDownDetector: FaceDownDetector? = null
    private var batteryMonitor: BatteryMonitor? = null
    private var audioService: AudioService? = null
    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as AudioService.LocalBinder
            audioService = binder.getService()
            isBound = true
            updateAudioService(viewModel.isSoundOn.value)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            audioService = null
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel = ViewModelProvider(this)[NightlightViewModel::class.java]
        viewModel.initPrefs(this)
        viewModel.setAudioMode(com.nightlight.app.model.AudioMode.NOISE)

        setContent {
            NightlightTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val isPoweredOn by viewModel.isPoweredOn.collectAsState()
                    val proximityTriggered by viewModel.proximityTriggered.collectAsState()
                    val effectiveBrightness by viewModel.effectiveBrightness.collectAsState()
                    val audioMode by viewModel.audioMode.collectAsState()
                    val lullabySong by viewModel.lullabySong.collectAsState()
                    val noiseColor by viewModel.noiseColor.collectAsState()
                    val brownNoiseDepth by viewModel.brownNoiseDepth.collectAsState()
                    val volume by viewModel.volume.collectAsState()

                    val isSoundOn by viewModel.isSoundOn.collectAsState()

                    LaunchedEffect(audioMode, lullabySong, noiseColor, brownNoiseDepth, volume, isSoundOn) {
                        updateAudioService(isSoundOn)
                    }

                    DisposableEffect(isPoweredOn, proximityTriggered) {
                        if (isPoweredOn && !proximityTriggered) {
                            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        } else {
                            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                        }
                        onDispose {}
                    }

                    DisposableEffect(effectiveBrightness, isPoweredOn) {
                        if (isPoweredOn) {
                            val attrs = window.attributes
                            attrs.screenBrightness = effectiveBrightness
                            window.attributes = attrs
                        }
                        onDispose {}
                    }

                    MainScreen(viewModel = viewModel)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val intent = Intent(this, AudioService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
            audioService = null
        }
    }

    override fun onResume() {
        super.onResume()
        faceDownDetector = FaceDownDetector(this) { triggered ->
            viewModel.setProximityTriggered(triggered)
        }
        faceDownDetector?.register()

        batteryMonitor = BatteryMonitor(this) {
            if (viewModel.isPoweredOn.value) {
                viewModel.togglePower()
            }
            Toast.makeText(this, getString(R.string.battery_low), Toast.LENGTH_LONG).show()
        }
        batteryMonitor?.register()
    }

    override fun onPause() {
        super.onPause()
        faceDownDetector?.unregister()
        faceDownDetector = null
        batteryMonitor?.unregister()
        batteryMonitor = null
    }

    override fun onDestroy() {
        super.onDestroy()
        val intent = Intent(this, AudioService::class.java)
        stopService(intent)
    }

    private fun updateAudioService(isSoundOn: Boolean) {
        if (!isBound || audioService == null) return
        if (isSoundOn) {
            val mode = viewModel.audioMode.value
            val song = viewModel.lullabySong.value
            val noiseColor = viewModel.noiseColor.value
            val depth = viewModel.brownNoiseDepth.value
            audioService?.getAudioManager()?.play(mode, noiseColor, depth, song)
            val label = if (mode == com.nightlight.app.model.AudioMode.LULLABY) "Lullaby playing" else "${noiseColor.name.lowercase()} noise playing"
            audioService?.updateNotification(label)
        } else {
            audioService?.getAudioManager()?.stop()
        }
    }
}
