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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyStats
import com.example.R
import com.example.data.model.DetectionStatus
import com.example.data.model.TargetApp
import com.example.data.model.UserSettings
import com.example.ui.components.CircularProgressMeter
import com.example.ui.theme.BorderColor
import com.example.ui.theme.MainBackground
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryAccent
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated

@Composable
fun WaqfaHomeScreen(
    currentStats: DailyStats,
    settings: UserSettings,
    detectionStatus: DetectionStatus,
    onToggleLanguage: () -> Unit,
    onOpenTracking: () -> Unit
) {
    val isAr = settings.language == "ar"
    val greetingName = settings.userName.ifBlank { if (isAr) "صديقي" else "there" }
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(MainBackground).padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(Modifier.height(22.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(46.dp).clip(RoundedCornerShape(14.dp)).background(SurfaceElevated).border(1.dp, SecondaryAccent.copy(alpha = .55f), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                        Image(painter = painterResource(R.drawable.waqfa_launcher_logo), contentDescription = "WAQFA logo", modifier = Modifier.size(42.dp), contentScale = ContentScale.Fit)
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("WAQFA", color = PrimaryText, fontWeight = FontWeight.Black, letterSpacing = 1.4.sp, fontSize = 18.sp)
                        Text(if (isAr) "استرجع سيطرتك على وقتك" else "Take back your attention", color = SecondaryAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Row(Modifier.clip(RoundedCornerShape(18.dp)).background(SurfaceElevated).clickable(onClick = onToggleLanguage).padding(horizontal = 10.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Language, null, tint = SecondaryAccent, modifier = Modifier.size(15.dp)); Spacer(Modifier.width(5.dp)); Text(if (isAr) "EN" else "ع", color = PrimaryText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(28.dp))
            Text(if (isAr) "أهلًا، $greetingName" else "Welcome, $greetingName", color = PrimaryText, fontWeight = FontWeight.Black, fontSize = 26.sp)
            Text(if (isAr) "خطة وقفة: من ${settings.habitBaseline} إلى ${settings.dailyLimit} يوميًا" else "Your WAQFA plan: ${settings.dailyLimit} videos daily", color = SecondaryText, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
            Spacer(Modifier.height(8.dp))
            CircularProgressMeter(currentStats.totalCount, settings.dailyLimit, settings.warningThresholdPercent, remainingLabel = if (isAr) "متبقي اليوم" else "remaining today", consumedLabel = if (isAr) "مقاطع شاهدتها" else "videos watched")
            if (currentStats.totalCount == 0) Text(if (isAr) "بداية نظيفة — لم يتم احتساب أي مقطع اليوم بعد." else "A clean start — no videos counted today.", color = SecondaryAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
            Spacer(Modifier.height(18.dp))
        }
        item {
            val statusText = when (detectionStatus) {
                DetectionStatus.ACTIVE_MONITORING -> if (isAr) "العدّ التلقائي يعمل الآن" else "Automatic counting is on"
                DetectionStatus.PERMISSIONS_NEEDED -> if (isAr) "فعّل الصلاحيات لبدء العد" else "Enable permissions to start counting"
                else -> if (isAr) "في انتظار تفعيل المتابعة" else "Waiting for tracking"
            }
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(SurfaceDark).border(1.dp, BorderColor, RoundedCornerShape(18.dp)).padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(38.dp).clip(CircleShape).background(SecondaryAccent.copy(alpha=.15f)), contentAlignment = Alignment.Center) { Icon(Icons.Default.Accessibility, null, tint = SecondaryAccent) }
                Spacer(Modifier.width(11.dp)); Column(Modifier.weight(1f)) { Text(statusText, color = PrimaryText, fontWeight = FontWeight.Bold, fontSize = 14.sp); Text(if (isAr) "يحسب مقاطع الفيديو القصيرة أثناء التمرير" else "Counts short videos while you scroll", color = SecondaryText, fontSize = 11.sp) }
            }
            Spacer(Modifier.height(18.dp)); Text(if (isAr) "المنصات التي تتابعها" else "Platforms you track", color = PrimaryText, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.fillMaxWidth()); Spacer(Modifier.height(10.dp))
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TargetApp.entries.forEach { app ->
                    val enabled = app in settings.enabledApps
                    Column(Modifier.weight(1f).clip(RoundedCornerShape(15.dp)).background(if (enabled) SurfaceElevated else SurfaceDark).border(1.dp, if (enabled) SecondaryAccent.copy(alpha=.45f) else BorderColor, RoundedCornerShape(15.dp)).padding(vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text(app.title.take(1), color = if(enabled) SecondaryAccent else SecondaryText, fontWeight = FontWeight.Black, fontSize = 18.sp); Text(app.title, color = PrimaryText, fontSize = 9.sp, maxLines = 1) }
                }
            }
            Spacer(Modifier.height(18.dp))
            Button(onClick = onOpenTracking, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(17.dp), colors = ButtonDefaults.buttonColors(containerColor = SecondaryAccent, contentColor = MainBackground)) { Icon(Icons.Default.Settings, null, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text(if (isAr) "إدارة المتابعة والصلاحيات" else "Manage tracking & permissions", fontWeight = FontWeight.Black) }
            Spacer(Modifier.height(130.dp))
        }
    }
}
