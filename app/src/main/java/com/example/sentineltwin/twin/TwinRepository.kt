package com.example.sentineltwin.twin

import android.content.Context
import android.content.SharedPreferences
import com.example.sentineltwin.device.SnapshotNormalizer
import com.example.sentineltwin.domain.TwinSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

class TwinRepository(
    private val context: Context,
    private val normalizer: SnapshotNormalizer
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("twin_history", Context.MODE_PRIVATE)
    
    private val _latestSnapshotFlow = MutableStateFlow<TwinSnapshot?>(null)
    val latestSnapshotFlow: Flow<TwinSnapshot?> = _latestSnapshotFlow.asStateFlow()

    private val json = Json { ignoreUnknownKeys = true }

    init {
        val history = loadHistory()
        if (history.isNotEmpty()) {
            _latestSnapshotFlow.value = history.last()
        }
    }

    suspend fun saveCurrentSnapshot(): TwinSnapshot {
        val snapshot = normalizer.createSnapshot()
        _latestSnapshotFlow.value = snapshot
        
        // Save to SharedPreferences
        val history = loadHistory().toMutableList()
        history.add(snapshot)
        
        // Keep only last 100
        if (history.size > 100) {
            history.removeAt(0)
        }
        
        prefs.edit().putString("history", json.encodeToString(history)).apply()
        
        return snapshot
    }

    fun getRecentHistory(): List<TwinSnapshot> {
        return loadHistory()
    }
    
    private fun loadHistory(): List<TwinSnapshot> {
        val jsonStr = prefs.getString("history", null) ?: return emptyList()
        return try {
            json.decodeFromString(jsonStr)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
