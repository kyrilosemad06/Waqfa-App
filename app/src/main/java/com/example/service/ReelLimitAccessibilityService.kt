package com.example.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.example.ReelLimitApplication
import com.example.data.model.DetectionStatus
import com.example.data.model.TargetApp
import com.example.engine.detection.FacebookReelsDetectionAdapter
import com.example.engine.detection.InstagramDetectionAdapter
import com.example.engine.detection.TikTokDetectionAdapter
import com.example.engine.detection.YouTubeShortsDetectionAdapter
import android.provider.Settings
import android.os.Build

class ReelLimitAccessibilityService : AccessibilityService() {

    private val igAdapter = InstagramDetectionAdapter()
    private val ytAdapter = YouTubeShortsDetectionAdapter()
    private val ttAdapter = TikTokDetectionAdapter()
    private val fbAdapter = FacebookReelsDetectionAdapter()

    override fun onServiceConnected() {
        super.onServiceConnected()
        val app = application as? ReelLimitApplication ?: return
        app.counterEngine.setDetectionStatus(DetectionStatus.ACTIVE_MONITORING)
        // Start the visible WAQFA counter as soon as the user has granted both permissions.
        if (app.settingsRepository.settingsFlow.value.floatingCounterEnabled &&
            (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this))) {
            FloatingCounterService.start(this)
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val packageName = event.packageName?.toString() ?: return

        // Never leave the barrier above WAQFA itself; this lets the user change their plan.
        if (packageName == applicationContext.packageName) {
            WaqfaBlockerService.stop(this)
            return
        }

        val targetApp = TargetApp.fromPackage(packageName) ?: return
        val app = application as? ReelLimitApplication ?: return
        val settings = app.settingsRepository.settingsFlow.value

        if (!settings.enabledApps.contains(targetApp)) return

        // The strict barrier is enforced immediately when a tracked social app becomes foreground.
        // This prevents a user from reopening the feed after their daily quota is already exhausted.
        if (settings.strictModeEnabled && app.counterEngine.currentStats.value.totalCount >= settings.dailyLimit) {
            WaqfaBlockerService.start(this)
            return
        }

        val rootNode = try {
            rootInActiveWindow
        } catch (_: Exception) {
            null
        }

        val candidate = when (targetApp) {
            TargetApp.INSTAGRAM -> igAdapter.processAccessibilityEvent(event, rootNode)
            TargetApp.YOUTUBE -> ytAdapter.processAccessibilityEvent(event, rootNode)
            TargetApp.TIKTOK -> ttAdapter.processAccessibilityEvent(event, rootNode)
            TargetApp.FACEBOOK -> fbAdapter.processAccessibilityEvent(event, rootNode)
        }

        if (candidate != null) {
            val counted = app.counterEngine.processCandidate(candidate, settings)
            if (counted) {
                // If strict mode is enabled and quota reached, bring up intervention
                val stats = app.counterEngine.currentStats.value
                if (settings.strictModeEnabled && stats.totalCount >= settings.dailyLimit) {
                    WaqfaBlockerService.start(this)
                }
            }
        }
    }

    override fun onInterrupt() {
        val app = application as? ReelLimitApplication ?: return
        app.counterEngine.setDetectionStatus(DetectionStatus.PAUSED)
    }

    override fun onDestroy() {
        super.onDestroy()
        val app = application as? ReelLimitApplication ?: return
        app.counterEngine.setDetectionStatus(DetectionStatus.IDLE)
    }
}
