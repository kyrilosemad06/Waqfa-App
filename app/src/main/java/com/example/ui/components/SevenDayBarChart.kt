package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyStats
import com.example.ui.theme.BorderColor
import com.example.ui.theme.LimitReachedColor
import com.example.ui.theme.PrimaryAccent
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated

@Composable
fun SevenDayBarChart(
    days: List<DailyStats>,
    dailyLimit: Int,
    isAr: Boolean = false,
    modifier: Modifier = Modifier
) {
    val maxCount = (days.maxOfOrNull { it.totalCount } ?: dailyLimit).coerceAtLeast(dailyLimit + 5)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isAr) "استهلاك آخر 7 أيام" else "7-Day Video History",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryText
            )
            Text(
                text = if (isAr) "الحد: $dailyLimit" else "Limit: $dailyLimit",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = PrimaryAccent
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Chart Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val bottomLabelSpace = 24.dp.toPx()
                val chartHeight = canvasHeight - bottomLabelSpace

                // Quota dashed line
                val quotaY = chartHeight - (dailyLimit.toFloat() / maxCount.toFloat()) * chartHeight
                drawLine(
                    color = PrimaryAccent.copy(alpha = 0.5f),
                    start = Offset(0f, quotaY),
                    end = Offset(canvasWidth, quotaY),
                    strokeWidth = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                )

                if (days.isNotEmpty()) {
                    val barWidth = 22.dp.toPx()
                    val totalBars = days.size
                    val spacing = (canvasWidth - (barWidth * totalBars)) / (totalBars + 1)

                    days.forEachIndexed { index, stat ->
                        val barHeight = (stat.totalCount.toFloat() / maxCount.toFloat()) * chartHeight
                        val left = spacing + index * (barWidth + spacing)
                        val top = chartHeight - barHeight

                        val barColor = if (stat.totalCount > dailyLimit) LimitReachedColor else PrimaryAccent

                        // Draw background track
                        drawRoundRect(
                            color = SurfaceElevated,
                            topLeft = Offset(left, 0f),
                            size = Size(barWidth, chartHeight),
                            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                        )

                        // Draw active bar
                        if (barHeight > 0) {
                            drawRoundRect(
                                color = barColor,
                                topLeft = Offset(left, top),
                                size = Size(barWidth, barHeight),
                                cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                            )
                        }
                    }
                }
            }
        }

        // Days labels row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            val displayDays = if (days.isNotEmpty()) days else List(7) { DailyStats("2026-10-0$it") }
            displayDays.forEach { stat ->
                val dayLabel = try {
                    val parts = stat.dateString.split("-")
                    if (parts.size == 3) "${parts[1]}/${parts[2]}" else stat.dateString
                } catch (_: Exception) {
                    stat.dateString
                }
                Text(
                    text = dayLabel,
                    fontSize = 10.sp,
                    color = SecondaryText,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
