package com.example

import android.content.Intent
import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StringsRepo
import com.example.ui.MainViewModel
import com.example.ui.WaqfaApp
import com.example.ui.NavTab
import com.example.ui.screens.AppManagementScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FloatingPreviewScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.StatisticsScreen
import com.example.ui.screens.StrictModeScreen
import com.example.ui.screens.TechnicalArchitectureScreen
import com.example.ui.theme.BorderColor
import com.example.ui.theme.MainBackground
import com.example.ui.theme.PrimaryAccent
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.WaqfaTheme
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()
    private val notificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (intent?.getBooleanExtra("EXTRA_NAVIGATE_STRICT_MODE", false) == true) {
            viewModel.openStrictMode()
        }

        setContent { WaqfaApp(viewModel, (application as ReelLimitApplication).licenseRepository) }
        return

        setContent {
            val settings by viewModel.settings.collectAsState()
            val currentStats by viewModel.currentStats.collectAsState()
            val detectionStatus by viewModel.detectionStatus.collectAsState()
            val auditLogs by viewModel.auditLogs.collectAsState()
            val recent7Days by viewModel.recent7Days.collectAsState()
            val isStrictModeActive by viewModel.isStrictModeActive.collectAsState()
            val frictionCountdown by viewModel.frictionCountdown.collectAsState()
            val currentNavTab by viewModel.currentNavTab.collectAsState()

            val isAr = settings.language == "ar"
            val layoutDirection = if (isAr) LayoutDirection.Rtl else LayoutDirection.Ltr

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                WaqfaTheme {
                    if (!settings.onboardingCompleted) {
                        OnboardingScreen(
                            currentLang = settings.language,
                            onComplete = { apps, limit, resetH, resetM ->
                                viewModel.completeOnboarding(apps, limit, resetH, resetM)
                            }
                        )
                    } else if (isStrictModeActive) {
                        BackHandler {
                            viewModel.dismissStrictMode()
                        }
                        StrictModeScreen(
                            currentStats = currentStats,
                            settings = settings,
                            frictionCountdown = frictionCountdown,
                            onStartFriction = { viewModel.startDeliberateExceptionFriction() },
                            onCancelFriction = { viewModel.cancelFrictionCountdown() },
                            onReturnHome = { viewModel.dismissStrictMode() }
                        )
                    } else {
                        BackHandler(enabled = currentNavTab != NavTab.DASHBOARD) {
                            viewModel.selectNavTab(NavTab.DASHBOARD)
                        }

                        Scaffold(
                            containerColor = MainBackground,
                            bottomBar = {
                                NavigationBar(
                                    containerColor = SurfaceDark,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, BorderColor)
                                        .testTag("main_bottom_nav"),
                                    tonalElevation = 0.dp
                                ) {
                                    NavigationBarItem(
                                        selected = currentNavTab == NavTab.DASHBOARD,
                                        onClick = { viewModel.selectNavTab(NavTab.DASHBOARD) },
                                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                                        label = {
                                            Text(
                                                text = StringsRepo.get("nav_dashboard", settings.language),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        },
                                        colors = navigationItemColors()
                                    )

                                    NavigationBarItem(
                                        selected = currentNavTab == NavTab.FLOATING_PILL,
                                        onClick = { viewModel.selectNavTab(NavTab.FLOATING_PILL) },
                                        icon = { Icon(Icons.Default.Layers, contentDescription = "Floating Pill") },
                                        label = {
                                            Text(
                                                text = StringsRepo.get("nav_floating", settings.language),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        },
                                        colors = navigationItemColors()
                                    )

                                    NavigationBarItem(
                                        selected = currentNavTab == NavTab.STATISTICS,
                                        onClick = { viewModel.selectNavTab(NavTab.STATISTICS) },
                                        icon = { Icon(Icons.Default.BarChart, contentDescription = "Stats") },
                                        label = {
                                            Text(
                                                text = StringsRepo.get("nav_stats", settings.language),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        },
                                        colors = navigationItemColors()
                                    )

                                    NavigationBarItem(
                                        selected = currentNavTab == NavTab.APP_ENGINES,
                                        onClick = { viewModel.selectNavTab(NavTab.APP_ENGINES) },
                                        icon = { Icon(Icons.Default.Apps, contentDescription = "Apps") },
                                        label = {
                                            Text(
                                                text = StringsRepo.get("nav_apps", settings.language),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        },
                                        colors = navigationItemColors()
                                    )

                                    NavigationBarItem(
                                        selected = currentNavTab == NavTab.TECH_PLAN,
                                        onClick = { viewModel.selectNavTab(NavTab.TECH_PLAN) },
                                        icon = { Icon(Icons.Default.Code, contentDescription = "Tech Plan") },
                                        label = {
                                            Text(
                                                text = StringsRepo.get("nav_architecture", settings.language),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        },
                                        colors = navigationItemColors()
                                    )

                                    NavigationBarItem(
                                        selected = currentNavTab == NavTab.SETTINGS,
                                        onClick = { viewModel.selectNavTab(NavTab.SETTINGS) },
                                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                                        label = {
                                            Text(
                                                text = StringsRepo.get("nav_settings", settings.language),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        },
                                        colors = navigationItemColors()
                                    )
                                }
                            }
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                AnimatedContent(
                                    targetState = currentNavTab,
                                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                                    label = "nav_content"
                                ) { tab ->
                                    when (tab) {
                                        NavTab.DASHBOARD -> DashboardScreen(
                                            currentStats = currentStats,
                                            settings = settings,
                                            detectionStatus = detectionStatus,
                                            auditLogs = auditLogs,
                                            onToggleLanguage = { viewModel.toggleLanguage() },
                                            onUpdateDailyLimit = { viewModel.updateDailyLimit(it) },
                                            onToggleApp = { viewModel.toggleApp(it) },
                                            onSimulate = { viewModel.simulateDetection(it) },
                                            onManualAdd = { viewModel.applyManualCorrection(1, it) },
                                            onManualRemove = { viewModel.applyManualCorrection(-1, it) },
                                            onOpenStats = { viewModel.selectNavTab(NavTab.STATISTICS) },
                                            onTriggerFocus = { viewModel.openStrictMode() },
                                            onResetToday = { viewModel.resetTodayCount() }
                                        )

                                        NavTab.FLOATING_PILL -> FloatingPreviewScreen(
                                            currentStats = currentStats,
                                            settings = settings,
                                            onSimulateCandidate = { viewModel.simulateDetection(it) },
                                            onSimulateRapidSkip = { viewModel.simulateRapidSkip(it) },
                                            onSimulateReplay = { viewModel.simulateDuplicateReplay(it) },
                                            onUpdateSettings = { viewModel.updateSettings(it) }
                                        )

                                        NavTab.STATISTICS -> StatisticsScreen(
                                            currentStats = currentStats,
                                            recent7Days = recent7Days,
                                            settings = settings,
                                            onClearAllData = { viewModel.clearAllData() }
                                        )

                                        NavTab.APP_ENGINES -> AppManagementScreen(
                                            settings = settings,
                                            onToggleApp = { viewModel.toggleApp(it) }
                                        )

                                        NavTab.TECH_PLAN -> TechnicalArchitectureScreen(
                                            settings = settings
                                        )

                                        NavTab.SETTINGS -> SettingsScreen(
                                            settings = settings,
                                            onUpdateSettings = { viewModel.updateSettings(it) },
                                            onResetToday = { viewModel.resetTodayCount() }
                                        )

                                        NavTab.LICENSE -> SettingsScreen(
                                            settings = settings,
                                            onUpdateSettings = { viewModel.updateSettings(it) },
                                            onResetToday = { viewModel.resetTodayCount() }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun navigationItemColors() = NavigationBarItemDefaults.colors(
        selectedIconColor = PrimaryAccent,
        selectedTextColor = PrimaryAccent,
        unselectedIconColor = SecondaryText,
        unselectedTextColor = SecondaryText,
        indicatorColor = SurfaceElevated
    )

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.getBooleanExtra("EXTRA_NAVIGATE_STRICT_MODE", false)) {
            viewModel.openStrictMode()
        }
    }
}
