package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import kotlin.math.roundToInt

@Composable
fun FloatingPillOverlayView(
    count: Int,
    limit: Int,
    warningThresholdPercent: Int,
    isAr: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isMinimized by remember { mutableStateOf(false) }

    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val isLimit = count >= limit
    val isWarning = count >= (limit * warningThresholdPercent) / 100

    val pillColor by animateColorAsState(
        targetValue = when {
            isLimit -> LimitReachedColor
            isWarning -> WarningColor
            else -> PrimaryAccent
        },
        label = "pill_color"
    )

    val pillBgColor by animateColorAsState(
        targetValue = when {
            isLimit -> Color(0xFF250E11)
            isWarning -> Color(0xFF261B0E)
            else -> SurfaceElevated
        },
        label = "pill_bg_color"
    )

    val hasOverlayPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        Settings.canDrawOverlays(context)
    } else {
        true
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceDark)
            .border(1.dp, BorderColor, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = if (isAr) "العداد العائم فوق الشاشة" else "Floating Counter Pill",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
                Text(
                    text = if (isAr) "اسحب العداد لتجربة موقعه على الشاشة" else "Drag to test position anywhere on screen",
                    fontSize = 12.sp,
                    color = SecondaryText
                )
            }

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(SurfaceElevated)
                    .clickable { isMinimized = !isMinimized }
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = if (isMinimized) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = "Toggle minimized",
                    tint = SecondaryText,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Interactive Simulation Stage
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MainBackground)
                .border(1.dp, BorderColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Clean preview backdrop
            Text(
                text = if (isAr) "معاينة موضع العداد العائم على شاشة الهاتف" else "Preview of floating counter on screen",
                fontSize = 12.sp,
                color = SecondaryText.copy(alpha = 0.5f),
                fontWeight = FontWeight.Medium
            )

            // The Draggable Floating Pill
            Box(
                modifier = Modifier
                    .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                        }
                    }
                    .shadow(12.dp, RoundedCornerShape(30.dp))
                    .clip(RoundedCornerShape(30.dp))
                    .background(pillBgColor)
                    .border(1.5.dp, pillColor, RoundedCornerShape(30.dp))
                    .clickable { isMinimized = !isMinimized }
                    .padding(horizontal = if (isMinimized) 12.dp else 16.dp, vertical = 8.dp)
                    .testTag("floating_preview_pill")
            ) {
                if (isMinimized) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(pillColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$count",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = pillColor
                        )
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "WAQFA",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SecondaryText,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(12.dp)
                                .background(BorderColor)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$count / $limit",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = pillColor
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Native System Overlay Permission status & activator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceElevated)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isAr) "ظهور العداد الحقيقي فوق التطبيقات" else "System Floating Overlay Status",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryText
                )
                Text(
                    text = if (hasOverlayPermission) {
                        if (isAr) "الصلاحية ممنوحة (يعمل العداد الحقيقي فوق التطبيقات)" else "Overlay permission granted & active"
                    } else {
                        if (isAr) "تتطلب إذناً لعرض العداد فوق إنستغرام ويوتيوب" else "Requires permission to draw over other apps"
                    },
                    fontSize = 11.sp,
                    color = if (hasOverlayPermission) PrimaryAccent else WarningColor
                )
            }

            if (!hasOverlayPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Button(
                    onClick = {
                        val intent = Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:${context.packageName}")
                        ).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryAccent),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = if (isAr) "تفعيل" else "Grant",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MainBackground
                    )
                }
            }
        }
    }
}
