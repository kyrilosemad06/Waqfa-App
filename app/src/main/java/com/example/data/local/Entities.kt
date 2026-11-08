package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.DailyStats
import com.example.data.model.DetectionConfidence
import com.example.data.model.DetectionEvent
import com.example.data.model.EventType
import com.example.data.model.TargetApp

@Entity(tableName = "daily_stats")
data class DailyStatsEntity(
    @PrimaryKey
    val dateString: String,
    val confirmedCount: Int = 0,
    val estimatedCount: Int = 0,
    val instagramCount: Int = 0,
    val youtubeCount: Int = 0,
    val tiktokCount: Int = 0,
    val facebookCount: Int = 0,
    val limitReachedAt: Long? = null,
    val warningShown: Boolean = false,
    val exceptionsUsedToday: Int = 0
) {
    fun toDailyStats(): DailyStats {
        val map = mutableMapOf<TargetApp, Int>()
        if (instagramCount > 0) map[TargetApp.INSTAGRAM] = instagramCount
        if (youtubeCount > 0) map[TargetApp.YOUTUBE] = youtubeCount
        if (tiktokCount > 0) map[TargetApp.TIKTOK] = tiktokCount
        if (facebookCount > 0) map[TargetApp.FACEBOOK] = facebookCount

        return DailyStats(
            dateString = dateString,
            confirmedCount = confirmedCount,
            estimatedCount = estimatedCount,
            countByApp = map,
            limitReachedAt = limitReachedAt,
            warningShown = warningShown,
            exceptionsUsedToday = exceptionsUsedToday
        )
    }

    companion object {
        fun fromDailyStats(stats: DailyStats): DailyStatsEntity {
            return DailyStatsEntity(
                dateString = stats.dateString,
                confirmedCount = stats.confirmedCount,
                estimatedCount = stats.estimatedCount,
                instagramCount = stats.countByApp[TargetApp.INSTAGRAM] ?: 0,
                youtubeCount = stats.countByApp[TargetApp.YOUTUBE] ?: 0,
                tiktokCount = stats.countByApp[TargetApp.TIKTOK] ?: 0,
                facebookCount = stats.countByApp[TargetApp.FACEBOOK] ?: 0,
                limitReachedAt = stats.limitReachedAt,
                warningShown = stats.warningShown,
                exceptionsUsedToday = stats.exceptionsUsedToday
            )
        }
    }
}

@Entity(tableName = "detection_events")
data class DetectionEventEntity(
    @PrimaryKey
    val eventId: String,
    val appId: String,
    val timestamp: Long,
    val confidence: String,
    val eventType: String,
    val contentHash: String?,
    val dwellTimeMs: Long,
    val isConfirmed: Boolean,
    val note: String
) {
    fun toDetectionEvent(): DetectionEvent {
        val app = TargetApp.entries.firstOrNull { it.id == appId } ?: TargetApp.INSTAGRAM
        val conf = runCatching { DetectionConfidence.valueOf(confidence) }.getOrDefault(DetectionConfidence.HIGH_PROBABILITY)
        val type = runCatching { EventType.valueOf(eventType) }.getOrDefault(EventType.SWIPE_DWELL)
        return DetectionEvent(
            eventId = eventId,
            app = app,
            timestamp = timestamp,
            confidence = conf,
            eventType = type,
            contentHash = contentHash,
            dwellTimeMs = dwellTimeMs,
            isConfirmed = isConfirmed,
            note = note
        )
    }

    companion object {
        fun fromDetectionEvent(event: DetectionEvent): DetectionEventEntity {
            return DetectionEventEntity(
                eventId = event.eventId,
                appId = event.app.id,
                timestamp = event.timestamp,
                confidence = event.confidence.name,
                eventType = event.eventType.name,
                contentHash = event.contentHash,
                dwellTimeMs = event.dwellTimeMs,
                isConfirmed = event.isConfirmed,
                note = event.note
            )
        }
    }
}
