package com.example.sentineltwin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.sentineltwin.theme.SentinelTwinTheme
import com.example.sentineltwin.device.SnapshotNormalizer
import com.example.sentineltwin.twin.TwinRepository
import com.example.sentineltwin.ui.TwinScreen
import com.example.sentineltwin.ui.TimelineScreen
import com.example.sentineltwin.ui.InvestigateScreen
import com.example.sentineltwin.ui.SandboxScreen
import com.example.sentineltwin.domain.TwinSnapshot
import com.example.sentineltwin.domain.Explanation
import com.example.sentineltwin.local.SnapshotEntity
import com.example.sentineltwin.demo.DemoScenario
import com.example.sentineltwin.reasoning.EvidenceBuilder
import com.example.sentineltwin.reasoning.DeterministicReasoner
import com.example.sentineltwin.twin.BaselineEngine
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

var isDemoModeActive = false

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    enableEdgeToEdge()
    setContent {
      SentinelTwinTheme { 
        var currentTab by remember { mutableStateOf(0) }
        val normalizer = remember { SnapshotNormalizer(this@MainActivity) }
        val repository = remember { TwinRepository(this@MainActivity, normalizer) }
        val demoScenario = remember { DemoScenario() }
        val reasoner = remember { DeterministicReasoner() }
        
        var snapshot by remember { mutableStateOf<TwinSnapshot?>(null) }
        var history by remember { mutableStateOf<List<TwinSnapshot>>(emptyList()) }
        var explanation by remember { mutableStateOf<Explanation?>(null) }
        LaunchedEffect(Unit) {
            while (true) {
                if (!isDemoModeActive) {
                    snapshot = repository.saveCurrentSnapshot()
                    history = repository.getRecentHistory()
                } else {
                    snapshot = demoScenario.getSyntheticSnapshot()
                    // Create a synthetic history that grows with the demo
                    val fakeHistory = history.toMutableList()
                    fakeHistory.add(snapshot!!)
                    if (fakeHistory.size > 10) fakeHistory.removeAt(0)
                    history = fakeHistory
                }
                delay(3000L) // Poll every 3 seconds
            }
        }

        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        icon = { },
                        label = { Text("Live Twin") },
                        selected = currentTab == 0,
                        onClick = { currentTab = 0 }
                    )
                    NavigationBarItem(
                        icon = { },
                        label = { Text("History") },
                        selected = currentTab == 1,
                        onClick = { currentTab = 1 }
                    )
                    NavigationBarItem(
                        icon = { },
                        label = { Text("Investigate") },
                        selected = currentTab == 2,
                        onClick = { currentTab = 2 }
                    )
                    NavigationBarItem(
                        icon = { },
                        label = { Text("Sandbox") },
                        selected = currentTab == 3,
                        onClick = { currentTab = 3 }
                    )
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        isDemoModeActive = true
                        demoScenario.advanceStage()
                    },
                    containerColor = Color(0xFF42A5F5),
                    contentColor = Color.White
                ) {
                    Text(if (isDemoModeActive) "Next Stage" else "Start Demo", modifier = Modifier.padding(horizontal = 16.dp), fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
                }
            }
        ) { innerPadding ->
            Surface(modifier = Modifier.fillMaxSize().padding(innerPadding), color = MaterialTheme.colorScheme.background) { 
                when (currentTab) {
                    0 -> {
                        val entities = history.map { 
                            SnapshotEntity(
                                id = it.id, 
                                timestampMs = it.timestampMs,
                                batteryPercent = it.batteryPercent.value,
                                isCharging = it.charging.value,
                                temperatureCelsius = it.temperatureCelsius.value,
                                thermalStatusStr = it.thermalStatus.value,
                                memoryUsedPercent = it.memoryUsedPercent.value,
                                networkStateStr = it.networkState.value,
                                estimatedWorkload = it.estimatedWorkload.value,
                                baselineState = it.baselineState,
                                confidence = it.confidence.name
                            )
                        }
                        TwinScreen(snapshot = snapshot, history = entities)
                    }
                    1 -> {
                        val entities = history.map { 
                            SnapshotEntity(
                                id = it.id, 
                                timestampMs = it.timestampMs,
                                batteryPercent = it.batteryPercent.value,
                                isCharging = it.charging.value,
                                temperatureCelsius = it.temperatureCelsius.value,
                                thermalStatusStr = it.thermalStatus.value,
                                memoryUsedPercent = it.memoryUsedPercent.value,
                                networkStateStr = it.networkState.value,
                                estimatedWorkload = it.estimatedWorkload.value,
                                baselineState = it.baselineState,
                                confidence = it.confidence.name
                            )
                        }
                        TimelineScreen(history = entities) {
                            val baselines = BaselineEngine().calculateBaseline(history)
                            val bundle = EvidenceBuilder().buildEvidence("Why is this event abnormal?", it.let { s -> history.find { h -> h.timestampMs == s.timestampMs } ?: snapshot!! }, baselines)
                            explanation = DeterministicReasoner().explain(bundle)
                            currentTab = 2 
                        }
                    }
                    2 -> InvestigateScreen(explanation = explanation, onSimulateClick = { currentTab = 3 })
                    3 -> SandboxScreen()
                }
            } 
        }
      }
    }
  }
}
