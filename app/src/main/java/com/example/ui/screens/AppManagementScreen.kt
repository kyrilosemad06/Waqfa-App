package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DetectionConfidence
import com.example.data.model.StringsRepo
import com.example.data.model.TargetApp
import com.example.data.model.UserSettings
import com.example.ui.theme.BorderColor
import com.example.ui.theme.FacebookColor
import com.example.ui.theme.InstagramColor
import com.example.ui.theme.MainBackground
import com.example.ui.theme.PrimaryAccent
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryAccent
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TikTokColor
import com.example.ui.theme.WarningColor
import com.example.ui.theme.YouTubeColor

@Composable
fun AppManagementScreen(
    settings: UserSettings,
    onToggleApp: (TargetApp) -> Unit
) {
    val context = LocalContext.current
    val isAr = settings.language == "ar"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("app_management_screen")
    ) {
        Text(
            text = StringsRepo.get("app_engines_title", settings.language),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryText
        )
        Text(
            text = if (isAr) "تفاصيل المحركات التقنية المخصصة لكل منصة ومستويات الدقة." else "Platform-specific detection adapters, confidence ratings, and permission controls.",
            fontSize = 13.sp,
            color = SecondaryText,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // Honest Technical Transparency Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceDark)
                .border(1.dp, PrimaryAccent.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = PrimaryAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAr) "شفافية تقنية وصراحة هندسية" else "Technical Transparency Notice",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = StringsRepo.get("technical_honesty_banner", settings.language),
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = SecondaryText
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // System Permissions Management Cards
        Text(
            text = if (isAr) "صلاحيات نظام أندرويد" else "Android System Permissions",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryText
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Accessibility Service Card
        PermissionStatusRow(
            icon = Icons.Default.Accessibility,
            title = if (isAr) "خدمة إمكانية الوصول (Accessibility)" else "Accessibility Service",
            desc = if (isAr) "فحص هياكل واجهات المنصات لاكتشاف المقاطع محلياً" else "Inspects UI container events locally on device",
            actionLabel = if (isAr) "فتح الإعدادات" else "Configure",
            onAction = {
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Overlay Permission Card
        val hasOverlay = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Settings.canDrawOverlays(context) else true
        PermissionStatusRow(
            icon = Icons.Default.Layers,
            title = if (isAr) "الظهور فوق التطبيقات (Overlay)" else "Draw Over Other Apps",
            desc = if (isAr) "لعرض شريط العداد العائم وشاشة التوقف الوقائي" else "Required for persistent floating pill overlay",
            actionLabel = if (hasOverlay) (if (isAr) "ممنوحة" else "Granted") else (if (isAr) "منح" else "Grant"),
            isGranted = hasOverlay,
            onAction = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${context.packageName}")
                    ).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Platform-Specific Engine Adapters
        Text(
            text = if (isAr) "محركات المنصات المستهدفة" else "Platform Engine Breakdown",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryText
        )

        Spacer(modifier = Modifier.height(12.dp))

        TargetApp.entries.forEach { app ->
            val isEnabled = settings.enabledApps.contains(app)
            val platformColor = when (app) {
                TargetApp.INSTAGRAM -> InstagramColor
                TargetApp.YOUTUBE -> YouTubeColor
                TargetApp.TIKTOK -> TikTokColor
                TargetApp.FACEBOOK -> FacebookColor
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceDark)
                    .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .clip(CircleShape)
                                .background(platformColor)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = app.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryText
                            )
                            Text(
                                text = app.packageName,
                                fontSize = 11.sp,
                                color = SecondaryText
                            )
                        }
                    }

                    Switch(
                        checked = isEnabled,
                        onCheckedChange = { onToggleApp(app) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = PrimaryAccent,
                            checkedTrackColor = SurfaceElevated
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Technical explanation
                Text(
                    text = if (isAr) app.detectionNotesAr else app.detectionNotesEn,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    color = SecondaryText
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = StringsRepo.get("confidence_label", settings.language),
                        fontSize = 11.sp,
                        color = SecondaryText
                    )

                    val confLabel = when (app.defaultConfidence) {
                        DetectionConfidence.CONFIRMED -> if (isAr) "مؤكد بنسبة عالية (Confirmed)" else "Confirmed (Title Hash)"
                        DetectionConfidence.HIGH_PROBABILITY -> if (isAr) "دقة عالية (Container Transition)" else "High (Container Transition)"
                        DetectionConfidence.HEURISTIC_ESTIMATE -> if (isAr) "تقدير ذكي بزمن البقاء (Heuristic Dwell)" else "Heuristic Dwell (Obfuscated IDs)"
                    }

                    val confColor = when (app.defaultConfidence) {
                        DetectionConfidence.CONFIRMED -> PrimaryAccent
                        DetectionConfidence.HIGH_PROBABILITY -> SecondaryText
                        DetectionConfidence.HEURISTIC_ESTIMATE -> WarningColor
                    }

                    Text(
                        text = confLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = confColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun PermissionStatusRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String,
    actionLabel: String,
    isGranted: Boolean = false,
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceDark)
            .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isGranted) PrimaryAccent else SecondaryText,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
                Text(
                    text = desc,
                    fontSize = 11.sp,
                    color = SecondaryText
                )
            }
        }

        OutlinedButton(
            onClick = onAction,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = if (isGranted) PrimaryAccent else SecondaryText
            )
        ) {
            Text(text = actionLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}
