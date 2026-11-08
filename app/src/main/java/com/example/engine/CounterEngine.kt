package com.example.engine

import com.example.data.local.DailyStatsDao
import com.example.data.local.DailyStatsEntity
import com.example.data.local.DetectionEventDao
import com.example.data.local.DetectionEventEntity
import com.example.data.model.AuditLogEntry
import com.example.data.model.DailyStats
import com.example.data.model.DetectionConfidence
import com.example.data.model.DetectionEvent
import com.example.data.model.DetectionStatus
import com.example.data.model.EventType
import com.example.data.model.TargetApp
import com.example.data.model.UserSettings
import com.example.engine.detection.DetectionCandidate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class CounterEngine(
    private val dailyStatsDao: DailyStatsDao,
    private val detectionEventDao: DetectionEventDao,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val _currentStats = MutableStateFlow(DailyStats(getCurrentTrackingDate(0, 0)))
    val currentStats: StateFlow<DailyStats> = _currentStats.asStateFlow()

    private val _detectionStatus = MutableStateFlow(DetectionStatus.ACTIVE_MONITORING)
    val detectionStatus: StateFlow<DetectionStatus> = _detectionStatus.asStateFlow()

    private val _auditLogs = MutableStateFlow<List<AuditLogEntry>>(emptyList())
    val auditLogs: StateFlow<List<AuditLogEntry>> = _auditLogs.asStateFlow()

    private val _onLimitReachedEvent = MutableSharedFlow<Unit>()
    val onLimitReachedEvent: SharedFlow<Unit> = _onLimitReachedEvent.asSharedFlow()

    private val _onWarningEvent = MutableSharedFlow<Unit>()
    val onWarningEvent: SharedFlow<Unit> = _onWarningEvent.asSharedFlow()

    // Deduplication tracking: recently seen hashes with timestamps
    private val recentHashes = mutableMapOf<String, Long>()
    private var lastEventTimestamp: Long = 0L
    private var lastAppLogged: TargetApp? = null

    // Hash expiration window: 30 minutes
    private val HASH_EXPIRATION_MS = 30 * 60 * 1000L
    // Debounce threshold for rapid successive events
    private val RAPID_DEBOUNCE_MS = 2500L

    init {
        coroutineScope.launch {
            loadStatsForToday(0, 0)
        }
    }

    fun getCurrentTrackingDate(resetHour: Int, resetMinute: Int): String {
        val cal = Calendar.getInstance()
        // If current time is before the daily reset time, count belongs to previous day
        val currentMinutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
        val resetMinutes = resetHour * 60 + resetMinute

        if (currentMinutes < resetMinutes) {
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(cal.time)
    }

    suspend fun loadStatsForToday(resetHour: Int, resetMinute: Int) {
        val todayStr = getCurrentTrackingDate(resetHour, resetMinute)
        val existing = dailyStatsDao.getStatsForDate(todayStr)
        if (existing != null) {
            _currentStats.value = existing.toDailyStats()
        } else {
            val fresh = DailyStats(dateString = todayStr)
            _currentStats.value = fresh
            dailyStatsDao.insertOrUpdate(DailyStatsEntity.fromDailyStats(fresh))
        }
    }

    /**
     * Ingest a video detection candidate.
     * Evaluates deduplication, dwell threshold, target app enablement, and updates daily quota.
     * Returns true if event was successfully counted as a new unique video.
     */
    fun processCandidate(
        candidate: DetectionCandidate,
        settings: UserSettings
    ): Boolean {
        val now = System.currentTimeMillis()

        // 1. Check if target app is enabled by user
        if (!settings.enabledApps.contains(candidate.app)) {
            addAuditLog(
                "Ignored ${candidate.app.title} video: app is disabled in settings.",
                "تم تجاهل فيديو ${candidate.app.title}: التطبيق معطل في الإعدادات.",
                isDeduplicated = true,
                app = candidate.app
            )
            return false
        }

        // 2. Debounce rapid events from same app
        if (candidate.app == lastAppLogged && (now - lastEventTimestamp) < RAPID_DEBOUNCE_MS) {
            addAuditLog(
                "Deduplicated rapid scroll event in ${candidate.app.title} (<2.5s debounce).",
                "تم منع احتساب تكرار سريع في ${candidate.app.title} (أقل من 2.5 ثانية).",
                isDeduplicated = true,
                app = candidate.app
            )
            return false
        }

        // 3. Content Hash Deduplication
        cleanupOldHashes(now)
        val hash = candidate.contentHash
        if (hash != null) {
            val lastSeen = recentHashes[hash]
            if (lastSeen != null && (now - lastSeen) < HASH_EXPIRATION_MS) {
                addAuditLog(
                    "Deduplicated replay of same video hash in ${candidate.app.title}.",
                    "تم منع تكرار نفس معرف الفيديو في ${candidate.app.title}.",
                    isDeduplicated = true,
                    app = candidate.app
                )
                return false
            }
            recentHashes[hash] = now
        }

        // 4. Update tracking timestamps
        lastEventTimestamp = now
        lastAppLogged = candidate.app

        // 5. Build confirmed/estimated event
        val isConfirmed = candidate.confidence == DetectionConfidence.CONFIRMED ||
                candidate.confidence == DetectionConfidence.HIGH_PROBABILITY

        val event = DetectionEvent(
            eventId = UUID.randomUUID().toString(),
            app = candidate.app,
            timestamp = now,
            confidence = candidate.confidence,
            eventType = candidate.eventType,
            contentHash = candidate.contentHash,
            dwellTimeMs = candidate.dwellTimeMs,
            isConfirmed = isConfirmed,
            note = candidate.note
        )

        // 6. Record event and increment counter
        coroutineScope.launch {
            recordEligibleVideo(event, settings)
        }
        return true
    }

    private suspend fun recordEligibleVideo(event: DetectionEvent, settings: UserSettings) {
        val todayStr = getCurrentTrackingDate(settings.resetHour, settings.resetMinute)
        var current = _currentStats.value
        if (current.dateString != todayStr) {
            current = DailyStats(dateString = todayStr)
        }

        val updatedMap = current.countByApp.toMutableMap()
        updatedMap[event.app] = (updatedMap[event.app] ?: 0) + 1

        val newConfirmed = if (event.isConfirmed) current.confirmedCount + 1 else current.confirmedCount
        val newEstimated = if (!event.isConfirmed) current.estimatedCount + 1 else current.estimatedCount
        val newTotal = newConfirmed + newEstimated

        val warningThresholdCount = (settings.dailyLimit * settings.warningThresholdPercent) / 100
        val isWarningTriggered = newTotal >= warningThresholdCount && !current.warningShown
        val isLimitTriggered = newTotal >= settings.dailyLimit && current.limitReachedAt == null

        val updated = current.copy(
            confirmedCount = newConfirmed,
            estimatedCount = newEstimated,
            countByApp = updatedMap,
            limitReachedAt = if (isLimitTriggered) System.currentTimeMillis() else current.limitReachedAt,
            warningShown = current.warningShown || isWarningTriggered
        )

        _currentStats.value = updated

        // Persist to Room
        dailyStatsDao.insertOrUpdate(DailyStatsEntity.fromDailyStats(updated))
        detectionEventDao.insertEvent(DetectionEventEntity.fromDetectionEvent(event))

        addAuditLog(
            "Counted 1 video from ${event.app.title} (${event.confidence.name}). Total: $newTotal / ${settings.dailyLimit}",
            "تم احتساب فيديو من ${event.app.title}. المجموع: $newTotal / ${settings.dailyLimit}",
            isDeduplicated = false,
            app = event.app
        )

        if (isWarningTriggered) {
            _onWarningEvent.emit(Unit)
        }
        if (isLimitTriggered) {
            _onLimitReachedEvent.emit(Unit)
        }
    }

    fun applyManualCorrection(delta: Int, app: TargetApp, settings: UserSettings) {
        coroutineScope.launch {
            val todayStr = getCurrentTrackingDate(settings.resetHour, settings.resetMinute)
            var current = _currentStats.value
            if (current.dateString != todayStr) {
                current = DailyStats(dateString = todayStr)
            }

            val appCount = current.countByApp[app] ?: 0
            val newAppCount = (appCount + delta).coerceAtLeast(0)
            val updatedMap = current.countByApp.toMutableMap()
            updatedMap[app] = newAppCount

            val newConfirmed = if (delta > 0) current.confirmedCount + delta else (current.confirmedCount + delta).coerceAtLeast(0)
            val updated = current.copy(
                confirmedCount = newConfirmed,
                countByApp = updatedMap
            )
            _currentStats.value = updated
            dailyStatsDao.insertOrUpdate(DailyStatsEntity.fromDailyStats(updated))

            val msgEn = if (delta > 0) "Manually logged +$delta video for ${app.title}." else "Manually removed $delta video for ${app.title}."
            val msgAr = if (delta > 0) "تمت إضافة $delta فيديو يدوياً لـ ${app.title}." else "تم تصحيح وخصم $delta فيديو لـ ${app.title}."
            addAuditLog(msgEn, msgAr, isDeduplicated = false, app = app)
        }
    }

    fun grantDeliberateException(settings: UserSettings): Boolean {
        val current = _currentStats.value
        if (current.exceptionsUsedToday >= settings.maxExceptionsPerDay) {
            return false
        }
        coroutineScope.launch {
            // Grants 3 additional videos by either extending capacity or resetting limit flag
            val updated = current.copy(
                exceptionsUsedToday = current.exceptionsUsedToday + 1,
                limitReachedAt = null // Clear strict mode lockdown
            )
            _currentStats.value = updated
            dailyStatsDao.insertOrUpdate(DailyStatsEntity.fromDailyStats(updated))

            addAuditLog(
                "Deliberate emergency exception used (+3 videos buffer granted).",
                "تم تفعيل الاستثناء الواعي الطارئ (منح رصيد إضافي +3 فيديوهات).",
                isDeduplicated = false
            )
        }
        return true
    }

    fun resetTodayCount(settings: UserSettings) {
        coroutineScope.launch {
            val todayStr = getCurrentTrackingDate(settings.resetHour, settings.resetMinute)
            val fresh = DailyStats(dateString = todayStr)
            _currentStats.value = fresh
            dailyStatsDao.insertOrUpdate(DailyStatsEntity.fromDailyStats(fresh))
            addAuditLog(
                "Today's counter manually reset to zero.",
                "تمت إعادة ضبط عداد اليوم يدوياً إلى الصفر.",
                isDeduplicated = false
            )
        }
    }

    suspend fun clearAllHistory() {
        dailyStatsDao.clearAll()
        detectionEventDao.clearAll()
        _currentStats.value = DailyStats(getCurrentTrackingDate(0, 0))
        _auditLogs.value = emptyList()
        recentHashes.clear()
    }

    fun setDetectionStatus(status: DetectionStatus) {
        _detectionStatus.value = status
    }

    private fun cleanupOldHashes(now: Long) {
        val iterator = recentHashes.entries.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (now - entry.value > HASH_EXPIRATION_MS) {
                iterator.remove()
            }
        }
    }

    private fun addAuditLog(msgEn: String, msgAr: String, isDeduplicated: Boolean, app: TargetApp? = null) {
        val entry = AuditLogEntry(
            id = UUID.randomUUID().toString(),
            timestamp = System.currentTimeMillis(),
            messageEn = msgEn,
            messageAr = msgAr,
            isDeduplicated = isDeduplicated,
            app = app
        )
        _auditLogs.value = (listOf(entry) + _auditLogs.value).take(50)
    }
}
