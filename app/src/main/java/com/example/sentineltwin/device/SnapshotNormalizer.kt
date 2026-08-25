package com.example.sentineltwin.device

import android.content.Context
import com.example.sentineltwin.domain.Confidence
import com.example.sentineltwin.domain.DataProvenance
import com.example.sentineltwin.domain.SignalQuality
import com.example.sentineltwin.domain.SignalValue
import com.example.sentineltwin.domain.TwinSnapshot

class SnapshotNormalizer(private val context: Context) {
    private val adapters = DeviceSignalAdapters(context)

    fun createSnapshot(): TwinSnapshot {
        val (batteryLevel, chargingState) = adapters.getBatteryLevelAndCharging()
        val temperature = adapters.getBatteryTemperature()
        val thermalStatus = adapters.getThermalStatus()
        val memoryPressure = adapters.getMemoryPressure()
        val networkState = adapters.getNetworkState()
        
        // Dummy data for storage right now
        val storageUsed = SignalValue(
            value = 50.0,
            provenance = DataProvenance.ESTIMATED,
            quality = SignalQuality.FRESH,
            timestampMs = System.currentTimeMillis(),
            source = "Placeholder",
            note = "To be implemented"
        )
        
        // Dummy for thermal headroom
        val thermalHeadroom = SignalValue(
            value = 0.5,
            provenance = DataProvenance.UNAVAILABLE,
            quality = SignalQuality.UNSUPPORTED,
            timestampMs = System.currentTimeMillis(),
            source = "PowerManager",
            note = "API missing"
        )

        // Estimated Workload Indicator
        val workload = estimateWorkload(batteryLevel, memoryPressure, temperature)

        return TwinSnapshot(
            id = 0,
            timestampMs = System.currentTimeMillis(),
            batteryPercent = batteryLevel,
            charging = chargingState,
            temperatureCelsius = temperature,
            thermalStatus = thermalStatus,
            thermalHeadroom = thermalHeadroom,
            estimatedWorkload = workload,
            memoryUsedPercent = memoryPressure,
            storageUsedPercent = storageUsed,
            networkState = networkState,
            baselineState = "LEARNING",
            twinHealth = null,
            confidence = Confidence.MODERATE
        )
    }

    private fun estimateWorkload(
        batteryLevel: SignalValue<Double>,
        memoryPressure: SignalValue<Double>,
        temperature: SignalValue<Double>
    ): SignalValue<Double> {
        val mem = memoryPressure.value ?: 0.0
        val temp = temperature.value ?: 30.0
        
        // Simple heuristic: normalize temp (30-45C) and memory (0-100%)
        val tempWeight = ((temp - 30.0) / 15.0).coerceIn(0.0, 1.0) * 50.0
        val memWeight = (mem / 100.0) * 50.0
        val estimated = tempWeight + memWeight

        return SignalValue(
            value = estimated,
            provenance = DataProvenance.ESTIMATED,
            quality = SignalQuality.FRESH,
            timestampMs = System.currentTimeMillis(),
            source = "SnapshotNormalizer",
            note = "Formula: f(memory, temp)"
        )
    }
}
