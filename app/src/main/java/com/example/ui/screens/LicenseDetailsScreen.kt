package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.licensing.LicenseRepository
import com.example.ui.theme.BorderColor
import com.example.ui.theme.MainBackground
import com.example.ui.theme.PrimaryAccent
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryAccent
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SurfaceDark
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.ceil

@Composable
fun LicenseDetailsScreen(repository: LicenseRepository, onBack: () -> Unit) {
    val state by repository.state.collectAsState()
    val license = state.license ?: return
    val clipboard = LocalClipboardManager.current
    var request by remember { mutableStateOf("") }
    var copied by remember { mutableStateOf(false) }
    val now = System.currentTimeMillis()
    val days = ceil((license.expiresAtEpochMs - now).coerceAtLeast(0).toDouble() / 86_400_000.0).toInt()
    val date = SimpleDateFormat("dd MMMM yyyy", Locale("ar")).format(Date(license.expiresAtEpochMs))
    val title = if (days <= 14) "رخصتك تنتهي قريبًا" else "رخصتك مفعّلة"
    val hint = if (days <= 14) "اطلب التجديد الآن لضمان عدم توقف WAQFA." else "كل شيء جاهز. استخدم WAQFA بدون إنترنت."

    Column(Modifier.fillMaxSize().background(MainBackground).verticalScroll(rememberScrollState()).padding(18.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark, contentColor = SecondaryAccent), shape = RoundedCornerShape(14.dp)) { Icon(Icons.Default.ArrowBack, null) }
            Spacer(Modifier.width(12.dp)); Text("رخصتي", color = PrimaryText, fontSize = 23.sp, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.height(22.dp))
        Column(Modifier.fillMaxWidth().background(SurfaceDark, RoundedCornerShape(22.dp)).border(1.dp, BorderColor, RoundedCornerShape(22.dp)).padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Verified, null, tint = SecondaryAccent); Spacer(Modifier.width(9.dp)); Text(title, color = PrimaryText, fontSize = 19.sp, fontWeight = FontWeight.Black) }
            Text(hint, color = SecondaryText, fontSize = 13.sp, modifier = Modifier.padding(top = 8.dp))
            Spacer(Modifier.height(17.dp))
            DetailLine("تنتهي في", date)
            DetailLine("الأيام المتبقية", "$days يوم")
            DetailLine("رقم الرخصة", license.licenseId)
            DetailLine("نقل الجهاز", "${license.transferCount} من ${license.maxTransfers} مستخدمة")
        }
        Spacer(Modifier.height(18.dp))
        Column(Modifier.fillMaxWidth().background(SurfaceDark, RoundedCornerShape(22.dp)).border(1.dp, BorderColor, RoundedCornerShape(22.dp)).padding(20.dp)) {
            Text("تجديد أو نقل لجهاز جديد", color = PrimaryText, fontWeight = FontWeight.Black, fontSize = 18.sp)
            Text("اضغط لإخراج كود جاهز. أرسله للمالك؛ عند النقل تظل المدة المتبقية نفسها.", color = SecondaryText, fontSize = 12.sp, modifier = Modifier.padding(top = 7.dp, bottom = 12.dp))
            Button(onClick = { request = repository.recoveryRequest(license.licenseId); copied = false }, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(15.dp), colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent, contentColor = Color.White)) { Text(if (days <= 14) "إنشاء كود التجديد" else "إنشاء كود نقل الجهاز", fontWeight = FontWeight.Bold) }
            if (request.isNotBlank()) {
                Text(request, color = SecondaryAccent, fontSize = 12.sp, modifier = Modifier.fillMaxWidth().padding(top = 13.dp).background(Color(0xFF102F58), RoundedCornerShape(13.dp)).padding(13.dp))
                Spacer(Modifier.height(10.dp))
                Button(onClick = { clipboard.setText(AnnotatedString(request)); copied = true }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark, contentColor = SecondaryAccent), border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)) { Icon(Icons.Default.ContentCopy, null); Spacer(Modifier.width(8.dp)); Text(if (copied) "تم النسخ" else "نسخ الكود للمالك") }
            }
        }
        Spacer(Modifier.height(20.dp))
        Text("خصوصيتك محفوظة: نشاطك ورخصتك يتم التحقق منهما محليًا، ولا يحتاج التطبيق إلى خادم.", color = SecondaryText, fontSize = 12.sp)
    }
}

@Composable private fun DetailLine(label: String, value: String) = Row(Modifier.fillMaxWidth().padding(vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) { Text(label, color = SecondaryText, fontSize = 13.sp, modifier = Modifier.weight(1f)); Text(value, color = PrimaryText, fontSize = 14.sp, fontWeight = FontWeight.Bold) }
