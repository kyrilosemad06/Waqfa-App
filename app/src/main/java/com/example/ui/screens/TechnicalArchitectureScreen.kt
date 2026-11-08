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
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.PhoneIphone
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
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
fun TechnicalArchitectureScreen(settings: UserSettings) {
    val isAr = settings.language == "ar"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("tech_plan_screen")
    ) {
        Text(
            text = StringsRepo.get("nav_architecture", settings.language),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryText
        )
        Text(
            text = if (isAr) "التصميم الهندسي لمنصة أندرويد الأصلية وخارطة طريق نظام iOS." else "Comprehensive Android Native Architecture & iOS Feasibility Roadmap.",
            fontSize = 13.sp,
            color = SecondaryText,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        // Android Native Architecture Overview
        TechSectionCard(
            title = if (isAr) "1. البنية الهندسية لنظام أندرويد (Android Architecture)" else "1. Android Native Production Architecture",
            icon = Icons.Default.Android,
            accent = PrimaryAccent
        ) {
            Text(
                text = if (isAr)
                    "• واجهة المستخدم: مبنية بالكامل باستخدام Jetpack Compose و Material 3 مع دعم RTL و LTR الفوري.\n" +
                            "• قاعدة البيانات المحلية: Room Database (مشفرة ومحلية 100%) تشمل جداول DailyStatsEntity و DetectionEventEntity.\n" +
                            "• محرك الرصد (CounterEngine): خوارزمية ثلاثية المستوى (Temporal Debounce 2.5s + 30-min Hash Cache + Dwell Threshold 2.0s).\n" +
                            "• خدمة إمكانية الوصول (ReelLimitAccessibilityService): تستمع للأحداث من حزم: Instagram, YouTube, TikTok, Facebook فقط.\n" +
                            "• الخدمة العائمة (FloatingCounterService): Foreground Service مع WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY لعرض عداد حي فوق التطبيقات."
                else
                    "• UI Layer: 100% Jetpack Compose with Material 3 dynamic theming and native RTL/LTR switching.\n" +
                            "• Local Storage: Room Database holding DailyStatsEntity & DetectionEventEntity with zero external telemetry.\n" +
                            "• CounterEngine: 3-tier deduplication engine (2.5s rapid scroll debounce + 30-min hash cache + 2.0s dwell validation).\n" +
                            "• Accessibility Service: Declared specifically for target packages (Instagram, YouTube, TikTok, Facebook katana).\n" +
                            "• System Overlay: Foreground Service using WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY with touch-drag coordinates.",
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = SecondaryText
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Technical Honesty & Limitations
        TechSectionCard(
            title = if (isAr) "2. الصراحة التقنية والحدود الواقعية" else "2. Technical Honesty & Platform Realities",
            icon = Icons.Default.Policy,
            accent = WarningColor
        ) {
            Text(
                text = if (isAr)
                    "• إنستغرام ويوتيوب: يمكن قراءة حاويات ReelsViewer و ShortsPlayer بثقة عالية (High/Confirmed).\n" +
                            "• تيك توك: تشفر أسماء العقد البرمجية ديناميكياً (Obfuscated IDs)، لذلك يعتمد المحرك على أحداث التمرير الرأسي VerticalViewPager مع زمن استقرار لا يقل عن ثانيتين (Heuristic Dwell).\n" +
                            "• سياسات Google Play: تُستخدم AccessibilityService لأغراض الرفاهية الرقمية فقط، مع إفصاح صريح (Prominent Disclosure) وعدم قراءة محتوى الشاشة أو الرسائل نهائياً."
                else
                    "• Instagram & YouTube: Expose recognizable View IDs and transition signatures allowing High/Confirmed confidence ratings.\n" +
                            "• TikTok: Obfuscates all View IDs; therefore, detection relies on VerticalViewPager scroll events coupled with minimum dwell heuristics (Heuristic Estimate).\n" +
                            "• Google Play Compliance: AccessibilityService is utilized strictly for digital wellbeing. No screenshot capture, no password harvesting, and zero remote transmission.",
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = SecondaryText
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // iOS Feasibility Roadmap
        TechSectionCard(
            title = if (isAr) "3. خارطة طريق نظام iOS (Apple Screen Time Roadmap)" else "3. iOS Feasibility Roadmap",
            icon = Icons.Default.PhoneIphone,
            accent = SecondaryAccent
        ) {
            Text(
                text = if (isAr)
                    "• قيود نظام iOS: لا يسمح نظام iOS بأي شريط عائم فوق التطبيقات الأخرى (System Overlays Forbidden).\n" +
                            "• واجهات Apple الرسمية: تعتمد النسخة المستقبلية على حزمة Screen Time Frameworks الرسمية:\n" +
                            "  1) FamilyControls: لطلب إذن حظر التطبيقات من المستخدم.\n" +
                            "  2) DeviceActivity: لجدولة ومراقبة وقت استخدام تطبيقات الفيديو القصيرة.\n" +
                            "  3) ManagedSettings: لتطبيق الحظر الصارم (Shield Configuration) فور نفاد الوقت.\n" +
                            "• البديل العملي على iOS: مراقبة مدة استخدام الحزم بدلاً من عدّ الفيديوهات الفردية، مع إرسال إشعارات وتطبيق درع الحظر الشامل (Shield UI)."
                else
                    "• System Overlay Constraint: iOS strictly prohibits drawing UI over third-party applications (no floating pill equivalent exists).\n" +
                            "• Official Apple APIs: An iOS companion would utilize Apple's Screen Time frameworks:\n" +
                            "  1) FamilyControls: Authorizes monitoring of restricted application bundles.\n" +
                            "  2) DeviceActivity: DeviceActivityMonitor extension monitors scheduled viewing budgets.\n" +
                            "  3) ManagedSettings: ManagedSettingsStore enforces application shields when budget expires.\n" +
                            "• UX Adaptation: On iOS, the app budgets session duration (e.g. 20 mins ~ 20 videos) and triggers a ShieldConfiguration extension to pause compulsive browsing.",
                fontSize = 12.sp,
                lineHeight = 18.sp,
                color = SecondaryText
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Feature Checklist
        TechSectionCard(
            title = if (isAr) "4. قائمة الإنجازات والجاهزية" else "4. Deliverables & Production Checklist",
            icon = Icons.Default.Check,
            accent = PrimaryAccent
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                ChecklistRow("Complete Jetpack Compose M3 Dark Aesthetic (#080B0A, #36E6B3)", true)
                ChecklistRow("Room Database schema (DailyStatsDao & DetectionEventDao)", true)
                ChecklistRow("3-Tier Deduplication CounterEngine (Debounce + Hash + Dwell)", true)
                ChecklistRow("Full Arabic RTL & English LTR seamless toggle", true)
                ChecklistRow("Interactive Floating Overlay Pill Simulator with drag gestures", true)
                ChecklistRow("Real Android Foreground Service & Overlay WindowManager", true)
                ChecklistRow("Real Android AccessibilityService for Instagram, Shorts, TikTok", true)
                ChecklistRow("Deliberate 10-Second Friction Delay Exception Flow", true)
                ChecklistRow("7-Day Canvas Bar Chart & Adherence Analytics", true)
                ChecklistRow("Unit & Robolectric Test Suite for Counter Engine & Deduplication", true)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun TechSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: androidx.compose.ui.graphics.Color,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = PrimaryText)
        }
        Spacer(modifier = Modifier.height(10.dp))
        content()
    }
}

@Composable
private fun ChecklistRow(label: String, isDone: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(PrimaryAccent.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = PrimaryAccent, modifier = Modifier.size(12.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, fontSize = 11.sp, color = PrimaryText, fontWeight = FontWeight.Medium)
    }
}
