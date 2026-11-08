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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun FocusPlanScreen(onComplete: (Int, Int, Int, Set<TargetApp>) -> Unit) {
    var baseline by remember { mutableIntStateOf(40) }
    var target by remember { mutableIntStateOf(20) }
    var strictness by remember { mutableIntStateOf(2) }
    var apps by remember { mutableStateOf(TargetApp.entries.toSet()) }
    val strictNames = listOf("مرن", "متوازن", "صارم", "صارم جدًا")
    val strictDescriptions = listOf(
        "تنبيهات فقط؛ القرار يبقى لك.",
        "تنبيه واضح واستثناء واحد يوميًا.",
        "حاجز فوري عند الوصول للحد دون استثناءات.",
        "حاجز كامل حتى موعد إعادة الضبط اليومي."
    )
    Column(Modifier.fillMaxSize().background(MainBackground).verticalScroll(rememberScrollState()).padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(20.dp))
        Box(Modifier.padding(16.dp).clip(RoundedCornerShape(22.dp)).background(SecondaryAccent.copy(alpha = .14f)), contentAlignment = Alignment.Center) { Icon(Icons.Default.Shield, null, tint = SecondaryAccent) }
        Spacer(Modifier.height(12.dp))
        Text("أنشئ خطة وقفة", color = PrimaryText, fontSize = 27.sp, fontWeight = FontWeight.Black)
        Text("حدّد هدفك ودرجة الحماية. WAQFA سيتدخل عندما تصل للحد.", color = SecondaryText, fontSize = 13.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp, bottom = 20.dp))

        PlanCard("كم تشاهد تقريبًا يوميًا؟", "$baseline مقطع") {
            Slider(baseline.toFloat(), { baseline = it.toInt().coerceAtLeast(target) }, valueRange = 10f..120f, steps = 21, colors = planSliderColors())
            Presets(listOf(20, 40, 60, 80), baseline) { baseline = it.coerceAtLeast(target) }
        }
        Spacer(Modifier.height(14.dp))
        PlanCard("إلى كم تريد التقليل؟", "$target مقطع يوميًا") {
            Slider(target.toFloat(), { target = it.toInt().coerceAtMost(baseline) }, valueRange = 5f..80f, steps = 14, colors = planSliderColors())
            Presets(listOf(10, 15, 20, 30), target) { target = it.coerceAtMost(baseline) }
        }
        Spacer(Modifier.height(14.dp))
        PlanCard("درجة الصرامة", strictNames[strictness]) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) { strictNames.forEachIndexed { i, item -> Box(Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(if (i == strictness) PrimaryAccent else SurfaceElevated).clickable { strictness = i }.padding(vertical = 9.dp), contentAlignment = Alignment.Center) { Text(item, color = if (i == strictness) MainBackground else PrimaryText, fontSize = 10.sp, fontWeight = FontWeight.Bold) } } }
            Text(strictDescriptions[strictness], color = SecondaryText, fontSize = 12.sp, modifier = Modifier.padding(top = 11.dp))
        }
        Spacer(Modifier.height(14.dp))
        PlanCard("المنصات التي ستتوقف عندها", "${apps.size} منصات") {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) { TargetApp.entries.forEach { app -> val selected = app in apps; Box(Modifier.weight(1f).clip(RoundedCornerShape(11.dp)).background(if(selected) SecondaryAccent.copy(.18f) else SurfaceElevated).border(1.dp, if(selected) SecondaryAccent else BorderColor, RoundedCornerShape(11.dp)).clickable { apps = apps.toMutableSet().apply { if (selected) remove(app) else add(app) } }.padding(vertical = 10.dp), contentAlignment = Alignment.Center) { Text(app.title.take(1), color = if(selected) SecondaryAccent else SecondaryText, fontWeight = FontWeight.Black) } } }
        }
        Spacer(Modifier.height(20.dp))
        Text("التزامي: أقلل من $baseline إلى $target Reel يوميًا.", color = PrimaryText, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Text("يمكن تغيير الخطة من الإعدادات، لكن الحاجز سيحميك عندما تحتاجه.", color = SecondaryText, fontSize = 11.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 5.dp, bottom = 15.dp))
        Button(onClick = { onComplete(baseline, target, strictness, apps) }, enabled = apps.isNotEmpty(), modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(17.dp), colors = ButtonDefaults.buttonColors(containerColor = SecondaryAccent, contentColor = MainBackground)) { Text("ابدأ خطة وقفة", fontWeight = FontWeight.Black, fontSize = 17.sp) }
        Spacer(Modifier.height(18.dp))
    }
}

@Composable private fun PlanCard(title: String, value: String, body: @Composable () -> Unit) = Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(19.dp)).background(SurfaceDark).border(1.dp, BorderColor, RoundedCornerShape(19.dp)).padding(16.dp)) { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(title, color = PrimaryText, fontWeight = FontWeight.Bold); Text(value, color = SecondaryAccent, fontWeight = FontWeight.Black) }; Spacer(Modifier.height(10.dp)); body() }
@Composable private fun Presets(items: List<Int>, selected: Int, onSelect: (Int) -> Unit) = Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { items.forEach { item -> Box(Modifier.clip(RoundedCornerShape(9.dp)).background(if(item == selected) PrimaryAccent else SurfaceElevated).clickable { onSelect(item) }.padding(horizontal = 11.dp, vertical = 6.dp)) { Text("$item", color = if(item == selected) MainBackground else PrimaryText, fontSize = 11.sp, fontWeight = FontWeight.Bold) } } }
@Composable private fun planSliderColors() = SliderDefaults.colors(thumbColor = SecondaryAccent, activeTrackColor = SecondaryAccent, inactiveTrackColor = SurfaceElevated)
