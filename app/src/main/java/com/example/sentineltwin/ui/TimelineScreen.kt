package com.example.sentineltwin.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.sentineltwin.local.SnapshotEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TimelineScreen(history: List<SnapshotEntity>, onEventClick: (SnapshotEntity) -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF121212)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Device History",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(history) { snapshot ->
                    TimelineCard(snapshot = snapshot, onClick = { onEventClick(snapshot) })
                }
            }
        }
    }
}

@Composable
fun TimelineCard(snapshot: SnapshotEntity, onClick: () -> Unit) {
    val isDrift = snapshot.confidence == "LOW" || (snapshot.estimatedWorkload ?: 0.0) > 80.0
    val format = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val timeString = format.format(Date(snapshot.timestampMs))

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E1E1E), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Column {
                Text(
                    text = if (isDrift) "Anomaly Detected" else "Routine Check",
                    color = if (isDrift) Color(0xFFFF5252) else Color(0xFF42A5F5),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Activity Level: ${snapshot.estimatedWorkload?.toInt() ?: "--"}%",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
            }
            Text(
                text = timeString,
                color = Color.LightGray,
                fontSize = 14.sp
            )
        }
    }
}
