package com.nightwhite.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.nightwhite.app.R
import com.nightwhite.app.ui.theme.WarmAmber

@Composable
fun BrightnessSlider(
    brightness: Float,
    onBrightnessChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isPoweredOn: Boolean = false
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.brightness),
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.labelLarge,
                color = if (enabled) {
                    if (isPoweredOn) Color.Black else Color.White
                } else Color(0xFF8A7A6D)
            )
        }

        Slider(
            value = brightness,
            onValueChange = onBrightnessChange,
            enabled = enabled,
            valueRange = 0f..1f,
            steps = 9,
            colors = SliderDefaults.colors(
                thumbColor = WarmAmber,
                activeTrackColor = WarmAmber,
                inactiveTrackColor = Color(0xFF3E2C21),
                disabledThumbColor = WarmAmber.copy(alpha = 0.5f),
                disabledActiveTrackColor = WarmAmber.copy(alpha = 0.5f),
                disabledInactiveTrackColor = Color(0xFF3E2C21).copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
