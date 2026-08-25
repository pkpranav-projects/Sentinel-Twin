package com.example.sentineltwin.demo

import com.example.sentineltwin.domain.*

enum class DemoStage {
    NORMAL_BASELINE,
    WORKLOAD_RISE,
    THERMAL_RISE,
    DRIFT_DETECTED,
    RECOVERY
}

class DemoScenario {
    var currentStage = DemoStage.NORMAL_BASELINE
        private set

    fun advanceStage() {
        currentStage = when (currentStage) {
            DemoStage.NORMAL_BASELINE -> DemoStage.WORKLOAD_RISE
            DemoStage.WORKLOAD_RISE -> DemoStage.THERMAL_RISE
            DemoStage.THERMAL_RISE -> DemoStage.DRIFT_DETECTED
            DemoStage.DRIFT_DETECTED -> DemoStage.RECOVERY
            DemoStage.RECOVERY -> DemoStage.NORMAL_BASELINE
        }
    }

    fun reset() {
        currentStage = DemoStage.NORMAL_BASELINE
    }

    fun getSyntheticSnapshot(): TwinSnapshot {
        val timestamp = System.currentTimeMillis()
        
        val batteryPercent = when (currentStage) {
            DemoStage.NORMAL_BASELINE -> 85.0
            DemoStage.WORKLOAD_RISE -> 82.0
            DemoStage.THERMAL_RISE -> 75.0
            DemoStage.DRIFT_DETECTED -> 70.0
            DemoStage.RECOVERY -> 69.0
        }

        val temp = when (currentStage) {
            DemoStage.NORMAL_BASELINE -> 32.0
            DemoStage.WORKLOAD_RISE -> 36.0
            DemoStage.THERMAL_RISE -> 41.0
            DemoStage.DRIFT_DETECTED -> 44.0
            DemoStage.RECOVERY -> 35.0
        }

        val workload = when (currentStage) {
            DemoStage.NORMAL_BASELINE -> 10.0
            DemoStage.WORKLOAD_RISE -> 85.0
            DemoStage.THERMAL_RISE -> 90.0
            DemoStage.DRIFT_DETECTED -> 95.0
            DemoStage.RECOVERY -> 15.0
        }

        val thermalStatus = if (temp > 40.0) "SEVERE" else "NORMAL"
        val isCharging = false

        return TwinSnapshot(
            id = 0,
            timestampMs = timestamp,
            batteryPercent = SignalValue(batteryPercent, DataProvenance.SIMULATED, SignalQuality.FRESH, timestamp, "Demo", null),
            charging = SignalValue(isCharging, DataProvenance.SIMULATED, SignalQuality.FRESH, timestamp, "Demo", null),
            temperatureCelsius = SignalValue(temp, DataProvenance.SIMULATED, SignalQuality.FRESH, timestamp, "Demo", null),
            thermalStatus = SignalValue(thermalStatus, DataProvenance.SIMULATED, SignalQuality.FRESH, timestamp, "Demo", null),
            thermalHeadroom = SignalValue(null, DataProvenance.UNAVAILABLE, SignalQuality.UNSUPPORTED, timestamp, "Demo", null),
            estimatedWorkload = SignalValue(workload, DataProvenance.SIMULATED, SignalQuality.FRESH, timestamp, "Demo", null),
            memoryUsedPercent = SignalValue(40.0, DataProvenance.SIMULATED, SignalQuality.FRESH, timestamp, "Demo", null),
            storageUsedPercent = SignalValue(50.0, DataProvenance.SIMULATED, SignalQuality.FRESH, timestamp, "Demo", null),
            networkState = SignalValue("WIFI_CONNECTED", DataProvenance.SIMULATED, SignalQuality.FRESH, timestamp, "Demo", null),
            baselineState = "ESTABLISHED",
            twinHealth = if (currentStage == DemoStage.DRIFT_DETECTED) 30.0 else 90.0,
            confidence = Confidence.HIGH
        )
    }
}
