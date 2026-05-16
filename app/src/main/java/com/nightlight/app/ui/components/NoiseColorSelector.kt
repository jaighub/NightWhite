package com.nightlight.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nightlight.app.R
import com.nightlight.app.model.NoiseColor

@Composable
fun NoiseColorSelector(
    noiseColor: NoiseColor,
    onNoiseColorChange: (NoiseColor) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        NoiseColor.WHITE to stringResource(R.string.white),
        NoiseColor.PINK to stringResource(R.string.pink),
        NoiseColor.BROWN to stringResource(R.string.brown)
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        options.forEach { (color, label) ->
            val isSelected = noiseColor == color
            OutlinedButton(
                onClick = { onNoiseColorChange(color) },
                enabled = enabled,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                border = if (isSelected) BorderStroke(2.dp, Color(0xFFFFB347)) else BorderStroke(1.dp, Color(0xFF3E2C21)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isSelected) Color(0xFFFFB347).copy(alpha = 0.3f) else Color.Transparent,
                    contentColor = if (enabled) Color.White else Color(0xFF8A7A6D),
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = Color(0xFF8A7A6D)
                )
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}
