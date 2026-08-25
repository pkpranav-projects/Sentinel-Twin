package com.example.sentineltwin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SandboxScreen() {
    var predictionText by remember { mutableStateOf<String?>(null) }
    var isSimulating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF121212)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Simulation Sandbox",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Run 'What-If' scenarios against your Digital Twin's baseline to predict outcomes.",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Scenarios
            SimulationCard(
                title = "Install Heavy 3D Game",
                desc = "Simulates downloading and launching a resource-intensive game.",
                onSimulate = {
                    isSimulating = true
                    predictionText = null
                    scope.launch {
                        delay(1500)
                        isSimulating = false
                        predictionText = "Prediction: Based on your thermal baseline, sustained gameplay will raise device temperature by +4.2°C and decrease battery life by 18% per hour. Minor throttling expected after 45 mins."
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            SimulationCard(
                title = "Enable 5G Hotspot in Car",
                desc = "Simulates high network activity in an environment with high ambient temperature.",
                onSimulate = {
                    isSimulating = true
                    predictionText = null
                    scope.launch {
                        delay(1500)
                        isSimulating = false
                        predictionText = "Prediction: Critical thermal drift likely. The combination of charging, 5G networking, and ambient heat will exceed your 42°C baseline threshold within 12 minutes."
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Results Box
            if (isSimulating) {
                CircularProgressIndicator(color = Color(0xFF42A5F5), modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally))
            } else if (predictionText != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E1E1E), RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text("TWIN PREDICTION", color = Color(0xFF81C784), fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(predictionText!!, color = Color.White, fontSize = 14.sp, lineHeight = 20.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SimulationCard(title: String, desc: String, onSimulate: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E1E1E), RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(text = title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = desc, color = Color.Gray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onSimulate,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF42A5F5))
            ) {
                Text("Simulate Action", color = Color.White)
            }
        }
    }
}
