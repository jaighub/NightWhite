package com.nightwhite.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nightwhite.app.BuildConfig
import com.nightwhite.app.R
import com.nightwhite.app.ui.components.AudioModeSelector
import com.nightwhite.app.ui.components.BrightnessSlider
import com.nightwhite.app.ui.components.BrownNoiseDepthSlider
import com.nightwhite.app.ui.components.ColorTemperatureSlider
import com.nightwhite.app.ui.components.LullabySongSelector
import com.nightwhite.app.ui.components.NoiseColorSelector
import com.nightwhite.app.ui.components.PowerButton
import com.nightwhite.app.ui.components.SleepTimerSelector
import com.nightwhite.app.ui.components.VolumeSlider
import com.nightwhite.app.ui.theme.WarmBlack
import com.nightwhite.app.viewmodel.NightlightViewModel

@Composable
fun MainScreen(viewModel: NightlightViewModel) {
    val isPoweredOn by viewModel.isPoweredOn.collectAsState()
    val brightness by viewModel.brightness.collectAsState()
    val colorTemp by viewModel.colorTemp.collectAsState()
    val effectiveColor by viewModel.effectiveColor.collectAsState()
    val audioMode by viewModel.audioMode.collectAsState()
    val lullabySong by viewModel.lullabySong.collectAsState()
    val isSoundOn by viewModel.isSoundOn.collectAsState()
    val noiseColor by viewModel.noiseColor.collectAsState()
    val brownNoiseDepth by viewModel.brownNoiseDepth.collectAsState()
    val volume by viewModel.volume.collectAsState()
    val showControls by viewModel.showControls.collectAsState()
    val sleepTimerMinutes by viewModel.sleepTimerMinutes.collectAsState()

    var showHelpDialog by remember { mutableStateOf(false) }

    val backgroundColor = if (isPoweredOn) effectiveColor else Color.Black

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .clickable { viewModel.resetControlsTimer() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.systemBars.asPaddingValues())
                .padding(horizontal = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { showHelpDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = stringResource(R.string.how_to_use),
                        tint = Color.White.copy(alpha = 0.6f)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                PowerButton(
                    isPoweredOn = isPoweredOn,
                    onToggle = { viewModel.togglePower() }
                )

                Spacer(modifier = Modifier.height(16.dp))

                AnimatedVisibility(
                    visible = showControls,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        SleepTimerSelector(
                            minutes = sleepTimerMinutes,
                            onMinutesChange = { viewModel.setSleepTimer(it) },
                            isPoweredOn = isPoweredOn
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        ColorTemperatureSlider(
                            colorTemp = colorTemp,
                            onColorTempChange = { viewModel.setColorTemp(it) },
                            isPoweredOn = isPoweredOn,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        BrightnessSlider(
                            brightness = brightness,
                            onBrightnessChange = { viewModel.setBrightness(it) },
                            isPoweredOn = isPoweredOn,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        HorizontalDivider(
                            color = Color.White.copy(alpha = 0.15f),
                            thickness = 1.dp,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        AudioModeSelector(
                            audioMode = audioMode,
                            isSoundOn = isSoundOn,
                            onSoundToggle = { viewModel.toggleSound() },
                            onAudioModeChange = { viewModel.setAudioMode(it) },
                            isPoweredOn = isPoweredOn
                        )

                        if (audioMode == com.nightwhite.app.model.AudioMode.NOISE) {
                            Spacer(modifier = Modifier.height(16.dp))

                            NoiseColorSelector(
                                noiseColor = noiseColor,
                                onNoiseColorChange = { viewModel.setNoiseColor(it) },
                                isPoweredOn = isPoweredOn
                            )

                            if (noiseColor == com.nightwhite.app.model.NoiseColor.BROWN) {
                                Spacer(modifier = Modifier.height(16.dp))

                                BrownNoiseDepthSlider(
                                    depth = brownNoiseDepth,
                                    onDepthChange = { viewModel.setBrownNoiseDepth(it) },
                                    isPoweredOn = isPoweredOn
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.height(16.dp))

                            LullabySongSelector(
                                song = lullabySong,
                                onSongChange = { viewModel.setLullabySong(it) },
                                isPoweredOn = isPoweredOn
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        VolumeSlider(
                            volume = volume,
                            onVolumeChange = { viewModel.setVolume(it) },
                            isPoweredOn = isPoweredOn
                        )
                    }
                }

                AnimatedVisibility(
                    visible = !showControls && isPoweredOn,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = stringResource(R.string.tap_to_adjust),
                        color = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }

        if (showHelpDialog) {
            InfoDialog(onDismiss = { showHelpDialog = false })
        }
    }
}

@Composable
fun InfoDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.how_to_use)) },
        containerColor = Color(0.9f, 0.9f, 0.9f, 0.9f),
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                HelpItem(icon = "💡", text = stringResource(R.string.help_power))
                Spacer(modifier = Modifier.height(12.dp))
                HelpItem(icon = "📱", text = stringResource(R.string.help_flip))
                Spacer(modifier = Modifier.height(12.dp))
                HelpItem(icon = "👆", text = stringResource(R.string.help_controls))
                Spacer(modifier = Modifier.height(12.dp))
                HelpItem(icon = "⏰", text = stringResource(R.string.help_timer))
                Spacer(modifier = Modifier.height(12.dp))
                HelpItem(icon = "🔽", text = stringResource(R.string.help_tile))
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "${stringResource(R.string.version)} ${BuildConfig.VERSION_NAME}",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Got it")
            }
        }
    )
}

@Composable
fun HelpItem(icon: String, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icon, fontSize = 18.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
    }
}
