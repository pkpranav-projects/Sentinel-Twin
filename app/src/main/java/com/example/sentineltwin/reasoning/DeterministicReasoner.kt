package com.example.sentineltwin.reasoning

import com.example.sentineltwin.domain.Confidence
import com.example.sentineltwin.domain.EvidenceBundle
import com.example.sentineltwin.domain.Explanation

class DeterministicReasoner {

    fun explain(bundle: EvidenceBundle): Explanation {
        // Find if workload is elevated
        val workloadElevated = bundle.evidence.any { it.metric.contains("Workload") }
        val tempElevated = bundle.evidence.any { it.metric.contains("Temperature") || it.metric.contains("Thermal") }

        val observation = if (tempElevated) {
            "I observed meaningful thermal drift compared to your personal baseline."
        } else {
            "Your phone is operating within normal baseline parameters."
        }

        val likelyCause = if (tempElevated && workloadElevated) {
            "An unusually high estimated workload is likely contributing to the elevated thermal pressure."
        } else if (tempElevated && bundle.snapshot.charging.value == true) {
            "The device is charging, which often causes a temporary increase in thermal pressure."
        } else if (tempElevated) {
            "The device temperature is elevated, but the workload indicator does not show a clear correlation. Environmental factors may be involved."
        } else {
            "No abnormal causes detected."
        }

        val recommendation = if (tempElevated && workloadElevated) {
            "Recommendation: Reduce active workload by closing heavy applications or lowering screen brightness."
        } else if (tempElevated && bundle.snapshot.charging.value == true) {
            "Recommendation: Avoid heavy usage while charging to allow the device to cool."
        } else {
            "Recommendation: Monitor for further changes."
        }

        val actionType = if (workloadElevated) "SIMULATE_REDUCE_WORKLOAD" else "NONE"

        val confidence = if (bundle.missingSignals.isNotEmpty()) Confidence.MODERATE else Confidence.HIGH

        return Explanation(
            observation = observation,
            likelyCause = likelyCause,
            evidence = bundle.evidence,
            confidence = confidence,
            recommendation = recommendation,
            actionType = actionType,
            providerLabel = "Deterministic Local Reasoner"
        )
    }
}
