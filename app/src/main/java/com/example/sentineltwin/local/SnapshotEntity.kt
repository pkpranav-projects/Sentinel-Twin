package com.example.sentineltwin.local

data class SnapshotEntity(
    val id: Long = 0,
    val timestampMs: Long,
    
    // Flattened simple values for baseline calculating
    val batteryPercent: Double?,
    val isCharging: Boolean?,
    val temperatureCelsius: Double?,
    val thermalStatusStr: String?,
    val memoryUsedPercent: Double?,
    val networkStateStr: String?,
    val estimatedWorkload: Double?,
    
    val baselineState: String,
    val confidence: String
)
