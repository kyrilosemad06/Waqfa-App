package com.example.ui.screens

import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyStats
import com.example.data.model.StringsRepo
import com.example.data.model.TargetApp
import com.example.data.model.UserSettings
import com.example.service.FloatingCounterService
import com.example.ui.components.FloatingPillOverlayView
import com.example.ui.theme.BorderColor
import com.example.ui.theme.LimitReachedColor
import com.example.ui.theme.MainBackground
import com.example.ui.theme.PrimaryAccent
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryAccent
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.WarningColor

@Composable
fun FloatingPreviewScreen(
    currentStats: DailyStats,
    settings: UserSettings,
    onSimulateCandidate: (TargetApp) -> Unit,
    onSimulateRapidSkip: (TargetApp) -> Unit,
    onSimulateReplay: (TargetApp) -> Unit,
    onUpdateSettings: (UserSettings) -> Unit
) {
    val context = LocalContext.current
    val isAr = settings.language == "ar"
    var isRealServiceRunning by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("floating_preview_screen")
    ) {
        Text(
            text = StringsRepo.get("floating_title", settings.language),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryText
        )
        Text(
            text = StringsRepo.get("floating_desc", settings.language),
            fontSize = 13.sp,
            color = SecondaryText,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // The interactive draggable Floating Pill preview stage
        FloatingPillOverlayView(
            count = currentStats.totalCount,
            limit = settings.dailyLimit,
            warningThresholdPercent = settings.warningThresholdPercent,
            isAr = isAr
        )

        Spacer(modifier = Modifier.height(20.dp))

        // System Floating Service Toggle (Real Android Foreground Service)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceDark)
                .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Default.Layers,
                    contentDescription = null,
                    tint = PrimaryAccent,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (isAr) "تشغيل الخدمة العائمة بالخلفية" else "Launch Real System Overlay",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                    Text(
                        text = if (isAr) "يبقي العداد ظاهراً فوق إنستغرام ويوتيوب أثناء التصفح" else "Keeps pill visible over other apps using Foreground Service",
                        fontSize = 11.sp,
                        color = SecondaryText
                    )
                }
            }

            Switch(
                checked = isRealServiceRunning,
                onCheckedChange = { start ->
                    if (start) {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context)) {
                            FloatingCounterService.start(context)
                            isRealServiceRunning = true
                        }
                    } else {
                        FloatingCounterService.stop(context)
                        isRealServiceRunning = false
                    }
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = PrimaryAccent,
                    checkedTrackColor = SurfaceElevated
                )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Simulation Test Suite Controls
        Text(
            text = if (isAr) "مختبر محاكاة الرصد ومنع التكرار" else "Detection & Deduplication Lab",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryText
        )
        Text(
            text = if (isAr) "جرب سيناريوهات مختلفة لاختبار استجابة المحرك وخوارزمية التكرار." else "Simulate various viewing scenarios to observe counter logic and deduplication.",
            fontSize = 12.sp,
            color = SecondaryText,
            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
        )

        // Sim Buttons Grid
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Normal Video View
            Button(
                onClick = { onSimulateCandidate(TargetApp.INSTAGRAM) },
                modifier = Modifier.fillMaxWidth().testTag("sim_valid_event_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceElevated),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = PrimaryAccent)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = if (isAr) "مشاهدة فيديو صالحة (+1 إنستغرام)" else "Valid Video View (+1 Instagram)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText
                        )
                        Text(
                            text = if (isAr) "فيديو جديد استقر لأكثر من ثانيتين -> يُحتسب" else "New video hash, dwell > 2s -> Count incremented",
                            fontSize = 10.sp,
                            color = SecondaryText
                        )
                    }
                }
            }

            // Rapid Skip (Debounce test)
            Button(
                onClick = { onSimulateRapidSkip(TargetApp.INSTAGRAM) },
                modifier = Modifier.fillMaxWidth().testTag("sim_rapid_skip_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceElevated),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.FastForward, contentDescription = null, tint = WarningColor)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = if (isAr) "تخطي سريع خاطف (< 1.2 ثانية)" else "Rapid Skip (< 1.2s Dwell)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText
                        )
                        Text(
                            text = if (isAr) "يتم تجاهله تلقائياً بواسطة خوارزمية منع التمرير السريع" else "Rejected by rapid-scroll debounce -> Won't count",
                            fontSize = 10.sp,
                            color = SecondaryText
                        )
                    }
                }
            }

            // Duplicate Replay
            Button(
                onClick = { onSimulateReplay(TargetApp.INSTAGRAM) },
                modifier = Modifier.fillMaxWidth().testTag("sim_replay_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceElevated),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = SecondaryAccent)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = if (isAr) "إعادة تشغيل نفس الفيديو السابق" else "Replay Same Video",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText
                        )
                        Text(
                            text = if (isAr) "يتم كشف تشابه المعرف وحجبه بواسطة جدول البصمات (Hash Table)" else "Identical content hash detected -> Deduplicated",
                            fontSize = 10.sp,
                            color = SecondaryText
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
