package com.example.ui.components

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DetectionConfidence
import com.example.data.model.TargetApp
import com.example.ui.theme.BorderColor
import com.example.ui.theme.FacebookColor
import com.example.ui.theme.InstagramColor
import com.example.ui.theme.PrimaryAccent
import com.example.ui.theme.PrimaryText
import com.example.ui.theme.SecondaryText
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TikTokColor
import com.example.ui.theme.WarningColor
import com.example.ui.theme.YouTubeColor

@Composable
fun AppBreakdownCard(
    app: TargetApp,
    count: Int,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    onSimulate: () -> Unit,
    onManualAdd: () -> Unit,
    onManualRemove: () -> Unit,
    isAr: Boolean = false,
    modifier: Modifier = Modifier
) {
    val platformColor = when (app) {
        TargetApp.INSTAGRAM -> InstagramColor
        TargetApp.YOUTUBE -> YouTubeColor
        TargetApp.TIKTOK -> TikTokColor
        TargetApp.FACEBOOK -> FacebookColor
    }

    val confidenceLabel = when (app.defaultConfidence) {
        DetectionConfidence.CONFIRMED -> if (isAr) "مؤكد" else "Confirmed"
        DetectionConfidence.HIGH_PROBABILITY -> if (isAr) "دقة عالية" else "High Prob."
        DetectionConfidence.HEURISTIC_ESTIMATE -> if (isAr) "تقدير ذكي" else "Heuristic"
    }

    val confidenceColor = when (app.defaultConfidence) {
        DetectionConfidence.CONFIRMED -> PrimaryAccent
        DetectionConfidence.HIGH_PROBABILITY -> SecondaryText
        DetectionConfidence.HEURISTIC_ESTIMATE -> WarningColor
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceDark)
            .border(1.dp, if (isEnabled) BorderColor else BorderColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .padding(16.dp)
            .testTag("app_card_${app.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Platform indicator dot
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(platformColor)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = app.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isEnabled) PrimaryText else SecondaryText
                    )
                    Text(
                        text = app.shortFormatName,
                        fontSize = 12.sp,
                        color = SecondaryText
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Confidence badge
                Box(
                    modifier = Modifier
                        .background(confidenceColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = confidenceLabel,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = confidenceColor
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Switch(
                    checked = isEnabled,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = PrimaryAccent,
                        checkedTrackColor = SurfaceElevated,
                        uncheckedThumbColor = SecondaryText,
                        uncheckedTrackColor = SurfaceDark
                    ),
                    modifier = Modifier.testTag("toggle_${app.id}")
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Count display and Quick Actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(SurfaceElevated)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$count",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isEnabled) PrimaryText else SecondaryText
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isAr) "فيديو" else "videos",
                    fontSize = 12.sp,
                    color = SecondaryText
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Manual correction decrement
                IconButton(
                    onClick = onManualRemove,
                    enabled = isEnabled && count > 0,
                    modifier = Modifier.size(36.dp).testTag("dec_${app.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Remove one",
                        tint = if (count > 0 && isEnabled) SecondaryText else BorderColor,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Manual correction increment
                IconButton(
                    onClick = onManualAdd,
                    enabled = isEnabled,
                    modifier = Modifier.size(36.dp).testTag("inc_${app.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add one",
                        tint = if (isEnabled) PrimaryAccent else BorderColor,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Simulate video view (+1 test event)
                IconButton(
                    onClick = onSimulate,
                    enabled = isEnabled,
                    modifier = Modifier
                        .size(36.dp)
                        .background(PrimaryAccent.copy(alpha = 0.15f), CircleShape)
                        .testTag("sim_${app.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Simulate detection",
                        tint = PrimaryAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
