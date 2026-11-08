package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WaqfaHomeScreen
import com.example.ui.screens.WaqfaTrackingControlScreen
import com.example.ui.theme.BorderColor
import com.example.ui.theme.MainBackground
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryAccent
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.WaqfaTheme
import com.example.licensing.LicenseRepository
import com.example.licensing.LicenseStatus
import com.example.ui.screens.LicenseActivationScreen
import com.example.ui.screens.LicenseDetailsScreen
import com.example.ui.screens.FocusPlanScreen

@Composable
fun WaqfaApp(viewModel: MainViewModel, licenseRepository: LicenseRepository) {
    val settings by viewModel.settings.collectAsState()
    val stats by viewModel.currentStats.collectAsState()
    val status by viewModel.detectionStatus.collectAsState()
    val currentTab by viewModel.currentNavTab.collectAsState()
    val licenseState by licenseRepository.state.collectAsState()

    WaqfaTheme {
        if (!licenseState.isActive) {
            LicenseActivationScreen(licenseRepository)
            return@WaqfaTheme
        }
        if (!settings.onboardingCompleted) {
            FocusPlanScreen { baseline, target, strictness, apps -> viewModel.startFocusPlan(baseline, target, strictness, apps) }
            return@WaqfaTheme
        }
        BackHandler(enabled = currentTab != NavTab.DASHBOARD) { viewModel.selectNavTab(NavTab.DASHBOARD) }
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(MainBackground, Color(0xFF071D3B), MainBackground)))) {
            Box(Modifier.fillMaxSize().padding(bottom = 94.dp)) {
                when (currentTab) {
                    NavTab.DASHBOARD -> WaqfaHomeScreen(stats, settings, status, { viewModel.toggleLanguage() }, { viewModel.selectNavTab(NavTab.APP_ENGINES) })
                    NavTab.APP_ENGINES -> WaqfaTrackingControlScreen(settings, { viewModel.toggleApp(it) })
                    NavTab.SETTINGS -> SettingsScreen(settings, { viewModel.updateSettings(it) }, { viewModel.resetTodayCount() }, { viewModel.selectNavTab(NavTab.LICENSE) })
                    NavTab.LICENSE -> LicenseDetailsScreen(licenseRepository, { viewModel.selectNavTab(NavTab.SETTINGS) })
                    else -> WaqfaHomeScreen(stats, settings, status, { viewModel.toggleLanguage() }, { viewModel.selectNavTab(NavTab.APP_ENGINES) })
                }
            }
            FloatingNavigation(currentTab, settings.language == "ar", viewModel::selectNavTab, Modifier.align(Alignment.BottomCenter).padding(horizontal = 18.dp, vertical = 17.dp))
        }
    }
}

@Composable
private fun FloatingNavigation(selected: NavTab, isAr: Boolean, onSelect: (NavTab) -> Unit, modifier: Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().height(72.dp).clip(RoundedCornerShape(26.dp)).background(SurfaceDark).border(1.dp, BorderColor, RoundedCornerShape(26.dp)).padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavButton(Icons.Default.Home, if (isAr) "الرئيسية" else "Home", selected == NavTab.DASHBOARD) { onSelect(NavTab.DASHBOARD) }
        NavButton(Icons.Default.Apps, if (isAr) "المتابعة" else "Tracking", selected == NavTab.APP_ENGINES) { onSelect(NavTab.APP_ENGINES) }
        NavButton(Icons.Default.Settings, if (isAr) "الإعدادات" else "Settings", selected == NavTab.SETTINGS) { onSelect(NavTab.SETTINGS) }
    }
}

@Composable
private fun NavButton(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .background(if (selected) SecondaryAccent.copy(alpha = .16f) else androidx.compose.ui.graphics.Color.Transparent)
            .padding(horizontal = 22.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, label, tint = if (selected) SecondaryAccent else SecondaryText, modifier = Modifier.size(20.dp))
        Text(label, color = if (selected) SecondaryAccent else SecondaryText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}
