package com.nightwhite.app.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

class FaceDownDetector(
    context: Context,
    private val onFaceDown: (Boolean) -> Unit
) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var lastState: Boolean? = null

    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]

            val isFaceDown = z < -5f

            if (lastState != isFaceDown) {
                lastState = isFaceDown
                Log.d("ProximitySensor", "accel x=$x y=$y z=$z faceDown=$isFaceDown")
                onFaceDown(isFaceDown)
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }

    fun register() {
        accelerometer?.let {
            Log.d("ProximitySensor", "Using accelerometer for face-down detection")
            sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_UI)
        } ?: Log.w("ProximitySensor", "No accelerometer available")
    }

    fun unregister() {
        sensorManager.unregisterListener(sensorEventListener)
    }
}
