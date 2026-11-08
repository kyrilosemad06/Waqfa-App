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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StringsRepo
import com.example.data.model.UserSettings
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
fun SettingsScreen(
    settings: UserSettings,
    onUpdateSettings: (UserSettings) -> Unit,
    onResetToday: () -> Unit,
    onOpenLicense: () -> Unit = {}
) {
    val isAr = settings.language == "ar"
    var showResetDialog by remember { mutableStateOf(false) }
    var displayName by remember(settings.userName) { mutableStateOf(settings.userName) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("settings_screen")
    ) {
        Text(
            text = StringsRepo.get("settings_title", settings.language),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryText
        )

        Row(
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(SurfaceDark).border(1.dp, BorderColor, RoundedCornerShape(16.dp)).clickable(onClick = onOpenLicense).padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(if (isAr) "رخصتي" else "My license", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                Text(if (isAr) "التفعيل، تاريخ الانتهاء ونقل الجهاز" else "Activation, expiry and device transfer", fontSize = 11.sp, color = SecondaryText)
            }
            Icon(Icons.Default.Security, null, tint = SecondaryAccent)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (isAr) "تخصيص قواعد الرصد، السلوك الصارم، وحدود الحصص اليومية." else "Configure quotas, strict mode pause triggers, and daily reset times.",
            fontSize = 13.sp,
            color = SecondaryText,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceDark)
                .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Text(
                text = if (isAr) "رسالة الترحيب" else "Welcome message",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText
            )
            Text(
                text = if (isAr) "اكتب اسمك ليظهر في الرئيسية." else "Your name appears on the home screen.",
                fontSize = 11.sp,
                color = SecondaryText,
                modifier = Modifier.padding(top = 3.dp, bottom = 10.dp)
            )
            OutlinedTextField(
                value = displayName,
                onValueChange = {
                    displayName = it
                    onUpdateSettings(settings.copy(userName = it.take(32)))
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text(if (isAr) "اسمك" else "Your name") },
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SecondaryAccent,
                    unfocusedBorderColor = BorderColor,
                    focusedTextColor = PrimaryText,
                    unfocusedTextColor = PrimaryText,
                    focusedLabelColor = SecondaryAccent,
                    unfocusedLabelColor = SecondaryText
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Language Selector Card
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = PrimaryAccent)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = StringsRepo.get("language_title", settings.language), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                    Text(text = if (isAr) "العربية (RTL)" else "English (LTR)", fontSize = 11.sp, color = SecondaryText)
                }
            }

            Row {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (!isAr) PrimaryAccent else SurfaceElevated)
                        .clickable { onUpdateSettings(settings.copy(language = "en")) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = "EN", fontWeight = FontWeight.Bold, color = if (!isAr) MainBackground else PrimaryText)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isAr) PrimaryAccent else SurfaceElevated)
                        .clickable { onUpdateSettings(settings.copy(language = "ar")) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(text = "عربي", fontWeight = FontWeight.Bold, color = if (isAr) MainBackground else PrimaryText)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Daily Limit Slider Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
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
                Text(
                    text = StringsRepo.get("daily_limit_slider", settings.language),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
                Text(
                    text = "${settings.dailyLimit} ${if (isAr) "فيديو" else "videos"}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    color = PrimaryAccent
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Slider(
                value = settings.dailyLimit.toFloat(),
                onValueChange = { onUpdateSettings(settings.copy(dailyLimit = it.toInt())) },
                valueRange = 5f..100f,
                steps = 94,
                colors = SliderDefaults.colors(
                    thumbColor = PrimaryAccent,
                    activeTrackColor = PrimaryAccent,
                    inactiveTrackColor = SurfaceElevated
                )
            )

            // Preset Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf(10, 15, 20, 25, 30, 50).forEach { preset ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (settings.dailyLimit == preset) PrimaryAccent else SurfaceElevated)
                            .clickable { onUpdateSettings(settings.copy(dailyLimit = preset)) }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "$preset",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (settings.dailyLimit == preset) MainBackground else PrimaryText
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Warning Threshold Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
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
                Text(
                    text = StringsRepo.get("warning_threshold_title", settings.language),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
                Text(
                    text = "${settings.warningThresholdPercent}% (${(settings.dailyLimit * settings.warningThresholdPercent) / 100} vids)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = WarningColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Slider(
                value = settings.warningThresholdPercent.toFloat(),
                onValueChange = { onUpdateSettings(settings.copy(warningThresholdPercent = it.toInt())) },
                valueRange = 50f..95f,
                steps = 8,
                colors = SliderDefaults.colors(
                    thumbColor = WarningColor,
                    activeTrackColor = WarningColor,
                    inactiveTrackColor = SurfaceElevated
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Daily Reset Time Card
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.AccessTime, contentDescription = null, tint = SecondaryAccent)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = StringsRepo.get("reset_time_title", settings.language),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                    Text(
                        text = if (isAr) "تجديد الرصيد تلقائياً كل ليلة" else "Resets quota based on your local timezone",
                        fontSize = 11.sp,
                        color = SecondaryText
                    )
                }
            }

            Text(
                text = String.format("%02d:%02d", settings.resetHour, settings.resetMinute),
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = PrimaryText
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Strict Mode Toggle
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = StringsRepo.get("strict_mode_title", settings.language),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
                Text(
                    text = StringsRepo.get("strict_mode_desc", settings.language),
                    fontSize = 11.sp,
                    color = SecondaryText
                )
            }

            Switch(
                checked = settings.strictModeEnabled,
                onCheckedChange = { onUpdateSettings(settings.copy(strictModeEnabled = it)) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = PrimaryAccent,
                    checkedTrackColor = SurfaceElevated
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Deliberate Exception Toggle
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = StringsRepo.get("exceptions_title", settings.language),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
                Text(
                    text = StringsRepo.get("exceptions_desc", settings.language),
                    fontSize = 11.sp,
                    color = SecondaryText
                )
            }

            Switch(
                checked = settings.exceptionsEnabled,
                onCheckedChange = { onUpdateSettings(settings.copy(exceptionsEnabled = it)) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = WarningColor,
                    checkedTrackColor = SurfaceElevated
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Reset Today's Count button
        OutlinedButton(
            onClick = { showResetDialog = true },
            modifier = Modifier.fillMaxWidth().testTag("reset_today_settings_btn"),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = WarningColor),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = WarningColor)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = StringsRepo.get("reset_today_confirm", settings.language), color = WarningColor)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(
                    text = StringsRepo.get("reset_today_confirm", settings.language),
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
            },
            text = {
                Text(
                    text = if (isAr) "هل أنت متأكد من تصفير عداد اليوم إلى 0؟" else "Are you sure you want to reset today's video count to 0?",
                    fontSize = 13.sp,
                    color = SecondaryText
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResetToday()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LimitReachedColor)
                ) {
                    Text(StringsRepo.get("confirm", settings.language), color = PrimaryText, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(StringsRepo.get("cancel", settings.language), color = SecondaryText)
                }
            },
            containerColor = SurfaceDark
        )
    }
}
