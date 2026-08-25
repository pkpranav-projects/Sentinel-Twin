package com.example.sentineltwin.twin

import com.example.sentineltwin.domain.BaselineMetric
import com.example.sentineltwin.domain.TwinSnapshot

class DriftEngine {
    
    fun detectDrift(current: TwinSnapshot, baselines: List<BaselineMetric>): Boolean {
        // Drift detected if any metric deviates from mean by more than 2 std-dev,
        // and baseline has minimum samples.
        
        var isDrifting = false
        
        for (baseline in baselines) {
            if (baseline.sampleCount < BaselineEngine.MINIMUM_SAMPLES) continue
            if (baseline.mean == null || baseline.standardDeviation == null) continue
            
            val currentValue = when (baseline.metric) {
                "Workload" -> current.estimatedWorkload.value
                "Temperature" -> current.temperatureCelsius.value
                "Memory" -> current.memoryUsedPercent.value
                else -> null
            }
            
            if (currentValue != null) {
                val zScore = Math.abs(currentValue - baseline.mean) / Math.max(baseline.standardDeviation, 0.1)
                if (zScore > 2.0) {
                    isDrifting = true
                }
            }
        }
        
        return isDrifting
    }
}
