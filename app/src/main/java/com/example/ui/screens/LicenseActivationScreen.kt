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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.licensing.LicenseRepository
import com.example.licensing.LicenseStatus

private val ActivationBackground = Color(0xFFF4F8FF)
private val ActivationCard = Color.White
private val ActivationInk = Color(0xFF102244)
private val ActivationMuted = Color(0xFF71809A)
private val ActivationBorder = Color(0xFFDDE7F4)
private val ActivationBlue = Color(0xFF004BB7)
private val ActivationSky = Color(0xFF00BFFF)

@Composable
fun LicenseActivationScreen(repository: LicenseRepository) {
    var activationCode by remember { mutableStateOf("") }
    val recoveryCode = remember { repository.preActivationRequest() }
    var copied by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf<LicenseStatus?>(null) }
    val clipboard = LocalClipboardManager.current
    Column(
        Modifier.fillMaxSize().background(ActivationBackground).verticalScroll(rememberScrollState()).padding(horizontal = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(28.dp))
        Box(Modifier.height(174.dp).width(220.dp), contentAlignment = Alignment.Center) {
            androidx.compose.foundation.Image(painterResource(R.drawable.waqfa_activation_logo), contentDescription = "WAQFA", modifier = Modifier.fillMaxSize())
        }
        Spacer(Modifier.height(10.dp))
        Box(Modifier.clip(RoundedCornerShape(14.dp)).background(Color.White).border(1.dp, ActivationBorder, RoundedCornerShape(14.dp)).padding(horizontal = 24.dp, vertical = 9.dp)) {
            Text("تفعيل", color = ActivationBlue, fontSize = 22.sp, fontWeight = FontWeight.Black)
        }
        Text("الكود أدناه تم إنشاؤه لهذا الهاتف فقط. انسخه وأرسله للمالك، ثم الصق كود التفعيل الذي يصلك.", color = ActivationMuted, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp, bottom = 22.dp))
        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(25.dp)).background(ActivationCard).border(1.dp, ActivationBorder, RoundedCornerShape(25.dp)).padding(20.dp)) {
            Text("كود التفعيل", color = ActivationInk, fontWeight = FontWeight.Black, fontSize = 19.sp)
            Text("الصق كود التفعيل الموقّع الذي وصلك من مالك التطبيق.", color = ActivationMuted, fontSize = 13.sp, modifier = Modifier.padding(top = 6.dp, bottom = 12.dp))
            OutlinedTextField(activationCode, { activationCode = it.trim() }, Modifier.fillMaxWidth().height(128.dp), label = { Text("الصق كود التفعيل هنا") }, colors = licenseFieldColors())
            Spacer(Modifier.height(14.dp))
            Button(onClick = { status = repository.activate(activationCode).status }, enabled = activationCode.isNotBlank(), modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = ActivationSky, contentColor = Color.White)) { Text("تفعيل الآن", fontWeight = FontWeight.Black, fontSize = 17.sp) }
        }
        if (status != null && status != LicenseStatus.ACTIVE) Text(statusMessage(status!!), color = Color(0xFFD45448), fontSize = 12.sp, modifier = Modifier.padding(top = 10.dp))
        Spacer(Modifier.height(18.dp))
        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(25.dp)).background(ActivationCard).border(1.dp, ActivationBorder, RoundedCornerShape(25.dp)).padding(20.dp)) {
            Text("طلب تفعيل الجهاز", color = ActivationInk, fontWeight = FontWeight.Black, fontSize = 19.sp)
            Text("الكود جاهز تلقائيًا ومربوط ببصمة التثبيت على هذا الهاتف؛ لا يمكن استخدامه لتفعيل هاتف آخر.", color = ActivationMuted, fontSize = 13.sp, modifier = Modifier.padding(top = 6.dp))
            Text(recoveryCode, color = ActivationBlue, fontSize = 12.sp, modifier = Modifier.fillMaxWidth().padding(top = 12.dp).clip(RoundedCornerShape(15.dp)).background(Color(0xFFE9F2FF)).padding(14.dp))
            Spacer(Modifier.height(10.dp))
            Button(onClick = { clipboard.setText(AnnotatedString(recoveryCode)); copied = true }, modifier = Modifier.fillMaxWidth().height(46.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = if (copied) Color(0xFF00A878) else Color.White, contentColor = if (copied) Color.White else ActivationBlue), border = androidx.compose.foundation.BorderStroke(1.dp, if (copied) Color(0xFF00A878) else ActivationBorder)) {
                Icon(Icons.Default.ContentCopy, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                AnimatedContent(targetState = copied, transitionSpec = { fadeIn(tween(160)) togetherWith fadeOut(tween(120)) }, label = "copy_state") { wasCopied -> Text(if (wasCopied) "✓ تم النسخ" else "نسخ كود الجهاز", fontWeight = FontWeight.Bold) }
            }
            Text("انسخه ثم أرسله للمالك كما هو عبر واتساب.", color = ActivationMuted, fontSize = 11.sp, modifier = Modifier.padding(top = 7.dp))
        }
        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun licenseFieldColors() = OutlinedTextFieldDefaults.colors(focusedBorderColor = ActivationSky, unfocusedBorderColor = ActivationBorder, focusedTextColor = ActivationInk, unfocusedTextColor = ActivationInk, focusedLabelColor = ActivationBlue, unfocusedLabelColor = ActivationMuted, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)

private fun statusMessage(status: LicenseStatus) = when (status) {
    LicenseStatus.INVALID_SIGNATURE -> "كود التفعيل غير صالح أو تم تعديله."
    LicenseStatus.WRONG_DEVICE -> "هذه الرخصة تخص جهازًا آخر. استخدم الاستعادة."
    LicenseStatus.EXPIRED -> "انتهت الرخصة. اطلب تجديدًا موقعًا من المالك."
    LicenseStatus.CLOCK_TAMPERED -> "تم اكتشاف تغيير كبير في ساعة الجهاز. راجع المالك."
    LicenseStatus.OWNER_KEY_NOT_CONFIGURED -> "لم يتم إعداد مفتاح المالك العام داخل نسخة التطبيق بعد."
    else -> "تعذر التفعيل."
}
