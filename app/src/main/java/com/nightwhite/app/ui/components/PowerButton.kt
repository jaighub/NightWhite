package com.nightwhite.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.nightwhite.app.R
import com.nightwhite.app.ui.theme.WarmAmber

@Composable
fun PowerButton(
    isPoweredOn: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(
                if (isPoweredOn) WarmAmber else Color(0xFF4A3B32)
            )
            .clickable { onToggle() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isPoweredOn) stringResource(R.string.on) else stringResource(R.string.off),
            color = if (isPoweredOn) Color.White else Color(0xFFE8D5C4),
            fontSize = 20.sp
        )
    }
}
