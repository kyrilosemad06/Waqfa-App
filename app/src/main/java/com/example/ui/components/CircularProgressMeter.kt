package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderColor
import com.example.ui.theme.BrandTeal
import com.example.ui.theme.LimitReachedColor
import com.example.ui.theme.PrimaryAccent
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryAccent
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.WarningColor

@Composable
fun CircularProgressMeter(
    currentCount: Int,
    dailyLimit: Int,
    warningThresholdPercent: Int,
    modifier: Modifier = Modifier,
    remainingLabel: String = "remaining",
    consumedLabel: String = "consumed"
) {
    val progress = if (dailyLimit > 0) (currentCount.toFloat() / dailyLimit.toFloat()).coerceIn(0f, 1.25f) else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceAtMost(1f),
        animationSpec = tween(durationMillis = 600),
        label = "progress"
    )

    val isLimit = currentCount >= dailyLimit
    val isWarning = currentCount >= (dailyLimit * warningThresholdPercent) / 100

    val meterColor by animateColorAsState(
        targetValue = when {
            isLimit -> LimitReachedColor
            isWarning -> WarningColor
            progress < 0.5f -> BrandTeal
            else -> SecondaryAccent
        },
        animationSpec = tween(durationMillis = 350),
        label = "meter_color"
    )

    val remaining = (dailyLimit - currentCount).coerceAtLeast(0)
    val percentage = if (dailyLimit > 0) ((currentCount.toFloat() / dailyLimit.toFloat()) * 100).toInt() else 0

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(240.dp)
        ) {
            Canvas(modifier = Modifier.size(220.dp)) {
                val strokeWidth = 18.dp.toPx()
                val diameter = size.minDimension - strokeWidth
                val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)
                val arcSize = Size(diameter, diameter)

                // Background track
                drawArc(
                    color = BorderColor,
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Active glowing progress sweep
                val sweepAngle = 270f * animatedProgress
                if (sweepAngle > 0f) {
                    val gradient = Brush.sweepGradient(
                        0.0f to meterColor,
                        0.5f to if (isLimit) LimitReachedColor else SecondaryAccent,
                        1.0f to meterColor
                    )
                    drawArc(
                        brush = gradient,
                        startAngle = 135f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }
            }

            // Central numerical display
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$currentCount",
                        fontSize = 54.sp,
                        fontWeight = FontWeight.Black,
                        color = meterColor,
                        letterSpacing = (-1.5).sp
                    )
                    Text(
                        text = " / $dailyLimit",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryText,
                        modifier = Modifier.padding(bottom = 10.dp, start = 4.dp)
                    )
                }

                Text(
                    text = consumedLabel,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = SecondaryText
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Percentage badge
                Box(
                    modifier = Modifier
                        .background(
                            color = when {
                                isLimit -> LimitReachedColor.copy(alpha = 0.2f)
                                isWarning -> WarningColor.copy(alpha = 0.2f)
                                else -> SurfaceDark
                            },
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "$percentage%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = meterColor
                    )
                }
            }
        }

        // Remaining Quota Summary Pill
        Box(
            modifier = Modifier
                .background(
                    color = if (isLimit) LimitReachedColor.copy(alpha = 0.15f) else SurfaceDark,
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = if (isLimit) "0 $remainingLabel" else "$remaining $remainingLabel",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isLimit) LimitReachedColor else PrimaryText
            )
        }
    }
}
