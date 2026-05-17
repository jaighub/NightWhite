package com.nightlight.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
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

@Composable
fun SleepTimerSelector(
    minutes: Int,
    onMinutesChange: (Int) -> Unit,
    isPoweredOn: Boolean = false,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        0 to stringResource(R.string.timer_off),
        60 to stringResource(R.string.timer_1h),
        120 to stringResource(R.string.timer_2h),
        240 to stringResource(R.string.timer_4h),
        480 to stringResource(R.string.timer_8h)
    )

    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        options.forEach { (m, label) ->
            val isSelected = minutes == m
            OutlinedButton(
                onClick = { onMinutesChange(m) },
                modifier = Modifier
                    .widthIn(min = 64.dp)
                    .padding(end = 4.dp),
                shape = RoundedCornerShape(8.dp),
                border = if (isSelected) BorderStroke(2.dp, Color(0xFFFFB347)) else BorderStroke(1.dp, Color(0xFF3E2C21)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isSelected) Color(0xFFFFB347).copy(alpha = 0.3f) else Color.Transparent,
                    contentColor = if (isPoweredOn) Color.Black else Color.White,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = Color(0xFF8A7A6D)
                )
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1
                )
            }
        }
    }
}
