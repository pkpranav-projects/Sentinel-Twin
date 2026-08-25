package com.example.sentineltwin.domain

import kotlinx.serialization.Serializable

@Serializable
enum class DataProvenance { REAL, ESTIMATED, SIMULATED, PREDICTED, UNAVAILABLE }

@Serializable
enum class SignalQuality {
    FRESH, STALE, PARTIAL, PERMISSION_DENIED, UNSUPPORTED, ERROR
}

enum class TwinEventType {
    SNAPSHOT, DRIFT_DETECTED, INVESTIGATION, RECOMMENDATION,
    ACTION, VERIFICATION, DEMO
}

@Serializable
enum class Confidence { LOW, MODERATE, HIGH }

@Serializable
data class SignalValue<T>(
    val value: T?,
    val provenance: DataProvenance,
    val quality: SignalQuality,
    val timestampMs: Long,
    val source: String?,
    val note: String?
)

@Serializable
data class TwinSnapshot(
    val id: Long = 0,
    val timestampMs: Long,
    val batteryPercent: SignalValue<Double>,
    val charging: SignalValue<Boolean>,
    val temperatureCelsius: SignalValue<Double>,
    val thermalStatus: SignalValue<String>,
    val thermalHeadroom: SignalValue<Double>,
    val estimatedWorkload: SignalValue<Double>,
    val memoryUsedPercent: SignalValue<Double>,
    val storageUsedPercent: SignalValue<Double>,
    val networkState: SignalValue<String>,
    val baselineState: String,
    val twinHealth: Double?,
    val confidence: Confidence
)

@Serializable
data class BaselineMetric(
    val metric: String,
    val sampleCount: Int,
    val mean: Double?,
    val standardDeviation: Double?,
    val recentSlope: Double?,
    val updatedAtMs: Long
)

data class InvestigationPlan(
    val intent: String,
    val requiredData: List<String>,
    val allowedActions: List<String>,
    val rationale: String
)

@Serializable
data class EvidenceItem(
    val metric: String,
    val currentValue: String,
    val comparison: String,
    val relationshipLabel: String,
    val provenance: DataProvenance,
    val quality: SignalQuality,
    val timestampMs: Long
)

@Serializable
data class EvidenceBundle(
    val question: String,
    val snapshot: TwinSnapshot,
    val baseline: List<BaselineMetric>,
    val evidence: List<EvidenceItem>,
    val missingSignals: List<String>,
    val createdAtMs: Long
)

@Serializable
data class Explanation(
    val observation: String,
    val likelyCause: String,
    val evidence: List<EvidenceItem>,
    val confidence: Confidence,
    val recommendation: String,
    val actionType: String,
    val providerLabel: String
)
