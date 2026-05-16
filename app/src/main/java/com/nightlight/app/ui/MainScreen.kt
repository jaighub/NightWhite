package com.nightlight.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.nightlight.app.ui.components.AudioModeSelector
import com.nightlight.app.ui.components.BrightnessSlider
import com.nightlight.app.ui.components.BrownNoiseDepthSlider
import com.nightlight.app.ui.components.ColorTemperatureSlider
import com.nightlight.app.ui.components.NoiseColorSelector
import com.nightlight.app.ui.components.PowerButton
import com.nightlight.app.ui.components.VolumeSlider
import com.nightlight.app.ui.theme.WarmBlack
import com.nightlight.app.viewmodel.NightlightViewModel

@Composable
fun MainScreen(viewModel: NightlightViewModel) {
    val isPoweredOn by viewModel.isPoweredOn.collectAsState()
    val brightness by viewModel.brightness.collectAsState()
    val colorTemp by viewModel.colorTemp.collectAsState()
    val effectiveColor by viewModel.effectiveColor.collectAsState()
    val audioMode by viewModel.audioMode.collectAsState()
    val isSoundOn by viewModel.isSoundOn.collectAsState()
    val noiseColor by viewModel.noiseColor.collectAsState()
    val brownNoiseDepth by viewModel.brownNoiseDepth.collectAsState()
    val volume by viewModel.volume.collectAsState()

    val backgroundColor = if (isPoweredOn) effectiveColor else WarmBlack

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(WindowInsets.systemBars.asPaddingValues())
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            PowerButton(
                isPoweredOn = isPoweredOn,
                onToggle = { viewModel.togglePower() }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Nightlight",
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            ColorTemperatureSlider(
                colorTemp = colorTemp,
                onColorTempChange = { viewModel.setColorTemp(it) },
                enabled = isPoweredOn,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "${(brightness * 100).toInt()}%",
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            BrightnessSlider(
                brightness = brightness,
                onBrightnessChange = { viewModel.setBrightness(it) },
                enabled = isPoweredOn,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            AudioModeSelector(
                audioMode = audioMode,
                isSoundOn = isSoundOn,
                onSoundToggle = { viewModel.toggleSound() },
                onAudioModeChange = { viewModel.setAudioMode(it) }
            )

            if (audioMode == com.nightlight.app.model.AudioMode.NOISE) {
                Spacer(modifier = Modifier.height(16.dp))

                NoiseColorSelector(
                    noiseColor = noiseColor,
                    onNoiseColorChange = { viewModel.setNoiseColor(it) }
                )

                if (noiseColor == com.nightlight.app.model.NoiseColor.BROWN) {
                    Spacer(modifier = Modifier.height(16.dp))

                    BrownNoiseDepthSlider(
                        depth = brownNoiseDepth,
                        onDepthChange = { viewModel.setBrownNoiseDepth(it) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            VolumeSlider(
                volume = volume,
                onVolumeChange = { viewModel.setVolume(it) }
            )
        }
    }
}
