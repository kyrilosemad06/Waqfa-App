package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TargetApp
import com.example.data.model.UserSettings
import com.example.ui.theme.BorderColor
import com.example.ui.theme.MainBackground
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryAccent
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated

@Composable
fun WaqfaTrackingScreen(settings: UserSettings, onToggleApp: (TargetApp) -> Unit) {
    val isAr = settings.language == "ar"
    val context = LocalContext.current
    val overlayGranted = Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context)
    Column(Modifier.fillMaxSize().background(MainBackground).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 22.dp)) {
        Text(if (isAr) "متابعة المقاطع" else "Video tracking", fontSize = 25.sp, fontWeight = FontWeight.Black, color = PrimaryText)
        Text(if (isAr) "فعّل الصلاحيات مرة واحدة، وWAQFA يعدّ تلقائيًا أثناء التمرير." else "Enable permissions once. WAQFA counts while you scroll.", color = SecondaryText, fontSize = 13.sp, modifier = Modifier.padding(top = 5.dp, bottom = 18.dp))

        PermissionCard(Icons.Default.Accessibility, if (isAr) "إمكانية الوصول" else "Accessibility", if (isAr) "تسمح بعدّ Reels وShorts محليًا على جهازك." else "Lets WAQFA count Reels and Shorts locally.", if (isAr) "فتح الإعدادات" else "Open settings") {
            context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
        Spacer(Modifier.height(10.dp))
        PermissionCard(Icons.Default.Layers, if (isAr) "العداد العائم" else "Floating counter", if (overlayGranted) (if (isAr) "مفعّل — سيظهر فوق التطبيقات." else "Enabled — visible above apps.") else (if (isAr) "فعّله لعرض عداد WAQFA أثناء المشاهدة." else "Enable it to see WAQFA while browsing."), if (overlayGranted) (if (isAr) "مفعّل" else "Enabled") else (if (isAr) "تفعيل" else "Enable")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) context.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
        Spacer(Modifier.height(24.dp))
        Text(if (isAr) "اختر المنصات" else "Choose platforms", color = PrimaryText, fontWeight = FontWeight.Bold, fontSize = 17.sp)
        Text(if (isAr) "كل منصة تعمل عند فتح قسم الفيديوهات القصيرة بها." else "Each platform counts in its short-video feed.", color = SecondaryText, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp, bottom = 10.dp))
        TargetApp.entries.forEach { app ->
            val enabled = app in settings.enabledApps
            Row(Modifier.fillMaxWidth().padding(vertical = 5.dp).clip(RoundedCornerShape(17.dp)).background(if (enabled) SurfaceElevated else SurfaceDark).border(1.dp, if (enabled) SecondaryAccent.copy(alpha = .5f) else BorderColor, RoundedCornerShape(17.dp)).clickable { onToggleApp(app) }.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(39.dp).clip(CircleShape).background(SecondaryAccent.copy(alpha = .14f)), contentAlignment = Alignment.Center) { Text(app.title.take(1), color = SecondaryAccent, fontSize = 18.sp, fontWeight = FontWeight.Black) }
                Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(app.title, color = PrimaryText, fontWeight = FontWeight.Bold); Text(app.shortFormatName, color = SecondaryText, fontSize = 12.sp) }
                Switch(checked = enabled, onCheckedChange = { onToggleApp(app) }, colors = SwitchDefaults.colors(checkedThumbColor = MainBackground, checkedTrackColor = SecondaryAccent, uncheckedThumbColor = SecondaryText, uncheckedTrackColor = SurfaceDark))
            }
        }
        Spacer(Modifier.height(112.dp))
    }
}

@Composable
private fun PermissionCard(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, description: String, action: String, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(SurfaceDark).border(1.dp, BorderColor, RoundedCornerShape(18.dp)).clickable(onClick = onClick).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(40.dp).clip(RoundedCornerShape(13.dp)).background(SecondaryAccent.copy(alpha = .14f)), contentAlignment = Alignment.Center) { Icon(icon, null, tint = SecondaryAccent) }
        Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(title, color = PrimaryText, fontWeight = FontWeight.Bold); Text(description, color = SecondaryText, fontSize = 11.sp) }
        Text(action, color = SecondaryAccent, fontSize = 12.sp, fontWeight = FontWeight.Black)
    }
}
