package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ReelLimitApplication
import com.example.data.local.DailyStatsEntity
import com.example.data.model.AuditLogEntry
import com.example.data.model.DailyStats
import com.example.data.model.DetectionStatus
import com.example.data.model.TargetApp
import com.example.data.model.UserSettings
import com.example.engine.detection.DemoDetectionProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

enum class NavTab {
    DASHBOARD,
    FLOATING_PILL,
    STATISTICS,
    APP_ENGINES,
    SETTINGS,
    LICENSE,
    TECH_PLAN
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val appInstance = application as ReelLimitApplication
    private val counterEngine = appInstance.counterEngine
    private val settingsRepo = appInstance.settingsRepository
    private val demoProvider = DemoDetectionProvider()

    val currentStats: StateFlow<DailyStats> = counterEngine.currentStats
    val settings: StateFlow<UserSettings> = settingsRepo.settingsFlow
    val detectionStatus: StateFlow<DetectionStatus> = counterEngine.detectionStatus
    val auditLogs: StateFlow<List<AuditLogEntry>> = counterEngine.auditLogs

    private val _lastSimulatedHash = MutableStateFlow<String?>(null)

    private val _isStrictModeActive = MutableStateFlow(false)
    val isStrictModeActive: StateFlow<Boolean> = _isStrictModeActive.asStateFlow()

    private val _currentNavTab = MutableStateFlow(NavTab.DASHBOARD)
    val currentNavTab: StateFlow<NavTab> = _currentNavTab.asStateFlow()

    private val _recent7Days = MutableStateFlow<List<DailyStats>>(emptyList())
    val recent7Days: StateFlow<List<DailyStats>> = _recent7Days.asStateFlow()

    // 10-second deliberate friction timer for emergency exception
    private val _frictionCountdown = MutableStateFlow<Int?>(null)
    val frictionCountdown: StateFlow<Int?> = _frictionCountdown.asStateFlow()
    private var frictionJob: Job? = null

    init {
        // Pre-populate with realistic 7-day historical sample if DB is completely empty
        viewModelScope.launch { load7DaysStats() }

        viewModelScope.launch {
            counterEngine.onLimitReachedEvent.collectLatest {
                if (settings.value.strictModeEnabled) {
                    _isStrictModeActive.value = true
                }
            }
        }
    }

    fun startFocusPlan(baseline: Int, target: Int, strictness: Int, apps: Set<TargetApp>) {
        updateSettings(settings.value.copy(
            habitBaseline = baseline,
            dailyLimit = target,
            strictnessLevel = strictness,
            strictModeEnabled = strictness >= 2,
            exceptionsEnabled = strictness < 3,
            maxExceptionsPerDay = if (strictness == 0) 3 else if (strictness == 1) 1 else 0,
            enabledApps = apps,
            planStartedAtEpochMs = System.currentTimeMillis(),
            onboardingCompleted = true
        ))
    }

    fun load7DaysStats() {
        viewModelScope.launch {
            val entities = appInstance.database.dailyStatsDao().getLast7DaysStats()
            _recent7Days.value = entities.map { it.toDailyStats() }
        }
    }

    fun selectNavTab(tab: NavTab) {
        _currentNavTab.value = tab
    }

    fun updateSettings(newSettings: UserSettings) {
        settingsRepo.updateSettings(newSettings)
    }

    fun updateDailyLimit(newLimit: Int) {
        val safe = newLimit.coerceIn(1, 200)
        updateSettings(settings.value.copy(dailyLimit = safe))
    }

    fun toggleLanguage() {
        val nextLang = if (settings.value.language == "ar") "en" else "ar"
        updateSettings(settings.value.copy(language = nextLang))
    }

    fun toggleApp(app: TargetApp) {
        val currentSet = settings.value.enabledApps.toMutableSet()
        if (currentSet.contains(app)) {
            currentSet.remove(app)
        } else {
            currentSet.add(app)
        }
        updateSettings(settings.value.copy(enabledApps = currentSet))
    }

    fun completeOnboarding(selectedApps: Set<TargetApp>, limit: Int, resetH: Int, resetM: Int) {
        updateSettings(
            settings.value.copy(
                enabledApps = selectedApps,
                dailyLimit = limit,
                resetHour = resetH,
                resetMinute = resetM,
                onboardingCompleted = true
            )
        )
    }

    fun simulateDetection(app: TargetApp) {
        val candidate = demoProvider.createSimulatedCandidate(app)
        _lastSimulatedHash.value = candidate.contentHash
        counterEngine.processCandidate(candidate, settings.value)
        load7DaysStats()
    }

    fun simulateRapidSkip(app: TargetApp) {
        val candidate = demoProvider.createRapidSkipCandidate(app)
        counterEngine.processCandidate(candidate, settings.value)
    }

    fun simulateDuplicateReplay(app: TargetApp) {
        val hash = _lastSimulatedHash.value
        val candidate = demoProvider.createSimulatedCandidate(app, forceDuplicateHash = hash)
        counterEngine.processCandidate(candidate, settings.value)
    }

    fun applyManualCorrection(delta: Int, app: TargetApp) {
        counterEngine.applyManualCorrection(delta, app, settings.value)
        load7DaysStats()
    }

    fun openStrictMode() {
        _isStrictModeActive.value = true
    }

    fun dismissStrictMode() {
        _isStrictModeActive.value = false
        cancelFrictionCountdown()
    }

    fun startDeliberateExceptionFriction() {
        frictionJob?.cancel()
        _frictionCountdown.value = 10
        frictionJob = viewModelScope.launch {
            for (i in 9 downTo 0) {
                delay(1000)
                _frictionCountdown.value = i
            }
            // Once timer hits 0, grant the deliberate exception
            counterEngine.grantDeliberateException(settings.value)
            _frictionCountdown.value = null
            _isStrictModeActive.value = false
            load7DaysStats()
        }
    }

    fun cancelFrictionCountdown() {
        frictionJob?.cancel()
        _frictionCountdown.value = null
    }

    fun resetTodayCount() {
        counterEngine.resetTodayCount(settings.value)
        load7DaysStats()
    }

    fun clearAllData() {
        viewModelScope.launch {
            counterEngine.clearAllHistory()
            load7DaysStats()
        }
    }
}
