package com.example.sentineltwin.device

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.PowerManager
import com.example.sentineltwin.domain.DataProvenance
import com.example.sentineltwin.domain.SignalQuality
import com.example.sentineltwin.domain.SignalValue

class DeviceSignalAdapters(private val context: Context) {

    fun getBatteryLevelAndCharging(): Pair<SignalValue<Double>, SignalValue<Boolean>> {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context.registerReceiver(null, ifilter)
        }
        val timestamp = System.currentTimeMillis()

        if (batteryStatus == null) {
            return Pair(
                SignalValue(null, DataProvenance.UNAVAILABLE, SignalQuality.ERROR, timestamp, "BatteryManager", null),
                SignalValue(null, DataProvenance.UNAVAILABLE, SignalQuality.ERROR, timestamp, "BatteryManager", null)
            )
        }

        val level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val percent = if (level != -1 && scale != -1) (level * 100.0) / scale else null

        val status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL

        return Pair(
            SignalValue(percent, DataProvenance.REAL, SignalQuality.FRESH, timestamp, "BatteryManager", null),
            SignalValue(isCharging, DataProvenance.REAL, SignalQuality.FRESH, timestamp, "BatteryManager", null)
        )
    }

    fun getBatteryTemperature(): SignalValue<Double> {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context.registerReceiver(null, ifilter)
        }
        val timestamp = System.currentTimeMillis()
        if (batteryStatus == null) {
            return SignalValue(null, DataProvenance.UNAVAILABLE, SignalQuality.ERROR, timestamp, "BatteryManager", null)
        }
        val temp = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
        // temp is in tenths of a degree centigrade
        val tempCelsius = if (temp != -1) temp / 10.0 else null

        return SignalValue(
            tempCelsius,
            if (tempCelsius != null) DataProvenance.REAL else DataProvenance.UNAVAILABLE,
            if (tempCelsius != null) SignalQuality.FRESH else SignalQuality.UNSUPPORTED,
            timestamp, "BatteryManager", null
        )
    }

    fun getThermalStatus(): SignalValue<String> {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val timestamp = System.currentTimeMillis()
        
        // Thermal status API is available on API 29+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            val status = powerManager.currentThermalStatus
            val statusString = when (status) {
                PowerManager.THERMAL_STATUS_NONE -> "NORMAL"
                PowerManager.THERMAL_STATUS_LIGHT -> "LIGHT"
                PowerManager.THERMAL_STATUS_MODERATE -> "MODERATE"
                PowerManager.THERMAL_STATUS_SEVERE -> "SEVERE"
                PowerManager.THERMAL_STATUS_CRITICAL -> "CRITICAL"
                PowerManager.THERMAL_STATUS_EMERGENCY -> "EMERGENCY"
                PowerManager.THERMAL_STATUS_SHUTDOWN -> "SHUTDOWN"
                else -> "UNKNOWN"
            }
            return SignalValue(statusString, DataProvenance.REAL, SignalQuality.FRESH, timestamp, "PowerManager", null)
        }
        return SignalValue(null, DataProvenance.UNAVAILABLE, SignalQuality.UNSUPPORTED, timestamp, "PowerManager", "API < 29")
    }

    fun getNetworkState(): SignalValue<String> {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val timestamp = System.currentTimeMillis()
        
        val network = connectivityManager.activeNetwork
        if (network == null) {
            return SignalValue("DISCONNECTED", DataProvenance.REAL, SignalQuality.FRESH, timestamp, "ConnectivityManager", null)
        }
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        if (capabilities == null) {
            return SignalValue("DISCONNECTED", DataProvenance.REAL, SignalQuality.FRESH, timestamp, "ConnectivityManager", null)
        }
        
        val isWifi = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        val isCellular = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

        val stateStr = when {
            hasInternet && isWifi -> "WIFI_CONNECTED"
            hasInternet && isCellular -> "CELLULAR_CONNECTED"
            !hasInternet -> "CONNECTED_NO_INTERNET"
            else -> "UNKNOWN"
        }
        return SignalValue(stateStr, DataProvenance.REAL, SignalQuality.FRESH, timestamp, "ConnectivityManager", null)
    }

    fun getMemoryPressure(): SignalValue<Double> {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val timestamp = System.currentTimeMillis()
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)

        val usedMem = memoryInfo.totalMem - memoryInfo.availMem
        val usedPercent = (usedMem.toDouble() / memoryInfo.totalMem.toDouble()) * 100.0

        return SignalValue(usedPercent, DataProvenance.REAL, SignalQuality.FRESH, timestamp, "ActivityManager", if (memoryInfo.lowMemory) "LOW_MEMORY_STATE" else null)
    }
}
