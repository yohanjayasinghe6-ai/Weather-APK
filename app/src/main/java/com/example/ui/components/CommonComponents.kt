package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.SunAmber
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WeatherSectionCard(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = containerColor,
        tonalElevation = 2.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            content = content
        )
    }
}

@Composable
fun SectionTitleRow(
    icon: ImageVector,
    title: String,
    trailing: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        trailing?.invoke()
    }
}

@Composable
fun MetricTile(
    icon: ImageVector,
    label: String,
    value: String,
    subtitle: String? = null,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun CachedDataBanner(text: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.8f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.CloudOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}

@Composable
fun DaylightArcCanvas(
    progress: Float,
    modifier: Modifier = Modifier
) {
    val trackColor = MaterialTheme.colorScheme.outlineVariant
    val activeColor = SunAmber
    val horizonColor = ElectricCyan.copy(alpha = 0.4f)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(74.dp)
    ) {
        val w = size.width
        val h = size.height
        val padH = 18.dp.toPx()
        val arcWidth = w - padH * 2
        val arcRadius = arcWidth / 2f
        val topY = h - 16.dp.toPx() - arcRadius * 0.7f
        val centerY = h - 16.dp.toPx()

        // Horizon line
        drawLine(
            color = horizonColor,
            start = Offset(padH - 8.dp.toPx(), centerY),
            end = Offset(w - padH + 8.dp.toPx(), centerY),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Dashed full arc
        drawArc(
            color = trackColor,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(padH, centerY - arcRadius * 0.75f),
            size = Size(arcWidth, arcRadius * 1.5f),
            style = Stroke(
                width = 3.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )
        )

        // Active sun progress arc
        val clamped = progress.coerceIn(0f, 1f)
        drawArc(
            color = activeColor,
            startAngle = 180f,
            sweepAngle = 180f * clamped,
            useCenter = false,
            topLeft = Offset(padH, centerY - arcRadius * 0.75f),
            size = Size(arcWidth, arcRadius * 1.5f),
            style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
        )

        // Sun dot position along ellipse
        val theta = PI * (1.0 - clamped)
        val sunX = (w / 2f) + (arcRadius * cos(theta)).toFloat()
        val sunY = centerY - ((arcRadius * 0.75f) * sin(theta)).toFloat()

        drawCircle(
            color = activeColor.copy(alpha = 0.25f),
            radius = 11.dp.toPx(),
            center = Offset(sunX, sunY)
        )
        drawCircle(
            color = activeColor,
            radius = 6.dp.toPx(),
            center = Offset(sunX, sunY)
        )
    }
}
