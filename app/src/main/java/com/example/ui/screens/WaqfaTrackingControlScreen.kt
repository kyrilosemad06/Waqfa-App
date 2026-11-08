package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
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
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.data.model.TargetApp
import com.example.data.model.UserSettings
import com.example.service.FloatingCounterService
import com.example.ui.theme.BorderColor
import com.example.ui.theme.BrandTeal
import com.example.ui.theme.MainBackground
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryAccent
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated

@Composable
fun WaqfaTrackingControlScreen(settings: UserSettings, onToggleApp: (TargetApp) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var refreshKey by remember { mutableIntStateOf(0) }
    DisposableEffect(lifecycleOwner) {
        val observer = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) { refreshKey++ }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    val accessibilityOn = remember(refreshKey) { isWaqfaAccessibilityOn(context) }
    val overlayOn = remember(refreshKey) { Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context) }
    val isAr = settings.language == "ar"
    LaunchedEffect(accessibilityOn, overlayOn, settings.floatingCounterEnabled) {
        if (accessibilityOn && overlayOn && settings.floatingCounterEnabled) FloatingCounterService.start(context)
    }

    Column(Modifier.fillMaxSize().background(MainBackground).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 22.dp)) {
        Text(if (isAr) "التحكم والمتابعة" else "Control & tracking", fontSize = 25.sp, fontWeight = FontWeight.Black, color = PrimaryText)
        Text(if (isAr) "فعّل الخطوتين لتبدأ WAQFA العدّ والمنع تلقائيًا." else "Turn on both steps for automatic counting and blocking.", color = SecondaryText, fontSize = 13.sp, modifier = Modifier.padding(top = 5.dp, bottom = 18.dp))

        PermissionStatusCard(Icons.Default.Accessibility, if (isAr) "1. خدمة إمكانية الوصول" else "1. Accessibility service", if (accessibilityOn) if (isAr) "مفعّلة — WAQFA يراقب التمرير الآن." else "Enabled — WAQFA can monitor scrolling." else if (isAr) "مطلوبة لعدّ الريلز تلقائيًا." else "Required for automatic Reel counting.", accessibilityOn, if (isAr) "فتح الإعدادات" else "Open settings") {
            context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
        Spacer(Modifier.height(10.dp))
        PermissionStatusCard(Icons.Default.Layers, if (isAr) "2. الظهور فوق التطبيقات" else "2. Display over apps", if (overlayOn) if (isAr) "مفعّل — البانر والحاجز يعملان." else "Enabled — banner and blocker can appear." else if (isAr) "مطلوب لإظهار العداد والمنع." else "Required for the counter and blocker.", overlayOn, if (isAr) "فتح الإعدادات" else "Open settings") {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) context.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
        if (accessibilityOn && overlayOn) {
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(BrandTeal.copy(alpha = .12f)).border(1.dp, BrandTeal.copy(alpha = .55f), RoundedCornerShape(16.dp)).padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, null, tint = BrandTeal); Spacer(Modifier.width(9.dp)); Text(if (isAr) "كل شيء مفعّل. سيظهر عداد WAQFA ويتحدث تلقائيًا." else "Everything is enabled. WAQFA updates automatically.", color = PrimaryText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.height(24.dp))
        Text(if (isAr) "المنصات المفعّلة" else "Enabled platforms", color = PrimaryText, fontWeight = FontWeight.Bold, fontSize = 17.sp)
        Text(if (isAr) "فعّل المنصات التي تريد عدّ مقاطعها القصيرة ومنعها عند الحد." else "Select platforms to count and block at your limit.", color = SecondaryText, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp, bottom = 10.dp))
        TargetApp.entries.forEach { app ->
            val enabled = app in settings.enabledApps
            Row(Modifier.fillMaxWidth().padding(vertical = 5.dp).clip(RoundedCornerShape(17.dp)).background(if (enabled) SurfaceElevated else SurfaceDark).border(1.dp, if (enabled) SecondaryAccent.copy(alpha = .5f) else BorderColor, RoundedCornerShape(17.dp)).clickable { onToggleApp(app) }.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(39.dp).clip(CircleShape).background(SecondaryAccent.copy(alpha = .14f)), contentAlignment = Alignment.Center) { Text(app.title.take(1), color = SecondaryAccent, fontSize = 18.sp, fontWeight = FontWeight.Black) }
                Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(app.title, color = PrimaryText, fontWeight = FontWeight.Bold); Text(app.shortFormatName, color = SecondaryText, fontSize = 12.sp) }
                Switch(checked = enabled, onCheckedChange = { onToggleApp(app) }, colors = SwitchDefaults.colors(checkedThumbColor = MainBackground, checkedTrackColor = BrandTeal, uncheckedThumbColor = SecondaryText, uncheckedTrackColor = SurfaceDark))
            }
        }
        Spacer(Modifier.height(112.dp))
    }
}

private fun isWaqfaAccessibilityOn(context: Context): Boolean {
    val enabled = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES) ?: return false
    return enabled.split(':').any { it.equals("${context.packageName}/com.example.service.ReelLimitAccessibilityService", ignoreCase = true) }
}

@Composable
private fun PermissionStatusCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, description: String, granted: Boolean, action: String, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(SurfaceDark).border(1.dp, if (granted) BrandTeal.copy(alpha = .55f) else BorderColor, RoundedCornerShape(18.dp)).clickable(onClick = onClick).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(40.dp).clip(RoundedCornerShape(13.dp)).background(if (granted) BrandTeal.copy(alpha = .14f) else SecondaryAccent.copy(alpha = .14f)), contentAlignment = Alignment.Center) { Icon(if (granted) Icons.Default.CheckCircle else icon, null, tint = if (granted) BrandTeal else SecondaryAccent) }
        Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(title, color = PrimaryText, fontWeight = FontWeight.Bold); Text(description, color = SecondaryText, fontSize = 11.sp) }
        Text(if (granted) "✓" else action, color = if (granted) BrandTeal else SecondaryAccent, fontSize = 12.sp, fontWeight = FontWeight.Black)
    }
}
