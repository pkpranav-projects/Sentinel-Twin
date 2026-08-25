package com.example.sentineltwin.twin

import com.example.sentineltwin.domain.BaselineMetric
import com.example.sentineltwin.domain.TwinSnapshot

class BaselineEngine {
    companion object {
        const val MINIMUM_SAMPLES = 10
    }

    fun calculateBaseline(history: List<TwinSnapshot>): List<BaselineMetric> {
        val count = history.size
        
        // Extract metrics
        val workloads = history.mapNotNull { it.estimatedWorkload.value }
        val temps = history.mapNotNull { it.temperatureCelsius.value }
        val memory = history.mapNotNull { it.memoryUsedPercent.value }

        return listOf(
            calculateMetric("Workload", count, workloads),
            calculateMetric("Temperature", count, temps),
            calculateMetric("Memory", count, memory)
        )
    }
    
    fun getBaselineState(historySize: Int): String {
        return if (historySize < MINIMUM_SAMPLES) "LEARNING ($historySize/$MINIMUM_SAMPLES)" else "ESTABLISHED"
    }

    private fun calculateMetric(name: String, sampleCount: Int, values: List<Double>): BaselineMetric {
        if (values.isEmpty()) {
            return BaselineMetric(name, sampleCount, null, null, null, System.currentTimeMillis())
        }
        val mean = values.average()
        val variance = values.map { (it - mean) * (it - mean) }.average()
        val stdDev = Math.sqrt(variance)

        // Simple slope (first vs last)
        val slope = if (values.size > 1) values.last() - values.first() else 0.0

        return BaselineMetric(
            metric = name,
            sampleCount = sampleCount,
            mean = mean,
            standardDeviation = stdDev,
            recentSlope = slope,
            updatedAtMs = System.currentTimeMillis()
        )
    }
}
