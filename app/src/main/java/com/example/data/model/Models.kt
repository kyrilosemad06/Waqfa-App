package com.example.data.model

enum class TargetApp(
    val id: String,
    val title: String,
    val packageName: String,
    val shortFormatName: String,
    val defaultConfidence: DetectionConfidence,
    val detectionNotesEn: String,
    val detectionNotesAr: String
) {
    INSTAGRAM(
        id = "instagram",
        title = "Instagram",
        packageName = "com.instagram.android",
        shortFormatName = "Reels",
        defaultConfidence = DetectionConfidence.HIGH_PROBABILITY,
        detectionNotesEn = "Monitors ReelsViewerFragment, clips container transitions, and viewport stabilization.",
        detectionNotesAr = "يراقب شاشة ReelsViewer وتغيرات حاوية المقاطع واستقرار المشاهدة."
    ),
    YOUTUBE(
        id = "youtube",
        title = "YouTube",
        packageName = "com.google.android.youtube",
        shortFormatName = "Shorts",
        defaultConfidence = DetectionConfidence.CONFIRMED,
        detectionNotesEn = "Detects ReelWatchFragment, playback surface nodes, and page-scroll snap events.",
        detectionNotesAr = "يكتشف مشغل Shorts وتغيرات مساحة العرض وانتقالات المقاطع."
    ),
    TIKTOK(
        id = "tiktok",
        title = "TikTok",
        packageName = "com.zhiliaoapp.musically",
        shortFormatName = "Videos",
        defaultConfidence = DetectionConfidence.HEURISTIC_ESTIMATE,
        detectionNotesEn = "Uses VerticalViewPager scroll events and dwell time heuristics due to obfuscated node IDs.",
        detectionNotesAr = "يستخدم تمرير VerticalViewPager وحساب زمن البقاء بسبب تشفير واجهة التطبيق."
    ),
    FACEBOOK(
        id = "facebook",
        title = "Facebook",
        packageName = "com.facebook.katana",
        shortFormatName = "Reels",
        defaultConfidence = DetectionConfidence.HIGH_PROBABILITY,
        detectionNotesEn = "Monitors FbReelsViewerActivity and video player layout swaps.",
        detectionNotesAr = "يراقب نشاط مشغل ريلز فيسبوك وتغيرات الحاويات المرئية."
    );

    companion object {
        fun fromPackage(packageName: String): TargetApp? {
            return entries.firstOrNull { it.packageName == packageName }
        }
    }
}

enum class DetectionConfidence {
    CONFIRMED,
    HIGH_PROBABILITY,
    HEURISTIC_ESTIMATE
}

enum class EventType {
    SWIPE_DWELL,
    VIEWPORT_TRANSITION,
    UI_NODE_CHANGE,
    MANUAL_LOG,
    DEMO_SIMULATION
}

data class DetectionEvent(
    val eventId: String,
    val app: TargetApp,
    val timestamp: Long,
    val confidence: DetectionConfidence,
    val eventType: EventType,
    val contentHash: String? = null,
    val dwellTimeMs: Long = 0L,
    val isConfirmed: Boolean = true,
    val note: String = ""
)

data class DailyStats(
    val dateString: String, // yyyy-MM-dd
    val confirmedCount: Int = 0,
    val estimatedCount: Int = 0,
    val countByApp: Map<TargetApp, Int> = emptyMap(),
    val limitReachedAt: Long? = null,
    val warningShown: Boolean = false,
    val exceptionsUsedToday: Int = 0
) {
    val totalCount: Int
        get() = confirmedCount + estimatedCount
}

data class UserSettings(
    val userName: String = "",
    val dailyLimit: Int = 20,
    val warningThresholdPercent: Int = 75,
    val resetHour: Int = 0,
    val resetMinute: Int = 0,
    val strictModeEnabled: Boolean = true,
    val exceptionsEnabled: Boolean = true,
    val maxExceptionsPerDay: Int = 1,
    val enabledApps: Set<TargetApp> = TargetApp.entries.toSet(),
    val language: String = "ar", // "en" or "ar"
    val floatingCounterEnabled: Boolean = true,
    val floatingCounterMinimized: Boolean = false,
    val demoModeActive: Boolean = false,
    val onboardingCompleted: Boolean = false,
    val minDwellSeconds: Float = 2.0f,
    val habitBaseline: Int = 40,
    /** 0 flexible, 1 balanced, 2 strict, 3 strictest */
    val strictnessLevel: Int = 2,
    val planStartedAtEpochMs: Long = 0L
)

enum class DetectionStatus {
    ACTIVE_MONITORING,
    DEMO_ACTIVE,
    PERMISSIONS_NEEDED,
    PAUSED,
    IDLE
}

data class AuditLogEntry(
    val id: String,
    val timestamp: Long,
    val messageEn: String,
    val messageAr: String,
    val isDeduplicated: Boolean = false,
    val app: TargetApp? = null
)
