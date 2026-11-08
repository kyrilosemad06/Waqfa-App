package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyStatsDao {
    @Query("SELECT * FROM daily_stats WHERE dateString = :date LIMIT 1")
    suspend fun getStatsForDate(date: String): DailyStatsEntity?

    @Query("SELECT * FROM daily_stats WHERE dateString = :date LIMIT 1")
    fun observeStatsForDate(date: String): Flow<DailyStatsEntity?>

    @Query("SELECT * FROM daily_stats ORDER BY dateString DESC LIMIT 7")
    suspend fun getLast7DaysStats(): List<DailyStatsEntity>

    @Query("SELECT * FROM daily_stats ORDER BY dateString DESC LIMIT 30")
    suspend fun getLast30DaysStats(): List<DailyStatsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(stats: DailyStatsEntity)

    @Query("DELETE FROM daily_stats WHERE dateString = :date")
    suspend fun deleteStatsForDate(date: String)

    @Query("DELETE FROM daily_stats")
    suspend fun clearAll()
}

@Dao
interface DetectionEventDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEvent(event: DetectionEventEntity)

    @Query("SELECT * FROM detection_events ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentEvents(limit: Int): List<DetectionEventEntity>

    @Query("SELECT * FROM detection_events ORDER BY timestamp DESC LIMIT :limit")
    fun observeRecentEvents(limit: Int): Flow<List<DetectionEventEntity>>

    @Query("DELETE FROM detection_events")
    suspend fun clearAll()
}
