package com.nightlight.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.nightlight.app.R
import com.nightlight.app.ui.theme.WarmAmber

@Composable
fun VolumeSlider(
    volume: Float,
    onVolumeChange: (Float) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.volume),
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.labelLarge,
            color = if (enabled) Color.White else Color(0xFF8A7A6D)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Slider(
            value = volume,
            onValueChange = onVolumeChange,
            enabled = enabled,
            valueRange = 0f..1f,
            steps = 9,
            colors = SliderDefaults.colors(
                thumbColor = WarmAmber,
                activeTrackColor = WarmAmber.copy(alpha = 0.5f),
                inactiveTrackColor = Color(0xFF3E2C21)
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
