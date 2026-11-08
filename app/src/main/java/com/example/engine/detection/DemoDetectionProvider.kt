package com.example.engine.detection

import com.example.data.model.DetectionConfidence
import com.example.data.model.EventType
import com.example.data.model.TargetApp
import kotlinx.coroutines.delay
import java.util.UUID

class DemoDetectionProvider {

    /**
     * Generates a realistic simulation candidate for testing counter engine
     * behavior, deduplication, confidence categorization, and limit enforcement.
     */
    fun createSimulatedCandidate(
        app: TargetApp,
        confidence: DetectionConfidence = app.defaultConfidence,
        forceDuplicateHash: String? = null
    ): DetectionCandidate {
        val hash = forceDuplicateHash ?: "demo_vid_${UUID.randomUUID().toString().take(6)}"
        return DetectionCandidate(
            app = app,
            eventType = EventType.DEMO_SIMULATION,
            confidence = confidence,
            contentHash = hash,
            dwellTimeMs = (2500..8000).random().toLong(),
            timestamp = System.currentTimeMillis(),
            note = "Interactive simulation event"
        )
    }

    /**
     * Simulates rapid skipping where user scrolls within 1.2 seconds (should trigger deduplication debounce)
     */
    fun createRapidSkipCandidate(app: TargetApp): DetectionCandidate {
        return DetectionCandidate(
            app = app,
            eventType = EventType.SWIPE_DWELL,
            confidence = DetectionConfidence.HEURISTIC_ESTIMATE,
            contentHash = "rapid_skip_${System.currentTimeMillis() / 1000}",
            dwellTimeMs = 800L,
            timestamp = System.currentTimeMillis(),
            note = "Rapid scroll skip (< 1.2s dwell)"
        )
    }
}
