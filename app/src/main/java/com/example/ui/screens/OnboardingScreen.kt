package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
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
import com.example.data.model.StringsRepo
import com.example.data.model.TargetApp
import com.example.ui.theme.BorderColor
import com.example.ui.theme.MainBackground
import com.example.ui.theme.PrimaryAccent
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryAccent
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated

@Composable
fun OnboardingScreen(
    currentLang: String,
    onComplete: (Set<TargetApp>, Int, Int, Int) -> Unit
) {
    var step by remember { mutableIntStateOf(0) }
    var selectedApps by remember { mutableStateOf(TargetApp.entries.toSet()) }
    var dailyLimit by remember { mutableIntStateOf(20) }
    var resetHour by remember { mutableIntStateOf(0) }
    val isAr = currentLang == "ar"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Progress Dots
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                (0..3).forEach { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (step == index) 24.dp else 8.dp, 8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (step == index) PrimaryAccent else BorderColor)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Step Content
            AnimatedContent(
                targetState = step,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "onboarding_steps"
            ) { currentStep ->
                when (currentStep) {
                    0 -> WelcomeStep(isAr)
                    1 -> AppSelectionStep(selectedApps, isAr) { selectedApps = it }
                    2 -> QuotaStep(dailyLimit, resetHour, isAr, { dailyLimit = it }, { resetHour = it })
                    3 -> TransparencyStep(isAr)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Navigation Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (step > 0) {
                    OutlinedButton(
                        onClick = { step-- },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SecondaryText),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(StringsRepo.get("onboarding_back", currentLang))
                    }
                } else {
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Button(
                    onClick = {
                        if (step < 3) {
                            step++
                        } else {
                            onComplete(selectedApps, dailyLimit, resetHour, 0)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("onboarding_next_btn")
                ) {
                    Text(
                        text = if (step == 3) StringsRepo.get("onboarding_get_started", currentLang) else StringsRepo.get("onboarding_next", currentLang),
                        color = MainBackground,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun WelcomeStep(isAr: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(SurfaceElevated)
                .border(2.dp, PrimaryAccent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = PrimaryAccent,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "WAQFA",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = PrimaryAccent,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isAr) "استرجع سيطرتك على وقتك." else "Take Back Your Attention.",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryText
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (isAr)
                "فيديوهات الريلز والشورتس صممت لتبقيك أسيراً للتمرير اللانهائي. تطبيق WAQFA يمنحك رصيداً واعياً يومياً، عداداً عائماً شفافاً، واستراحة حتمية لحماية تركيزك."
            else
                "Short-form video algorithms are optimized for compulsive, mindless consumption. WAQFA helps you take back control with an intentional daily quota, persistent floating count, and strict pause interventions.",
            fontSize = 14.sp,
            lineHeight = 22.sp,
            color = SecondaryText
        )
    }
}

@Composable
private fun AppSelectionStep(
    selectedApps: Set<TargetApp>,
    isAr: Boolean,
    onSelectionChange: (Set<TargetApp>) -> Unit
) {
    Column {
        Text(
            text = if (isAr) "اختر المنصات المستهدفة" else "Target Platforms",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryText
        )
        Text(
            text = if (isAr) "سيتم رصد واحتساب مقاطع الفيديو في هذه التطبيقات فقط." else "Select which platforms to monitor and include in your daily quota.",
            fontSize = 13.sp,
            color = SecondaryText,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        TargetApp.entries.forEach { app ->
            val isSelected = selectedApps.contains(app)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SurfaceDark)
                    .border(1.dp, if (isSelected) PrimaryAccent else BorderColor, RoundedCornerShape(12.dp))
                    .clickable {
                        val set = selectedApps.toMutableSet()
                        if (isSelected) set.remove(app) else set.add(app)
                        onSelectionChange(set)
                    }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = app.title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
                    Text(text = app.shortFormatName, fontSize = 12.sp, color = SecondaryText)
                }
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { checked ->
                        val set = selectedApps.toMutableSet()
                        if (checked) set.add(app) else set.remove(app)
                        onSelectionChange(set)
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = PrimaryAccent,
                        checkmarkColor = MainBackground,
                        uncheckedColor = SecondaryText
                    )
                )
            }
        }
    }
}

@Composable
private fun QuotaStep(
    limit: Int,
    resetHour: Int,
    isAr: Boolean,
    onLimitChange: (Int) -> Unit,
    onResetHourChange: (Int) -> Unit
) {
    Column {
        Text(
            text = if (isAr) "حدد رصيدك اليومي" else "Set Your Daily Quota",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryText
        )
        Text(
            text = if (isAr) "كم مقطع فيديو قصير تسمح لنفسك بمشاهدته يومياً؟" else "How many short-form videos do you consciously budget each day?",
            fontSize = 13.sp,
            color = SecondaryText,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
        )

        // Large Number Display
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
                Text(text = "$limit", fontSize = 48.sp, fontWeight = FontWeight.Black, color = PrimaryAccent)
                Text(text = if (isAr) "فيديو يومياً" else "videos per day", fontSize = 14.sp, color = SecondaryText)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick presets
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf(10, 15, 20, 30, 50).forEach { preset ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (limit == preset) PrimaryAccent else SurfaceElevated)
                        .clickable { onLimitChange(preset) }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "$preset",
                        fontWeight = FontWeight.Bold,
                        color = if (limit == preset) MainBackground else PrimaryText
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Slider(
            value = limit.toFloat(),
            onValueChange = { onLimitChange(it.toInt()) },
            valueRange = 5f..100f,
            steps = 94,
            colors = SliderDefaults.colors(
                thumbColor = PrimaryAccent,
                activeTrackColor = PrimaryAccent,
                inactiveTrackColor = SurfaceElevated
            )
        )
    }
}

@Composable
private fun TransparencyStep(isAr: Boolean) {
    Column {
        Text(
            text = if (isAr) "الشفافية والخصوصية التامة" else "Transparency & Privacy",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryText
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SurfaceDark)
                .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = PrimaryAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAr) "بياناتك على جهازك فقط" else "100% Local Processing",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isAr)
                        "لا حسابات، لا إعلانات، ولا خوادم خارجية. التطبيق لا يسجل كلمات السر أو الرسائل ولا يلتقط صوراً للشاشة."
                    else
                        "No accounts, no telemetry servers, and no tracking. REEL LIMIT inspects container layout signatures locally on-device and never records screen pixels or keystrokes.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = SecondaryText
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SurfaceDark)
                .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = SecondaryAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAr) "صراحة هندسية" else "Technical Honesty",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryText
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isAr)
                        "المنصات تُحدث واجهاتها باستمرار لتفادي الحظر. نعتمد خوارزمية مرنة لمنع التكرار ونوفر لك خيار تصحيح يدوي سهل في أي وقت."
                    else
                        "Target apps continuously modify their internal layout trees. We employ debounce algorithms and hash deduplication, plus instant manual correction if an edge case occurs.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = SecondaryText
                )
            }
        }
    }
}
