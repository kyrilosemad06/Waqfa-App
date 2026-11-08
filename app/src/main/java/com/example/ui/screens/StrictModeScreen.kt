package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyStats
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
import java.util.Calendar

@Composable
fun StrictModeScreen(
    currentStats: DailyStats,
    settings: UserSettings,
    frictionCountdown: Int?,
    onStartFriction: () -> Unit,
    onCancelFriction: () -> Unit,
    onReturnHome: () -> Unit
) {
    val isAr = settings.language == "ar"

    // Calculate time until next daily reset
    val now = Calendar.getInstance()
    val nextReset = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, settings.resetHour)
        set(Calendar.MINUTE, settings.resetMinute)
        set(Calendar.SECOND, 0)
        if (timeInMillis <= now.timeInMillis) {
            add(Calendar.DAY_OF_YEAR, 1)
        }
    }
    val diffMs = nextReset.timeInMillis - now.timeInMillis
    val hoursRemaining = (diffMs / (1000 * 60 * 60)).toInt()
    val minutesRemaining = ((diffMs / (1000 * 60)) % 60).toInt()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
            .padding(24.dp)
            .testTag("strict_mode_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Emotive Icon
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF250E11))
                    .border(2.dp, LimitReachedColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PauseCircle,
                    contentDescription = null,
                    tint = LimitReachedColor,
                    modifier = Modifier.size(54.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Arabic / English Headlines
            Text(
                text = StringsRepo.get("limit_reached_title", settings.language),
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = LimitReachedColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = StringsRepo.get("limit_reached_subtitle", settings.language),
                fontSize = 16.sp,
                color = SecondaryText,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Time Until Reset Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceDark)
                    .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.HourglassBottom,
                        contentDescription = null,
                        tint = SecondaryAccent,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = StringsRepo.get("time_until_reset", settings.language),
                        fontSize = 12.sp,
                        color = SecondaryText
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "${hoursRemaining}h ${minutesRemaining}m",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = PrimaryText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${currentStats.totalCount} / ${settings.dailyLimit} ${if (isAr) "فيديو تم استهلاكها" else "videos consumed today"}",
                        fontSize = 12.sp,
                        color = SecondaryText
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Honest Technical Enforcement Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceElevated)
                    .padding(14.dp)
            ) {
                Text(
                    text = StringsRepo.get("native_enforcement_note", settings.language),
                    fontSize = 11.sp,
                    color = SecondaryText,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Intentional Exception Friction Flow
            if (settings.exceptionsEnabled) {
                val hasExceptionsLeft = currentStats.exceptionsUsedToday < settings.maxExceptionsPerDay

                if (frictionCountdown != null) {
                    // Active 10-second deliberate friction delay
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SurfaceElevated)
                            .border(1.dp, WarningColor, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = StringsRepo.get("exception_countdown", settings.language),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = WarningColor
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${frictionCountdown}s",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            color = PrimaryText
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isAr) "توقف وتأمل: هل تحتاج هذه الفيديوهات حقاً؟" else "Pause and reflect: Is this intentional or impulsive?",
                            fontSize = 11.sp,
                            color = SecondaryText,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = onCancelFriction,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = SecondaryText),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(StringsRepo.get("cancel", settings.language))
                        }
                    }
                } else if (hasExceptionsLeft) {
                    OutlinedButton(
                        onClick = onStartFriction,
                        modifier = Modifier.fillMaxWidth().testTag("request_exception_btn"),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WarningColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.SelfImprovement, contentDescription = null, tint = WarningColor)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = StringsRepo.get("exception_button", settings.language),
                            fontSize = 13.sp,
                            color = WarningColor
                        )
                    }
                } else {
                    Text(
                        text = StringsRepo.get("exception_used_all", settings.language),
                        fontSize = 12.sp,
                        color = SecondaryText
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Return to Home Button
            Button(
                onClick = onReturnHome,
                modifier = Modifier.fillMaxWidth().testTag("return_home_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = StringsRepo.get("return_home", settings.language),
                    fontWeight = FontWeight.Bold,
                    color = MainBackground
                )
            }
        }
    }
}
