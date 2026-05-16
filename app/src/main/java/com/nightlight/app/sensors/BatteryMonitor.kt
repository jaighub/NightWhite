package com.nightlight.app.sensors

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.nightlight.app.R

class BatteryMonitor(
    private val context: Context,
    private val onBatteryCritical: () -> Unit
) {
    private val handler = Handler(Looper.getMainLooper())
    private var isTriggered = false
    private var isRegistered = false

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(receiverContext: Context?, intent: Intent?) {
            if (intent == null) return
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            if (level == -1 || scale == -1) return

            val percent = level * 100 / scale
            if (percent <= 15 && !isTriggered) {
                isTriggered = true
                Toast.makeText(context, context.getString(R.string.battery_low), Toast.LENGTH_LONG).show()
                handler.postDelayed({
                    onBatteryCritical()
                }, 10000)
            } else if (percent > 15) {
                isTriggered = false
                handler.removeCallbacksAndMessages(null)
            }
        }
    }

    fun register() {
        if (isRegistered) return
        isRegistered = true
        isTriggered = false
        handler.removeCallbacksAndMessages(null)
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        context.registerReceiver(batteryReceiver, filter)
    }

    fun unregister() {
        if (!isRegistered) return
        isRegistered = false
        context.unregisterReceiver(batteryReceiver)
        handler.removeCallbacksAndMessages(null)
        isTriggered = false
    }
}
