package com.example.sentineltwin.reasoning

import com.example.sentineltwin.domain.*

class EvidenceBuilder {

    fun buildEvidence(question: String, current: TwinSnapshot, baselines: List<BaselineMetric>): EvidenceBundle {
        val evidenceList = mutableListOf<EvidenceItem>()
        val missingSignals = mutableListOf<String>()

        val timestamp = System.currentTimeMillis()

        // 1. Check thermal status
        if (current.thermalStatus.value != null && current.thermalStatus.value != "NORMAL") {
            evidenceList.add(
                EvidenceItem(
                    metric = "Thermal Status",
                    currentValue = current.thermalStatus.value.toString(),
                    comparison = "Elevated from NORMAL",
                    relationshipLabel = "Direct Observation",
                    provenance = current.thermalStatus.provenance,
                    quality = current.thermalStatus.quality,
                    timestampMs = timestamp
                )
            )
        } else if (current.thermalStatus.value == null) {
            missingSignals.add("Thermal Status")
        }

        // 2. Check temperature
        val tempBaseline = baselines.find { it.metric == "Temperature" }
        if (current.temperatureCelsius.value != null && tempBaseline?.mean != null) {
            if (current.temperatureCelsius.value > tempBaseline.mean + 5.0) {
                evidenceList.add(
                    EvidenceItem(
                        metric = "Temperature",
                        currentValue = "${current.temperatureCelsius.value}°C",
                        comparison = "Higher than historical mean (${tempBaseline.mean}°C)",
                        relationshipLabel = "Primary Symptom",
                        provenance = current.temperatureCelsius.provenance,
                        quality = current.temperatureCelsius.quality,
                        timestampMs = timestamp
                    )
                )
            }
        } else if (current.temperatureCelsius.value == null) {
            missingSignals.add("Battery Temperature")
        }

        // 3. Check workload
        val workBaseline = baselines.find { it.metric == "Workload" }
        if (current.estimatedWorkload.value != null && workBaseline?.mean != null) {
            if (current.estimatedWorkload.value > workBaseline.mean * 1.5) {
                evidenceList.add(
                    EvidenceItem(
                        metric = "Workload Indicator",
                        currentValue = "${current.estimatedWorkload.value.toInt()}%",
                        comparison = "Elevated compared to baseline (${workBaseline.mean.toInt()}%)",
                        relationshipLabel = "Correlated Contributor",
                        provenance = current.estimatedWorkload.provenance,
                        quality = current.estimatedWorkload.quality,
                        timestampMs = timestamp
                    )
                )
            }
        }

        return EvidenceBundle(
            question = question,
            snapshot = current,
            baseline = baselines,
            evidence = evidenceList,
            missingSignals = missingSignals,
            createdAtMs = timestamp
        )
    }
}
