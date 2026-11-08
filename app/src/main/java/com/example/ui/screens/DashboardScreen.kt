package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import com.example.service.FloatingCounterService
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuditLogEntry
import com.example.data.model.DailyStats
import com.example.data.model.DetectionStatus
import com.example.data.model.StringsRepo
import com.example.data.model.TargetApp
import com.example.data.model.UserSettings
import com.example.ui.NavTab
import com.example.ui.components.AppBreakdownCard
import com.example.ui.components.CircularProgressMeter
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    currentStats: DailyStats,
    settings: UserSettings,
    detectionStatus: DetectionStatus,
    auditLogs: List<AuditLogEntry>,
    onToggleLanguage: () -> Unit,
    onUpdateDailyLimit: (Int) -> Unit,
    onToggleApp: (TargetApp) -> Unit,
    onSimulate: (TargetApp) -> Unit,
    onManualAdd: (TargetApp) -> Unit,
    onManualRemove: (TargetApp) -> Unit,
    onOpenStats: () -> Unit,
    onTriggerFocus: () -> Unit,
    onResetToday: () -> Unit
) {
    val isAr = settings.language == "ar"
    val context = LocalContext.current
    var showEditLimitDialog by remember { mutableStateOf(false) }
    var tempLimit by remember { mutableIntStateOf(settings.dailyLimit) }
    var showResetConfirmDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
            .padding(horizontal = 16.dp)
            .testTag("dashboard_screen")
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            // Header Row: App Name & Language Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SurfaceElevated)
                            .border(1.5.dp, PrimaryAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = PrimaryAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "WAQFA",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = PrimaryText
                        )
                        Text(
                            text = StringsRepo.get("tagline", settings.language),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = SecondaryText
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Language Switcher Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(SurfaceElevated)
                            .border(1.dp, BorderColor, RoundedCornerShape(20.dp))
                            .clickable { onToggleLanguage() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("lang_toggle_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Language",
                                tint = PrimaryAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAr) "English" else "العربية",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryText
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Detection Status Banner
            DetectionStatusBadge(detectionStatus, isAr)

            Spacer(modifier = Modifier.height(20.dp))

            // Main Circular Gauge
            CircularProgressMeter(
                currentCount = currentStats.totalCount,
                dailyLimit = settings.dailyLimit,
                warningThresholdPercent = settings.warningThresholdPercent,
                remainingLabel = StringsRepo.get("remaining_quota", settings.language),
                consumedLabel = StringsRepo.get("videos_consumed", settings.language)
            )

            // Direct Count Stepper Controls (+1 / -1)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { onManualRemove(TargetApp.INSTAGRAM) },
                    enabled = currentStats.totalCount > 0,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SecondaryText),
                    modifier = Modifier.padding(horizontal = 4.dp).testTag("dash_minus_one_btn")
                ) {
                    Icon(imageVector = Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = if (isAr) "-1 تصحيح" else "-1 Correct", fontSize = 13.sp)
                }

                Button(
                    onClick = { onSimulate(TargetApp.INSTAGRAM) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent),
                    modifier = Modifier.padding(horizontal = 4.dp).testTag("dash_plus_one_btn")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = MainBackground, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isAr) "+1 مشاهدة فيديو" else "+1 Video View",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MainBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Floating Overlay Trigger Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceElevated)
                    .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = null,
                        tint = PrimaryAccent,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isAr) "تفعيل العداد العائم فوق التطبيقات" else "Floating Overlay & System Access",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryText
                        )
                        Text(
                            text = if (isAr) "لعرض العداد العائم الحقيقي أثناء تصفح الفيديوهات" else "Display real-time pill over Instagram & YouTube",
                            fontSize = 11.sp,
                            color = SecondaryText
                        )
                    }
                }
                Button(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {
                            val intent = Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:${context.packageName}")
                            ).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                            context.startActivity(intent)
                        } else {
                            FloatingCounterService.start(context)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (isAr) "تشغيل" else "Start",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MainBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Quick Actions Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        tempLimit = settings.dailyLimit
                        showEditLimitDialog = true
                    },
                    modifier = Modifier.weight(1f).testTag("quick_edit_limit_btn"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryText),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = StringsRepo.get("quick_edit_limit", settings.language), fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onOpenStats,
                    modifier = Modifier.weight(1f).testTag("quick_stats_btn"),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryText),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.BarChart, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = StringsRepo.get("view_stats", settings.language), fontSize = 12.sp)
                }

                Button(
                    onClick = onTriggerFocus,
                    modifier = Modifier.weight(1.1f).testTag("quick_focus_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = StringsRepo.get("enter_focus_mode", settings.language),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MainBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Target Platforms Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = StringsRepo.get("app_engines_title", settings.language),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )

                Text(
                    text = if (isAr) "اضغط ▶ لاختبار الرصد" else "Tap ▶ to test view event",
                    fontSize = 11.sp,
                    color = SecondaryAccent
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        // List of Target App Cards
        items(TargetApp.entries) { app ->
            val isEnabled = settings.enabledApps.contains(app)
            val count = currentStats.countByApp[app] ?: 0

            AppBreakdownCard(
                app = app,
                count = count,
                isEnabled = isEnabled,
                onToggle = { onToggleApp(app) },
                onSimulate = { onSimulate(app) },
                onManualAdd = { onManualAdd(app) },
                onManualRemove = { onManualRemove(app) },
                isAr = isAr,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        // Live Deduplication & Audit Log Section
        item {
            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isAr) "سجل الرصد ومنع التكرار المباشر" else "Live Deduplication & Audit Log",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )

                TextButton(onClick = { showResetConfirmDialog = true }) {
                    Text(
                        text = StringsRepo.get("reset_today_confirm", settings.language),
                        fontSize = 12.sp,
                        color = WarningColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceDark)
                    .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                if (auditLogs.isEmpty()) {
                    Text(
                        text = if (isAr) "لا توجد أحداث مسجلة حتى الآن اليوم. اضغط على أزرار المحاكاة أعلاه لتجربة منع التكرار." else "No events recorded yet today. Use the play buttons above to simulate browsing and test deduplication.",
                        fontSize = 12.sp,
                        color = SecondaryText,
                        lineHeight = 18.sp
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        auditLogs.take(5).forEach { log ->
                            val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.US)
                            val timeStr = timeFormat.format(Date(log.timestamp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text(
                                    text = timeStr,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SecondaryText,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isAr) log.messageAr else log.messageEn,
                                    fontSize = 12.sp,
                                    color = if (log.isDeduplicated) WarningColor else PrimaryText,
                                    lineHeight = 16.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Edit Limit Dialog
    if (showEditLimitDialog) {
        AlertDialog(
            onDismissRequest = { showEditLimitDialog = false },
            title = {
                Text(
                    text = StringsRepo.get("quick_edit_limit", settings.language),
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$tempLimit",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black,
                        color = PrimaryAccent
                    )
                    Text(
                        text = StringsRepo.get("daily_limit_slider", settings.language),
                        fontSize = 12.sp,
                        color = SecondaryText
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Slider(
                        value = tempLimit.toFloat(),
                        onValueChange = { tempLimit = it.toInt() },
                        valueRange = 5f..100f,
                        steps = 94,
                        colors = SliderDefaults.colors(
                            thumbColor = PrimaryAccent,
                            activeTrackColor = PrimaryAccent,
                            inactiveTrackColor = SurfaceElevated
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateDailyLimit(tempLimit)
                        showEditLimitDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent)
                ) {
                    Text(StringsRepo.get("confirm", settings.language), color = MainBackground, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditLimitDialog = false }) {
                    Text(StringsRepo.get("cancel", settings.language), color = SecondaryText)
                }
            },
            containerColor = SurfaceDark
        )
    }

    // Reset Today Confirmation Dialog
    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = {
                Text(
                    text = StringsRepo.get("reset_today_confirm", settings.language),
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
            },
            text = {
                Text(
                    text = if (isAr) "هل تريد تصفير عداد فيديوهات اليوم؟ ستظل البيانات التاريخية للأيام السابقة محفوظة." else "Are you sure you want to reset today's counter to 0? Past historical days will remain intact.",
                    fontSize = 13.sp,
                    color = SecondaryText
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetToday()
                        showResetConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LimitReachedColor)
                ) {
                    Text(StringsRepo.get("confirm", settings.language), color = PrimaryText, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text(StringsRepo.get("cancel", settings.language), color = SecondaryText)
                }
            },
            containerColor = SurfaceDark
        )
    }
}

@Composable
private fun DetectionStatusBadge(status: DetectionStatus, isAr: Boolean) {
    val (label, color, desc) = when (status) {
        DetectionStatus.ACTIVE_MONITORING -> Triple(
            if (isAr) "الرصد المحلي نشط" else "Live Monitoring Active",
            PrimaryAccent,
            if (isAr) "يراقب إشارات الشاشة محلياً لمنع التكرار" else "Inspecting UI container transitions on-device"
        )
        DetectionStatus.DEMO_ACTIVE -> Triple(
            if (isAr) "وضع المحاكاة التفاعلية" else "Interactive Demo Mode",
            SecondaryAccent,
            if (isAr) "جاهز لاختبار أحداث المشاهدة ومنع التكرار" else "Ready to simulate video events & test deduplication"
        )
        DetectionStatus.PERMISSIONS_NEEDED -> Triple(
            if (isAr) "بانتظار الصلاحيات" else "Permissions Needed",
            WarningColor,
            if (isAr) "تتطلب تفعيل إمكانية الوصول أو الظهور فوق التطبيقات" else "Requires Accessibility or Overlay permissions"
        )
        DetectionStatus.PAUSED -> Triple(
            if (isAr) "الرصد متوقف مؤقتاً" else "Monitoring Paused",
            SecondaryText,
            if (isAr) "تم إيقاف الخدمة يدوياً" else "Service manually suspended"
        )
        DetectionStatus.IDLE -> Triple(
            if (isAr) "في وضع الخمول" else "Idle",
            SecondaryText,
            if (isAr) "لا توجد جلسة نشطة" else "No active viewing session"
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDark)
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = desc,
                fontSize = 11.sp,
                color = SecondaryText
            )
        }
    }
}
