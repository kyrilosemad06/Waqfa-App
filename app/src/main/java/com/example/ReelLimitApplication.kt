package com.example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.data.local.AppDatabase
import com.example.data.repository.SettingsRepository
import com.example.engine.CounterEngine
import com.example.licensing.LicenseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class ReelLimitApplication : Application() {
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val database by lazy { AppDatabase.getInstance(this) }
    val settingsRepository by lazy { SettingsRepository(this) }
    val licenseRepository by lazy { LicenseRepository(this) }
    val counterEngine by lazy {
        CounterEngine(
            dailyStatsDao = database.dailyStatsDao(),
            detectionEventDao = database.detectionEventDao(),
            coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        )
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val overlayChannel = NotificationChannel(
                CHANNEL_OVERLAY,
                "Floating Counter Overlay",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Foreground service notification keeping the floating counter alive while browsing."
                setShowBadge(false)
            }

            val alertChannel = NotificationChannel(
                CHANNEL_ALERTS,
                "Limit & Warning Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts when approaching or reaching daily short-form video limit."
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(overlayChannel)
            notificationManager.createNotificationChannel(alertChannel)
        }
    }

    companion object {
        lateinit var instance: ReelLimitApplication
            private set

        const val CHANNEL_OVERLAY = "reel_limit_overlay_channel"
        const val CHANNEL_ALERTS = "reel_limit_alerts_channel"
    }
}
