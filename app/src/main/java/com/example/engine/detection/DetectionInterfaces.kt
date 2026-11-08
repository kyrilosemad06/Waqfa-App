package com.example.engine.detection

import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.data.model.DetectionConfidence
import com.example.data.model.DetectionEvent
import com.example.data.model.DetectionStatus
import com.example.data.model.EventType
import com.example.data.model.TargetApp

interface DetectionProvider {
    val targetApp: TargetApp
    fun isAvailable(): Boolean
    fun getDetectionStatus(): DetectionStatus
    fun getConfidenceLevel(): DetectionConfidence
    fun processAccessibilityEvent(event: AccessibilityEvent, rootNode: AccessibilityNodeInfo?): DetectionCandidate?
}

data class DetectionCandidate(
    val app: TargetApp,
    val eventType: EventType,
    val confidence: DetectionConfidence,
    val contentHash: String?,
    val timestamp: Long = System.currentTimeMillis(),
    val dwellTimeMs: Long = 0L,
    val note: String = ""
)
