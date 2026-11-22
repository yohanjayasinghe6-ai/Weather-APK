package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.BrightCyan
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.OceanNavy
import com.example.ui.theme.RoyalBlue
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SunAmber
import kotlin.math.cos
import kotlin.math.sin

fun isRainyWmo(wmoCode: Int): Boolean = wmoCode in setOf(
    51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82, 85, 86, 95, 96, 99
)

fun isStormWmo(wmoCode: Int): Boolean = wmoCode in setOf(95, 96, 99)

fun heroGradientColors(wmoCode: Int, isDay: Boolean): List<Color> = when {
    !isDay -> listOf(Color(0xFF040F22), OceanNavy, Color(0xFF103766))
    isStormWmo(wmoCode) -> listOf(Color(0xFF071B36), Color(0xFF123563), Color(0xFF1D4E89))
    isRainyWmo(wmoCode) -> listOf(DeepNavy, RoyalBlue, Color(0xFF0E7490))
    wmoCode in setOf(2, 3, 45, 48) -> listOf(OceanNavy, Color(0xFF155E95), Color(0xFF1E88C7))
    else -> listOf(Color(0xFF0A3977), SkyBlue, ElectricCyan)
}

@Composable
fun WeatherConditionGraphic(
    wmoCode: Int,
    isDay: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    animate: Boolean = true
) {
    val transition = rememberInfiniteTransition(label = "weather_icon")
    val pulse by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse"
    )
    val phase = if (animate) pulse else 0.25f

    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        when {
            wmoCode == 0 || wmoCode == 1 -> {
                if (isDay) {
                    drawSun(Offset(w * 0.5f, h * 0.5f), w * 0.27f, phase)
                } else {
                    drawMoon(Offset(w * 0.52f, h * 0.5f), w * 0.28f)
                }
            }
            wmoCode == 2 -> {
                if (isDay) {
                    drawSun(Offset(w * 0.65f, h * 0.34f), w * 0.20f, phase)
                } else {
                    drawMoon(Offset(w * 0.68f, h * 0.34f), w * 0.19f)
                }
                drawCloud(Offset(w * 0.45f, h * 0.62f), w * 0.62f, Color.White, Color(0xFFD8EEFF))
            }
            wmoCode == 3 -> {
                drawCloud(Offset(w * 0.56f, h * 0.43f), w * 0.52f, Color(0xFFD2E5F8), Color(0xFF9AC0E4))
                drawCloud(Offset(w * 0.44f, h * 0.60f), w * 0.64f, Color.White, Color(0xFFCDE6FC))
            }
            wmoCode == 45 || wmoCode == 48 -> {
                drawCloud(Offset(w * 0.50f, h * 0.45f), w * 0.62f, Color(0xFFE5F2FE), Color(0xFFBBD8F5))
                val mistColor = Color(0xFFB3E5FC)
                for (i in 0..2) {
                    val y = h * (0.72f + i * 0.10f)
                    drawLine(
                        color = mistColor,
                        start = Offset(w * (0.20f + i * 0.06f), y),
                        end = Offset(w * (0.80f - i * 0.04f), y),
                        strokeWidth = w * 0.055f,
                        cap = StrokeCap.Round
                    )
                }
            }
            wmoCode in setOf(51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82, 85, 86) -> {
                val heavy = wmoCode in setOf(55, 63, 65, 81, 82, 85, 86)
                drawCloud(
                    Offset(w * 0.50f, h * 0.42f),
                    w * 0.66f,
                    if (heavy) Color(0xFFD5E8FA) else Color.White,
                    if (heavy) Color(0xFF8DBAE2) else Color(0xFFBFE0FC)
                )
                drawRainDrops(w, h, if (heavy) 4 else 3, phase)
            }
            wmoCode in setOf(95, 96, 99) -> {
                drawCloud(Offset(w * 0.50f, h * 0.39f), w * 0.68f, Color(0xFFC2DCF5), Color(0xFF6B97C4))
                drawLightningBolt(w, h)
                drawRainDrops(w, h, 2, phase)
            }
            else -> {
                drawSun(Offset(w * 0.62f, h * 0.36f), w * 0.18f, phase)
                drawCloud(Offset(w * 0.46f, h * 0.60f), w * 0.60f, Color.White, Color(0xFFCDE6FC))
            }
        }
    }
}

private fun DrawScope.drawSun(center: Offset, radius: Float, phase: Float) {
    val rayCount = 8
    val rayOuter = radius * 1.55f
    val rayInner = radius * 1.25f
    rotate(degrees = phase * 45f, pivot = center) {
        for (i in 0 until rayCount) {
            val angle = Math.toRadians((i * 360.0) / rayCount)
            val start = Offset(
                center.x + (rayInner * cos(angle)).toFloat(),
                center.y + (rayInner * sin(angle)).toFloat()
            )
            val end = Offset(
                center.x + (rayOuter * cos(angle)).toFloat(),
                center.y + (rayOuter * sin(angle)).toFloat()
            )
            drawLine(
                color = SunAmber.copy(alpha = 0.9f),
                start = start,
                end = end,
                strokeWidth = radius * 0.22f,
                cap = StrokeCap.Round
            )
        }
    }
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFDE047), SunAmber),
            center = center,
            radius = radius * 1.1f
        ),
        radius = radius,
        center = center
    )
}

private fun DrawScope.drawMoon(center: Offset, radius: Float) {
    drawCircle(
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFFFEF08A), Color(0xFFFDE047)),
            start = center - Offset(radius, radius),
            end = center + Offset(radius, radius)
        ),
        radius = radius,
        center = center
    )
    drawCircle(
        color = Color(0xFF0A2E5C),
        radius = radius * 0.78f,
        center = Offset(center.x + radius * 0.45f, center.y - radius * 0.22f)
    )
}

private fun DrawScope.drawCloud(
    center: Offset,
    width: Float,
    topColor: Color,
    bottomColor: Color
) {
    val h = width * 0.46f
    val left = center.x - width * 0.5f
    val baseY = center.y + h * 0.45f

    val brush = Brush.verticalGradient(
        colors = listOf(topColor, bottomColor),
        startY = center.y - h * 0.6f,
        endY = baseY
    )

    drawRoundRect(
        brush = brush,
        topLeft = Offset(left, center.y - h * 0.12f),
        size = Size(width, h * 0.62f),
        cornerRadius = CornerRadius(h * 0.35f, h * 0.35f)
    )
    drawCircle(
        brush = brush,
        radius = width * 0.25f,
        center = Offset(center.x - width * 0.12f, center.y - h * 0.24f)
    )
    drawCircle(
        brush = brush,
        radius = width * 0.19f,
        center = Offset(center.x + width * 0.18f, center.y - h * 0.12f)
    )
}

private fun DrawScope.drawRainDrops(w: Float, h: Float, count: Int, phase: Float) {
    val dropOffsets = when (count) {
        4 -> listOf(0.28f, 0.43f, 0.58f, 0.73f)
        3 -> listOf(0.32f, 0.50f, 0.68f)
        else -> listOf(0.32f, 0.68f)
    }
    dropOffsets.forEachIndexed { idx, xRatio ->
        val shift = ((phase + idx * 0.25f) % 1f) * (h * 0.09f)
        val x = w * xRatio
        val yStart = h * 0.66f + shift
        val yEnd = yStart + h * 0.13f
        drawLine(
            color = BrightCyan,
            start = Offset(x, yStart),
            end = Offset(x - w * 0.04f, yEnd),
            strokeWidth = w * 0.06f,
            cap = StrokeCap.Round
        )
    }
}

private fun DrawScope.drawLightningBolt(w: Float, h: Float) {
    val path = Path().apply {
        moveTo(w * 0.54f, h * 0.56f)
        lineTo(w * 0.42f, h * 0.74f)
        lineTo(w * 0.52f, h * 0.74f)
        lineTo(w * 0.46f, h * 0.92f)
        lineTo(w * 0.62f, h * 0.69f)
        lineTo(w * 0.52f, h * 0.69f)
        close()
    }
    drawPath(path = path, color = SunAmber)
}
