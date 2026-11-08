package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.DailyStats
import com.example.data.model.StringsRepo
import com.example.data.model.TargetApp
import com.example.data.model.UserSettings
import com.example.ui.components.SevenDayBarChart
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
fun StatisticsScreen(
    currentStats: DailyStats,
    recent7Days: List<DailyStats>,
    settings: UserSettings,
    onClearAllData: () -> Unit
) {
    val isAr = settings.language == "ar"
    var showClearConfirm by remember { mutableStateOf(false) }

    // Calculate metrics
    val streakDays = recent7Days.count { it.totalCount <= settings.dailyLimit }
    val totalVideosInWeek = recent7Days.sumOf { it.totalCount }
    val avgDaily = if (recent7Days.isNotEmpty()) totalVideosInWeek / recent7Days.size else currentStats.totalCount

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("statistics_screen")
    ) {
        Text(
            text = StringsRepo.get("stats_title", settings.language),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryText
        )
        Text(
            text = if (isAr) "تحليلات دقيقة لاسترجاع انتباهك مبنية على قياسات محلية 100%." else "Transparent metrics derived entirely from on-device activity.",
            fontSize = 13.sp,
            color = SecondaryText,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // KPI Metric Cards Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Streak Card
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceDark)
                    .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = WarningColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = StringsRepo.get("adherence_streak", settings.language),
                            fontSize = 11.sp,
                            color = SecondaryText,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$streakDays ${if (isAr) "أيام" else "days"}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = PrimaryText
                    )
                }
            }

            // Average Daily Card
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceDark)
                    .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Equalizer,
                            contentDescription = null,
                            tint = PrimaryAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = StringsRepo.get("average_daily", settings.language),
                            fontSize = 11.sp,
                            color = SecondaryText,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$avgDaily ${if (isAr) "فيديو/يوم" else "vids/day"}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = PrimaryText
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 7-Day Chart
        SevenDayBarChart(
            days = recent7Days,
            dailyLimit = settings.dailyLimit,
            isAr = isAr
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Platform Breakdown
        Text(
            text = StringsRepo.get("app_breakdown_title", settings.language),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryText
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceDark)
                .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            val total = currentStats.totalCount
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                TargetApp.entries.forEach { app ->
                    val appCount = currentStats.countByApp[app] ?: 0
                    val pct = if (total > 0) ((appCount.toFloat() / total.toFloat()) * 100).toInt() else 0

                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = app.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                            Text(text = "$appCount ($pct%)", fontSize = 12.sp, color = SecondaryText)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(SurfaceElevated)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(if (total > 0) appCount.toFloat() / total.toFloat() else 0f)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(PrimaryAccent)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Confirmed vs Heuristic Breakdown
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceDark)
                .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = PrimaryAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = StringsRepo.get("confirmed_vs_estimated", settings.language),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isAr) "أحداث مؤكدة بالبصمة:" else "Confirmed Events (Unique Hash):",
                        fontSize = 12.sp,
                        color = SecondaryText
                    )
                    Text(
                        text = "${currentStats.confirmedCount}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryAccent
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isAr) "تقديرات مستندة لزمن البقاء:" else "Heuristic Dwell Estimates:",
                        fontSize = 12.sp,
                        color = SecondaryText
                    )
                    Text(
                        text = "${currentStats.estimatedCount}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = WarningColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Delete Data Button
        OutlinedButton(
            onClick = { showClearConfirm = true },
            modifier = Modifier.fillMaxWidth().testTag("clear_all_data_btn"),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = LimitReachedColor),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = LimitReachedColor)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = StringsRepo.get("clear_all_data", settings.language), color = LimitReachedColor)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }

    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            title = {
                Text(
                    text = StringsRepo.get("clear_all_data", settings.language),
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
            },
            text = {
                Text(
                    text = StringsRepo.get("clear_data_confirm", settings.language),
                    fontSize = 13.sp,
                    color = SecondaryText
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllData()
                        showClearConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LimitReachedColor)
                ) {
                    Text(StringsRepo.get("confirm", settings.language), color = PrimaryText, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text(StringsRepo.get("cancel", settings.language), color = SecondaryText)
                }
            },
            containerColor = SurfaceDark
        )
    }
}
