package com.nightlight.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSoundOn) WarmAmber else Color(0xFF4A3B32)
                    )
                    .clickable { onSoundToggle() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isSoundOn) "ON" else "OFF",
                    color = if (isSoundOn) Color.White else Color(0xFFE8D5C4),
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            options.forEach { (mode, label) ->
                val isSelected = audioMode == mode
                OutlinedButton(
                    onClick = { onAudioModeChange(mode) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFB347)) else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3E2C21)),
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
