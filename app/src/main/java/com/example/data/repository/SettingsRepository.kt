package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.TargetApp
import com.example.data.model.UserSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("reel_limit_settings", Context.MODE_PRIVATE)

    private val _settingsFlow = MutableStateFlow(loadSettings())
    val settingsFlow: StateFlow<UserSettings> = _settingsFlow.asStateFlow()

    private fun loadSettings(): UserSettings {
        val userName = prefs.getString(KEY_USER_NAME, "") ?: ""
        val dailyLimit = prefs.getInt(KEY_DAILY_LIMIT, 20)
        val warningThreshold = prefs.getInt(KEY_WARNING_THRESHOLD, 75)
        val resetHour = prefs.getInt(KEY_RESET_HOUR, 0)
        val resetMinute = prefs.getInt(KEY_RESET_MINUTE, 0)
        val strictMode = prefs.getBoolean(KEY_STRICT_MODE, true)
        val exceptionsEnabled = prefs.getBoolean(KEY_EXCEPTIONS_ENABLED, true)
        val maxExceptions = prefs.getInt(KEY_MAX_EXCEPTIONS, 1)
        val language = prefs.getString(KEY_LANGUAGE, "ar") ?: "ar"
        val floatingCounter = prefs.getBoolean(KEY_FLOATING_COUNTER, true)
        val floatingMinimized = prefs.getBoolean(KEY_FLOATING_MINIMIZED, false)
        val demoMode = prefs.getBoolean(KEY_DEMO_MODE, false)
        val onboarding = prefs.getBoolean(KEY_ONBOARDING_DONE, false)
        val minDwell = prefs.getFloat(KEY_MIN_DWELL, 2.0f)
        val baseline = prefs.getInt(KEY_HABIT_BASELINE, 40)
        val strictness = prefs.getInt(KEY_STRICTNESS, 2)
        val planStartedAt = prefs.getLong(KEY_PLAN_STARTED_AT, 0L)

        val enabledAppsSaved = prefs.getStringSet(KEY_ENABLED_APPS, null)
        val enabledApps = if (enabledAppsSaved != null) {
            enabledAppsSaved.mapNotNull { id -> TargetApp.entries.firstOrNull { it.id == id } }.toSet()
        } else {
            TargetApp.entries.toSet()
        }

        return UserSettings(
            userName = userName,
            dailyLimit = dailyLimit,
            warningThresholdPercent = warningThreshold,
            resetHour = resetHour,
            resetMinute = resetMinute,
            strictModeEnabled = strictMode,
            exceptionsEnabled = exceptionsEnabled,
            maxExceptionsPerDay = maxExceptions,
            enabledApps = enabledApps,
            language = language,
            floatingCounterEnabled = floatingCounter,
            floatingCounterMinimized = floatingMinimized,
            demoModeActive = demoMode,
            onboardingCompleted = onboarding,
            minDwellSeconds = minDwell,
            habitBaseline = baseline,
            strictnessLevel = strictness,
            planStartedAtEpochMs = planStartedAt
        )
    }

    fun updateSettings(newSettings: UserSettings) {
        prefs.edit().apply {
            putString(KEY_USER_NAME, newSettings.userName.trim())
            putInt(KEY_DAILY_LIMIT, newSettings.dailyLimit)
            putInt(KEY_WARNING_THRESHOLD, newSettings.warningThresholdPercent)
            putInt(KEY_RESET_HOUR, newSettings.resetHour)
            putInt(KEY_RESET_MINUTE, newSettings.resetMinute)
            putBoolean(KEY_STRICT_MODE, newSettings.strictModeEnabled)
            putBoolean(KEY_EXCEPTIONS_ENABLED, newSettings.exceptionsEnabled)
            putInt(KEY_MAX_EXCEPTIONS, newSettings.maxExceptionsPerDay)
            putString(KEY_LANGUAGE, newSettings.language)
            putBoolean(KEY_FLOATING_COUNTER, newSettings.floatingCounterEnabled)
            putBoolean(KEY_FLOATING_MINIMIZED, newSettings.floatingCounterMinimized)
            putBoolean(KEY_DEMO_MODE, newSettings.demoModeActive)
            putBoolean(KEY_ONBOARDING_DONE, newSettings.onboardingCompleted)
            putFloat(KEY_MIN_DWELL, newSettings.minDwellSeconds)
            putInt(KEY_HABIT_BASELINE, newSettings.habitBaseline)
            putInt(KEY_STRICTNESS, newSettings.strictnessLevel)
            putLong(KEY_PLAN_STARTED_AT, newSettings.planStartedAtEpochMs)
            putStringSet(KEY_ENABLED_APPS, newSettings.enabledApps.map { it.id }.toSet())
            apply()
        }
        _settingsFlow.value = newSettings
    }

    companion object {
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_DAILY_LIMIT = "daily_limit"
        private const val KEY_WARNING_THRESHOLD = "warning_threshold"
        private const val KEY_RESET_HOUR = "reset_hour"
        private const val KEY_RESET_MINUTE = "reset_minute"
        private const val KEY_STRICT_MODE = "strict_mode"
        private const val KEY_EXCEPTIONS_ENABLED = "exceptions_enabled"
        private const val KEY_MAX_EXCEPTIONS = "max_exceptions"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_FLOATING_COUNTER = "floating_counter"
        private const val KEY_FLOATING_MINIMIZED = "floating_minimized"
        private const val KEY_DEMO_MODE = "demo_mode"
        private const val KEY_ONBOARDING_DONE = "onboarding_done"
        private const val KEY_MIN_DWELL = "min_dwell"
        private const val KEY_ENABLED_APPS = "enabled_apps"
        private const val KEY_HABIT_BASELINE = "habit_baseline"
        private const val KEY_STRICTNESS = "strictness_level"
        private const val KEY_PLAN_STARTED_AT = "plan_started_at"
    }
}
