package com.example.sentineltwin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sentineltwin.domain.Explanation
import com.example.sentineltwin.domain.EvidenceItem

@Composable
fun InvestigateScreen(explanation: Explanation?, onSimulateClick: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF121212)) {
        if (explanation == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("Analyzing phone health...", color = Color.Gray, fontWeight = FontWeight.Medium)
            }
            return@Surface
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Analysis Report",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Text(
                text = "Provider: ${explanation.providerLabel}",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Observation & Cause
            Text("WHAT WE NOTICED", color = Color(0xFF42A5F5), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
            Text(explanation.observation, color = Color.White, modifier = Modifier.padding(top = 4.dp, bottom = 16.dp))

            Text("LIKELY CAUSE", color = Color(0xFF42A5F5), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
            Text(explanation.likelyCause, color = Color.White, modifier = Modifier.padding(top = 4.dp, bottom = 24.dp))

            // Evidence
            Text("EVIDENCE", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 8.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                items(explanation.evidence) { item ->
                    EvidenceCard(item)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Recommendation
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E1E1E), RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text("SUGGESTED ACTION", color = Color(0xFF81C784), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
                    Text(explanation.recommendation, color = Color.White, modifier = Modifier.padding(top = 4.dp, bottom = 12.dp))
                    
                    if (explanation.actionType == "SIMULATE_REDUCE_WORKLOAD") {
                        Button(
                            onClick = onSimulateClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF42A5F5))
                        ) {
                            Text("Simulate Action", color = Color.White, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EvidenceCard(item: EvidenceItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF252525), RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(item.metric, color = Color.White, fontWeight = FontWeight.Medium)
            Text(item.comparison, color = Color.LightGray, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
        }
    }
}
