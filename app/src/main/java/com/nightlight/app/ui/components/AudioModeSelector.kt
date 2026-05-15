package com.nightlight.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nightlight.app.model.AudioMode
import com.nightlight.app.ui.theme.WarmAmber

@Composable
fun AudioModeSelector(
    audioMode: AudioMode,
    isSoundOn: Boolean,
    onSoundToggle: () -> Unit,
    onAudioModeChange: (AudioMode) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        AudioMode.NOISE to "Noise",
        AudioMode.LULLABY to "Lullaby"
    )

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
                text = "Sound",
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.labelLarge,
                color = Color.White
            )

            Spacer(modifier = Modifier.width(8.dp))

            Switch(
                checked = isSoundOn,
                onCheckedChange = { onSoundToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = WarmAmber,
                    checkedTrackColor = WarmAmber.copy(alpha = 0.5f),
                    uncheckedThumbColor = Color(0xFF8A7A6D),
                    uncheckedTrackColor = Color(0xFF3E2C21)
                )
            )
        }

        if (isSoundOn) {
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                options.forEach { (mode, label) ->
                    val isSelected = audioMode == mode
                    OutlinedButton(
                        onClick = { onAudioModeChange(mode) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        border = if (isSelected) BorderStroke(2.dp, Color(0xFFFFB347)) else BorderStroke(1.dp, Color(0xFF3E2C21)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSelected) Color(0xFFFFB347).copy(alpha = 0.3f) else Color.Transparent,
                            contentColor = Color.White,
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
    }
}
