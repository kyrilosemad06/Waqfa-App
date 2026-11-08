package com.example

import com.example.data.local.DailyStatsDao
import com.example.data.local.DailyStatsEntity
import com.example.data.local.DetectionEventDao
import com.example.data.local.DetectionEventEntity
import com.example.data.model.DetectionConfidence
import com.example.data.model.EventType
import com.example.data.model.TargetApp
import com.example.data.model.UserSettings
import com.example.engine.CounterEngine
import com.example.engine.detection.DetectionCandidate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CounterEngineTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private val fakeDailyStatsDao = object : DailyStatsDao {
        val stored = mutableMapOf<String, DailyStatsEntity>()
        override suspend fun getStatsForDate(date: String): DailyStatsEntity? = stored[date]
        override fun observeStatsForDate(date: String): Flow<DailyStatsEntity?> = flowOf(stored[date])
        override suspend fun getLast7DaysStats(): List<DailyStatsEntity> = stored.values.toList()
        override suspend fun getLast30DaysStats(): List<DailyStatsEntity> = stored.values.toList()
        override suspend fun insertOrUpdate(stats: DailyStatsEntity) { stored[stats.dateString] = stats }
        override suspend fun deleteStatsForDate(date: String) { stored.remove(date) }
        override suspend fun clearAll() { stored.clear() }
    }

    private val fakeDetectionEventDao = object : DetectionEventDao {
        val events = mutableListOf<DetectionEventEntity>()
        override suspend fun insertEvent(event: DetectionEventEntity) { events.add(event) }
        override suspend fun getRecentEvents(limit: Int): List<DetectionEventEntity> = events.takeLast(limit)
        override fun observeRecentEvents(limit: Int): Flow<List<DetectionEventEntity>> = flowOf(events.takeLast(limit))
        override suspend fun clearAll() { events.clear() }
    }

    private lateinit var counterEngine: CounterEngine

    @Before
    fun setup() {
        counterEngine = CounterEngine(
            dailyStatsDao = fakeDailyStatsDao,
            detectionEventDao = fakeDetectionEventDao,
            coroutineScope = testScope
        )
    }

    @Test
    fun testEligibleVideoCounting() = testScope.runTest {
        val settings = UserSettings(dailyLimit = 20)
        val candidate = DetectionCandidate(
            app = TargetApp.INSTAGRAM,
            eventType = EventType.SWIPE_DWELL,
            confidence = DetectionConfidence.HIGH_PROBABILITY,
            contentHash = "test_hash_001",
            timestamp = System.currentTimeMillis()
        )

        val counted = counterEngine.processCandidate(candidate, settings)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue(counted)
        assertEquals(1, counterEngine.currentStats.value.totalCount)
        assertEquals(1, counterEngine.currentStats.value.countByApp[TargetApp.INSTAGRAM])
    }

    @Test
    fun testDeduplicationOfRapidSuccessiveEvents() = testScope.runTest {
        val settings = UserSettings(dailyLimit = 20)
        val now = System.currentTimeMillis()

        val candidate1 = DetectionCandidate(
            app = TargetApp.YOUTUBE,
            eventType = EventType.VIEWPORT_TRANSITION,
            confidence = DetectionConfidence.CONFIRMED,
            contentHash = "yt_hash_1",
            timestamp = now
        )
        // Candidate 2 immediately arrives (<2.5s from same app)
        val candidate2 = DetectionCandidate(
            app = TargetApp.YOUTUBE,
            eventType = EventType.VIEWPORT_TRANSITION,
            confidence = DetectionConfidence.CONFIRMED,
            contentHash = "yt_hash_2",
            timestamp = now + 500
        )

        val firstCounted = counterEngine.processCandidate(candidate1, settings)
        val secondCounted = counterEngine.processCandidate(candidate2, settings)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue("First candidate should be counted", firstCounted)
        assertFalse("Second rapid scroll bounce candidate should be deduplicated", secondCounted)
        assertEquals(1, counterEngine.currentStats.value.totalCount)
    }

    @Test
    fun testContentHashDeduplicationOnReplay() = testScope.runTest {
        val settings = UserSettings(dailyLimit = 20)
        val candidate1 = DetectionCandidate(
            app = TargetApp.TIKTOK,
            eventType = EventType.SWIPE_DWELL,
            confidence = DetectionConfidence.HEURISTIC_ESTIMATE,
            contentHash = "tt_same_video_hash",
            timestamp = System.currentTimeMillis()
        )
        counterEngine.processCandidate(candidate1, settings)
        testDispatcher.scheduler.advanceUntilIdle()

        // Same hash presented again
        val candidateReplay = DetectionCandidate(
            app = TargetApp.TIKTOK,
            eventType = EventType.SWIPE_DWELL,
            confidence = DetectionConfidence.HEURISTIC_ESTIMATE,
            contentHash = "tt_same_video_hash",
            timestamp = System.currentTimeMillis() + 4000
        )
        val replayCounted = counterEngine.processCandidate(candidateReplay, settings)
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse("Replay of identical content hash should be deduplicated", replayCounted)
        assertEquals(1, counterEngine.currentStats.value.totalCount)
    }

    @Test
    fun testDisabledAppIgnored() = testScope.runTest {
        // Facebook is removed from enabled apps
        val settings = UserSettings(
            enabledApps = setOf(TargetApp.INSTAGRAM, TargetApp.YOUTUBE)
        )

        val candidateFb = DetectionCandidate(
            app = TargetApp.FACEBOOK,
            eventType = EventType.UI_NODE_CHANGE,
            confidence = DetectionConfidence.HIGH_PROBABILITY,
            contentHash = "fb_hash_1",
            timestamp = System.currentTimeMillis()
        )

        val counted = counterEngine.processCandidate(candidateFb, settings)
        testDispatcher.scheduler.advanceUntilIdle()

        assertFalse("Disabled app candidate should not be counted", counted)
        assertEquals(0, counterEngine.currentStats.value.totalCount)
    }

    @Test
    fun testManualCorrectionDoesNotGoBelowZero() = testScope.runTest {
        val settings = UserSettings()
        counterEngine.applyManualCorrection(-5, TargetApp.INSTAGRAM, settings)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(0, counterEngine.currentStats.value.totalCount)
        assertEquals(0, counterEngine.currentStats.value.countByApp[TargetApp.INSTAGRAM] ?: 0)
    }

    @Test
    fun testDeliberateExceptionGrantsBuffer() = testScope.runTest {
        val settings = UserSettings(dailyLimit = 5, maxExceptionsPerDay = 1)
        val granted = counterEngine.grantDeliberateException(settings)
        testDispatcher.scheduler.advanceUntilIdle()

        assertTrue("First deliberate exception should be granted", granted)
        assertEquals(1, counterEngine.currentStats.value.exceptionsUsedToday)

        val secondGranted = counterEngine.grantDeliberateException(settings)
        assertFalse("Second exception within same day should be denied", secondGranted)
    }

    @Test
    fun testResetTodayCounter() = testScope.runTest {
        val settings = UserSettings()
        counterEngine.applyManualCorrection(10, TargetApp.INSTAGRAM, settings)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(10, counterEngine.currentStats.value.totalCount)

        counterEngine.resetTodayCount(settings)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(0, counterEngine.currentStats.value.totalCount)
    }
}
