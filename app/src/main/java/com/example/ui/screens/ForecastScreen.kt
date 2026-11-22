package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.localization.LocalAppStrings
import com.example.model.DailyForecast
import com.example.model.HourlyForecast
import com.example.model.SriLankaLocation
import com.example.model.UserPreferences
import com.example.model.WeatherUiState
import com.example.ui.components.SectionTitleRow
import com.example.ui.components.WeatherConditionGraphic
import com.example.ui.components.WeatherSectionCard
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.RainCyan
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SunAmber
import com.example.util.WeatherFormatters

@Composable
fun ForecastScreen(
    selectedLocation: SriLankaLocation,
    weatherState: WeatherUiState,
    preferences: UserPreferences,
    onRefresh: () -> Unit
) {
    val strings = LocalAppStrings.current
    var expandedDayIso by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        when (weatherState) {
            is WeatherUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is WeatherUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(strings.errorLoadingWeather)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(onClick = onRefresh) { Text(strings.retry) }
                    }
                }
            }

            is WeatherUiState.Success -> {
                val bundle = weatherState.bundle
                val next24Hours = remember(bundle) {
                    val now = System.currentTimeMillis() - 3600_000L
                    val upcoming = bundle.hourly.filter { it.epochMillis >= now }
                    (if (upcoming.isNotEmpty()) upcoming else bundle.hourly).take(24)
                }
                val weekMin = remember(bundle) {
                    bundle.daily.minOfOrNull { it.tempMinC } ?: 20.0
                }
                val weekMax = remember(bundle) {
                    bundle.daily.maxOfOrNull { it.tempMaxC } ?: 35.0
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 760.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${selectedLocation.localizedName(preferences.languageCode)} • ${strings.navForecast}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = strings.weeklyOutlook,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = strings.forecastPredictionBadge,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    // Forecast prediction transparency notice
                    item {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = strings.forecastDisclaimer,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // 24-Hour Temperature & Rain Sparkline Card
                    item {
                        WeatherSectionCard {
                            SectionTitleRow(
                                icon = Icons.Filled.Schedule,
                                title = "${strings.hourlyForecast} (${strings.next24Hours})"
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            HourlyTrendChart(
                                hours = next24Hours,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(next24Hours, key = { "fc_${it.timeIso}" }) { hour ->
                                    ForecastHourTile(hour = hour, preferences = preferences)
                                }
                            }
                        }
                    }

                    // 7-Day Forecast Card
                    item {
                        WeatherSectionCard {
                            SectionTitleRow(
                                icon = Icons.Filled.CalendarMonth,
                                title = strings.sevenDayForecast
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            bundle.daily.forEachIndexed { idx, day ->
                                val isExpanded = expandedDayIso == day.dateIso
                                DailyForecastRow(
                                    index = idx,
                                    day = day,
                                    weekMin = weekMin,
                                    weekMax = weekMax,
                                    isExpanded = isExpanded,
                                    preferences = preferences,
                                    onClick = {
                                        expandedDayIso = if (isExpanded) null else day.dateIso
                                    }
                                )
                                if (idx < bundle.daily.lastIndex) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HourlyTrendChart(
    hours: List<HourlyForecast>,
    modifier: Modifier = Modifier
) {
    val tempLineColor = SunAmber
    val rainBarColor = RainCyan.copy(alpha = 0.38f)
    val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)

    Canvas(modifier = modifier) {
        if (hours.size < 2) return@Canvas
        val w = size.width
        val h = size.height
        val padTop = 14.dp.toPx()
        val padBottom = 10.dp.toPx()
        val chartH = h - padTop - padBottom

        // Reference line
        drawLine(
            color = gridColor,
            start = Offset(0f, h - padBottom),
            end = Offset(w, h - padBottom),
            strokeWidth = 1.dp.toPx()
        )

        val minT = hours.minOf { it.temperatureC }
        val maxT = (hours.maxOf { it.temperatureC }).coerceAtLeast(minT + 1.0)
        val stepX = w / (hours.size - 1).coerceAtLeast(1)
        val barWidth = (stepX * 0.52f).coerceAtLeast(4.dp.toPx())

        // Rain probability bars
        hours.forEachIndexed { idx, hr ->
            val x = idx * stepX
            val barH = (hr.rainProbabilityPercent / 100f) * chartH * 0.7f
            if (barH > 1f) {
                drawRoundRect(
                    color = rainBarColor,
                    topLeft = Offset((x - barWidth / 2f).coerceAtLeast(0f), h - padBottom - barH),
                    size = Size(barWidth, barH),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
            }
        }

        // Temperature curve
        val path = Path()
        hours.forEachIndexed { idx, hr ->
            val x = idx * stepX
            val norm = ((hr.temperatureC - minT) / (maxT - minT)).toFloat()
            val y = padTop + chartH * (1f - norm)
            if (idx == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(
            path = path,
            color = tempLineColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun ForecastHourTile(
    hour: HourlyForecast,
    preferences: UserPreferences
) {
    val isDayHour = hour.timeIso.substringAfter("T", "12:00").take(2).toIntOrNull()?.let {
        it in 6..18
    } ?: true

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .width(84.dp)
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = WeatherFormatters.formatHourLabel(hour.timeIso, preferences.timeFormat),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            WeatherConditionGraphic(
                wmoCode = hour.weatherCode,
                isDay = isDayHour,
                size = 28.dp,
                animate = false
            )
            Text(
                text = WeatherFormatters.formatTemperature(
                    hour.temperatureC,
                    preferences.temperatureUnit
                ),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${hour.rainProbabilityPercent}% • ${WeatherFormatters.formatRainAmount(hour.precipitationMm)}",
                style = MaterialTheme.typography.labelSmall,
                color = RainCyan,
                maxLines = 1
            )
            Text(
                text = WeatherFormatters.formatWindSpeed(hour.windSpeedKmh, preferences.windSpeedUnit),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun DailyForecastRow(
    index: Int,
    day: DailyForecast,
    weekMin: Double,
    weekMax: Double,
    isExpanded: Boolean,
    preferences: UserPreferences,
    onClick: () -> Unit
) {
    val strings = LocalAppStrings.current
    val (dayName, dateShort) = WeatherFormatters.formatDayLabel(
        day.dateIso,
        index,
        strings,
        preferences.languageCode
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp)
            .testTag("daily_row_$index")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.width(96.dp)) {
                Text(
                    text = dayName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = dateShort,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            WeatherConditionGraphic(
                wmoCode = day.weatherCode,
                isDay = true,
                size = 34.dp,
                animate = false
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.width(56.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.WaterDrop,
                        contentDescription = null,
                        tint = RainCyan,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "${day.rainProbabilityMaxPercent}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = RainCyan
                    )
                }
                Text(
                    text = WeatherFormatters.formatRainAmount(day.precipitationSumMm),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Min / Max bar
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = WeatherFormatters.formatTemperature(
                        day.tempMinC,
                        preferences.temperatureUnit,
                        includeUnit = false
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(6.dp))
                TemperatureRangeBar(
                    min = day.tempMinC,
                    max = day.tempMaxC,
                    weekMin = weekMin,
                    weekMax = weekMax,
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = WeatherFormatters.formatTemperature(
                        day.tempMaxC,
                        preferences.temperatureUnit,
                        includeUnit = false
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Icon(
                imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = strings.dayDetailsTitle,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }

        AnimatedVisibility(visible = isExpanded) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${strings.weatherDescription(day.weatherCode, true)} • ${strings.dayDetailsTitle}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DetailSmallItem(
                            strings.totalRain,
                            "${WeatherFormatters.formatRainAmount(day.precipitationSumMm)} (${
                                WeatherFormatters.rainIntensityLabel(day.precipitationSumMm, strings)
                            })"
                        )
                        DetailSmallItem(
                            strings.wind,
                            "${
                                WeatherFormatters.formatWindSpeed(
                                    day.windSpeedMaxKmh,
                                    preferences.windSpeedUnit
                                )
                            } ${strings.compassDirection(day.windDirectionDominantDeg)}"
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        DetailSmallItem(
                            strings.maxUv,
                            "${String.format("%.1f", day.uvIndexMax)} (${
                                WeatherFormatters.uvCategoryLabel(day.uvIndexMax, strings)
                            })"
                        )
                        DetailSmallItem(
                            "${strings.sunrise} / ${strings.sunset}",
                            "${WeatherFormatters.formatIsoTime(day.sunriseIso, preferences.timeFormat)} / ${
                                WeatherFormatters.formatIsoTime(day.sunsetIso, preferences.timeFormat)
                            }"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailSmallItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun TemperatureRangeBar(
    min: Double,
    max: Double,
    weekMin: Double,
    weekMax: Double,
    modifier: Modifier = Modifier
) {
    val span = (weekMax - weekMin).coerceAtLeast(1.0)
    val startFraction = ((min - weekMin) / span).toFloat().coerceIn(0f, 0.9f)
    val endFraction = ((max - weekMin) / span).toFloat().coerceIn(startFraction + 0.1f, 1f)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val left = size.width * startFraction
            val width = (size.width * (endFraction - startFraction)).coerceAtLeast(6.dp.toPx())
            drawRoundRect(
                brush = Brush.horizontalGradient(listOf(ElectricCyan, SkyBlue, SunAmber)),
                topLeft = Offset(left, 0f),
                size = Size(width, size.height),
                cornerRadius = CornerRadius(size.height / 2f, size.height / 2f)
            )
        }
    }
}
