package com.example.engine.detection

import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.example.data.model.DetectionConfidence
import com.example.data.model.DetectionStatus
import com.example.data.model.EventType
import com.example.data.model.TargetApp

class InstagramDetectionAdapter : DetectionProvider {
    override val targetApp: TargetApp = TargetApp.INSTAGRAM

    private var lastObservedNodeHash: String? = null
    private var lastCandidateTime: Long = 0L

    override fun isAvailable(): Boolean = true

    override fun getDetectionStatus(): DetectionStatus = DetectionStatus.ACTIVE_MONITORING

    override fun getConfidenceLevel(): DetectionConfidence = DetectionConfidence.HIGH_PROBABILITY

    override fun processAccessibilityEvent(
        event: AccessibilityEvent,
        rootNode: AccessibilityNodeInfo?
    ): DetectionCandidate? {
        val now = System.currentTimeMillis()
        val className = event.className?.toString() ?: ""

        // Check if event belongs to Instagram Reels viewport or pager
        val isReelsContext = className.contains("Reel", ignoreCase = true) ||
                className.contains("Clips", ignoreCase = true) ||
                className.contains("ViewPager", ignoreCase = true) ||
                event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED

        if (!isReelsContext) return null

        // Node inspection for video surface or audio track info
        var signature: String? = null
        if (rootNode != null) {
            val audioNodes = rootNode.findAccessibilityNodeInfosByViewId("com.instagram.android:id/row_feed_music_title")
            val clipsNodes = rootNode.findAccessibilityNodeInfosByViewId("com.instagram.android:id/clips_video_container")
            if (audioNodes.isNotEmpty()) {
                signature = "ig_audio_" + audioNodes[0].text?.toString()?.hashCode()
            } else if (clipsNodes.isNotEmpty()) {
                signature = "ig_clip_" + clipsNodes[0].hashCode()
            }
        }

        // Fallback hash based on event timestamp window if specific node ID wasn't extracted
        if (signature == null) {
            signature = "ig_scroll_${event.eventTime / 2000}"
        }

        if (signature == lastObservedNodeHash && (now - lastCandidateTime) < 3000) {
            return null // Suppress duplicate scroll pulse for same video
        }

        lastObservedNodeHash = signature
        lastCandidateTime = now

        return DetectionCandidate(
            app = targetApp,
            eventType = EventType.SWIPE_DWELL,
            confidence = DetectionConfidence.HIGH_PROBABILITY,
            contentHash = signature,
            timestamp = now,
            note = "Instagram Reels container transition"
        )
    }
}

class YouTubeShortsDetectionAdapter : DetectionProvider {
    override val targetApp: TargetApp = TargetApp.YOUTUBE

    private var lastShortId: String? = null
    private var lastTimestamp: Long = 0L

    override fun isAvailable(): Boolean = true

    override fun getDetectionStatus(): DetectionStatus = DetectionStatus.ACTIVE_MONITORING

    override fun getConfidenceLevel(): DetectionConfidence = DetectionConfidence.CONFIRMED

    override fun processAccessibilityEvent(
        event: AccessibilityEvent,
        rootNode: AccessibilityNodeInfo?
    ): DetectionCandidate? {
        val now = System.currentTimeMillis()
        val className = event.className?.toString() ?: ""

        val isShorts = className.contains("Shorts", ignoreCase = true) ||
                className.contains("ReelWatch", ignoreCase = true) ||
                event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED

        if (!isShorts) return null

        var signature: String? = null
        if (rootNode != null) {
            val titleNodes = rootNode.findAccessibilityNodeInfosByViewId("com.google.android.youtube:id/reel_player_title")
            val channelNodes = rootNode.findAccessibilityNodeInfosByViewId("com.google.android.youtube:id/channel_name")
            if (titleNodes.isNotEmpty()) {
                val title = titleNodes[0].text?.toString() ?: ""
                val channel = if (channelNodes.isNotEmpty()) channelNodes[0].text?.toString() ?: "" else ""
                signature = "yt_${(title + channel).hashCode()}"
            }
        }

        if (signature == null) {
            signature = "yt_snap_${event.eventTime / 2500}"
        }

        if (signature == lastShortId && (now - lastTimestamp) < 3000) {
            return null
        }

        lastShortId = signature
        lastTimestamp = now

        return DetectionCandidate(
            app = targetApp,
            eventType = EventType.VIEWPORT_TRANSITION,
            confidence = DetectionConfidence.CONFIRMED,
            contentHash = signature,
            timestamp = now,
            note = "YouTube Shorts viewport snap"
        )
    }
}

class TikTokDetectionAdapter : DetectionProvider {
    override val targetApp: TargetApp = TargetApp.TIKTOK

    private var lastScrollTime: Long = 0L
    private var lastGeneratedHash: String? = null

    override fun isAvailable(): Boolean = true

    override fun getDetectionStatus(): DetectionStatus = DetectionStatus.ACTIVE_MONITORING

    // TikTok heavily obfuscates view identifiers; therefore uses heuristic dwell detection
    override fun getConfidenceLevel(): DetectionConfidence = DetectionConfidence.HEURISTIC_ESTIMATE

    override fun processAccessibilityEvent(
        event: AccessibilityEvent,
        rootNode: AccessibilityNodeInfo?
    ): DetectionCandidate? {
        val now = System.currentTimeMillis()

        // Debounce: must have at least 2.5s between video transitions
        if (now - lastScrollTime < 2500) {
            return null
        }

        val isScrollOrPage = event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED ||
                event.className?.toString()?.contains("ViewPager", ignoreCase = true) == true

        if (!isScrollOrPage) return null

        val currentHash = "tt_feed_${now / 3000}"
        if (currentHash == lastGeneratedHash) return null

        lastScrollTime = now
        lastGeneratedHash = currentHash

        return DetectionCandidate(
            app = targetApp,
            eventType = EventType.SWIPE_DWELL,
            confidence = DetectionConfidence.HEURISTIC_ESTIMATE,
            contentHash = currentHash,
            timestamp = now,
            note = "TikTok VerticalViewPager heuristic dwell"
        )
    }
}

class FacebookReelsDetectionAdapter : DetectionProvider {
    override val targetApp: TargetApp = TargetApp.FACEBOOK

    private var lastSignature: String? = null
    private var lastCandidateTime: Long = 0L

    override fun isAvailable(): Boolean = true

    override fun getDetectionStatus(): DetectionStatus = DetectionStatus.ACTIVE_MONITORING

    override fun getConfidenceLevel(): DetectionConfidence = DetectionConfidence.HIGH_PROBABILITY

    override fun processAccessibilityEvent(
        event: AccessibilityEvent,
        rootNode: AccessibilityNodeInfo?
    ): DetectionCandidate? {
        val now = System.currentTimeMillis()
        val className = event.className?.toString() ?: ""

        val isFbReels = className.contains("Reel", ignoreCase = true) ||
                className.contains("FbReels", ignoreCase = true) ||
                event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED

        if (!isFbReels) return null

        val sig = "fb_reel_${now / 3000}"
        if (sig == lastSignature && (now - lastCandidateTime) < 3000) {
            return null
        }

        lastSignature = sig
        lastCandidateTime = now

        return DetectionCandidate(
            app = targetApp,
            eventType = EventType.UI_NODE_CHANGE,
            confidence = DetectionConfidence.HIGH_PROBABILITY,
            contentHash = sig,
            timestamp = now,
            note = "Facebook Reels viewer node transition"
        )
    }
}
