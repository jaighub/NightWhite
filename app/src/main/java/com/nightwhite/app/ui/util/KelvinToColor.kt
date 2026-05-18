package com.nightwhite.app.ui.util

import androidx.compose.ui.graphics.Color
import kotlin.math.ln
import kotlin.math.pow

fun kelvinToColor(kelvin: Int): Color {
    val temp = kelvin / 100.0
    val r: Float
    val g: Float
    val b: Float

    r = if (temp <= 66) 255f else (329.698727446 * (temp - 60).pow(-0.1332047592)).toFloat()

    g = if (temp <= 66) (99.4708025861 * ln(temp) - 161.1195681661).toFloat()
        else (288.1221695283 * (temp - 60).pow(-0.0755148492)).toFloat()

    b = when {
        temp >= 66 -> 255f
        temp <= 19 -> 0f
        else -> (138.5177312231 * ln(temp - 10) - 305.0447927307).toFloat()
    }

    return Color(
        red = (r / 255f).coerceIn(0f, 1f),
        green = (g / 255f).coerceIn(0f, 1f),
        blue = (b / 255f).coerceIn(0f, 1f)
    )
}
