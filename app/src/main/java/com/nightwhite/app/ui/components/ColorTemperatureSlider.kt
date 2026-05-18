package com.nightwhite.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.nightwhite.app.R
import com.nightwhite.app.ui.theme.WarmAmber
import kotlin.math.roundToInt

@Composable
fun ColorTemperatureSlider(
    colorTemp: Int,
    onColorTempChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isPoweredOn: Boolean = false
) {
    val sliderValue = ((colorTemp - 1900) / 4600f).coerceIn(0f, 1f)

    val label = when {
        sliderValue < 0.33f -> stringResource(R.string.warmer)
        sliderValue > 0.66f -> stringResource(R.string.cooler)
        else -> ""
    }

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
                text = stringResource(R.string.warmer),
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.labelLarge,
                color = if (enabled) {
                    if (isPoweredOn) Color(0xFF8B5A2B) else Color(0xFFFFB347)
                } else Color(0xFF8A7A6D)
            )

            Spacer(modifier = Modifier.weight(1f))

            if (label.isNotEmpty()) {
                Text(
                    text = label,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (enabled) {
                        if (isPoweredOn) Color.Black else Color.White
                    } else Color(0xFF8A7A6D)
                )
                Spacer(modifier = Modifier.weight(1f))
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            Text(
                text = stringResource(R.string.cooler),
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.labelLarge,
                color = if (enabled) {
                    if (isPoweredOn) Color(0xFF4A6B8A) else Color(0xFF87CEEB)
                } else Color(0xFF8A7A6D)
            )
        }

        Slider(
            value = sliderValue,
            onValueChange = { ratio ->
                val temp = 1900 + (ratio * 4600).roundToInt()
                onColorTempChange(temp)
            },
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
