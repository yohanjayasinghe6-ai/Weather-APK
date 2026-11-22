package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.outlined.Umbrella
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.localization.LocalAppStrings
import com.example.model.HourlyForecast
import com.example.model.SriLankaLocation
import com.example.model.UserPreferences
import com.example.model.WeatherBundle
import com.example.model.WeatherUiState
import com.example.ui.components.CachedDataBanner
import com.example.ui.components.DaylightArcCanvas
import com.example.ui.components.MetricTile
import com.example.ui.components.SectionTitleRow
import com.example.ui.components.WeatherConditionGraphic
import com.example.ui.components.WeatherSectionCard
import com.example.ui.components.heroGradientColors
import com.example.ui.components.isRainyWmo
import com.example.ui.theme.BrightCyan
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RainCyan
import com.example.ui.theme.SafeEmerald
import com.example.ui.theme.SunAmber
import com.example.util.WeatherFormatters

@Composable
fun HomeScreen(
    selectedLocation: SriLankaLocation,
    favoriteLocations: List<SriLankaLocation>,
    weatherState: WeatherUiState,
    isRefreshing: Boolean,
    comparisonBundles: Map<String, WeatherBundle>,
    preferences: UserPreferences,
    onOpenLocationPicker: () -> Unit,
    onToggleFavorite: (SriLankaLocation) -> Unit,
    onRefresh: () -> Unit,
    onSelectLocation: (SriLankaLocation) -> Unit,
    onNavigateToForecast: () -> Unit
) {
    val strings = LocalAppStrings.current
    val isFavorite = favoriteLocations.any { it.id == selectedLocation.id }

    when (weatherState) {
        is WeatherUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(24.dp)
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(44.dp))
                    Text(
                        text = strings.loadingWeather,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.clickable(onClick = onOpenLocationPicker)
                    ) {
                        Text(
                            text = selectedLocation.localizedName(preferences.languageCode),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }

        is WeatherUiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding(),
                contentAlignment = Alignment.Center
            ) {
                WeatherSectionCard(
                    modifier = Modifier
                        .padding(24.dp)
                        .widthIn(max = 460.dp)
                ) {
                    Text(
                        text = strings.errorLoadingWeather,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = weatherState.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = onRefresh,
                            modifier = Modifier.testTag("error_retry_button")
                        ) {
                            Text(strings.retry)
                        }
                        Button(onClick = onOpenLocationPicker) {
                            Text(strings.selectLocation)
                        }
                    }
                }
            }
        }

        is WeatherUiState.Success -> {
            val bundle = weatherState.bundle
            val current = bundle.current
            val today = bundle.daily.firstOrNull()
            val next24Hours = remember(bundle) {
                val now = System.currentTimeMillis() - 3600_000L
                val upcoming = bundle.hourly.filter { it.epochMillis >= now }
                (if (upcoming.isNotEmpty()) upcoming else bundle.hourly).take(24)
            }
            val monsoonProfile = remember(selectedLocation.monsoonZone) {
                WeatherFormatters.currentMonsoonProfile(selectedLocation.monsoonZone)
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 760.dp),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Atmospheric Hero Header
                    item {
                        HeroWeatherHeader(
                            location = selectedLocation,
                            bundle = bundle,
                            preferences = preferences,
                            isFavorite = isFavorite,
                            isRefreshing = isRefreshing,
                            onOpenLocationPicker = onOpenLocationPicker,
                            onToggleFavorite = { onToggleFavorite(selectedLocation) },
                            onRefresh = onRefresh
                        )
                    }

                    // Offline / Cached Notice
                    if (bundle.isFromCache) {
                        item {
                            val updatedTime = WeatherFormatters.formatEpochTime(
                                bundle.fetchedAtMillis,
                                preferences.timeFormat
                            )
                            CachedDataBanner(
                                text = String.format(strings.cachedDataBanner, updatedTime),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }

                    // 2. Hourly Forecast Strip (Next 24 Hours)
                    item {
                        WeatherSectionCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                            SectionTitleRow(
                                icon = Icons.Filled.Schedule,
                                title = "${strings.hourlyForecast} • ${strings.next24Hours}",
                                trailing = {
                                    Text(
                                        text = strings.navForecast,
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .clickable(onClick = onNavigateToForecast)
                                            .padding(4.dp)
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(next24Hours, key = { it.timeIso }) { hour ->
                                    HourlyChipCard(
                                        hour = hour,
                                        preferences = preferences
                                    )
                                }
                            }
                        }
                    }

                    // 3. Atmospheric Metrics Grid (Responsive 2 or 3 columns)
                    item {
                        val uvLabel = WeatherFormatters.uvCategoryLabel(current.uvIndex, strings)
                        val windCompass = strings.compassDirection(current.windDirectionDeg)

                        BoxWithConstraints(modifier = Modifier.padding(horizontal = 16.dp)) {
                            val columns = if (maxWidth > 540.dp) 3 else 2
                            val metrics = listOf(
                                Triple(
                                    Icons.Filled.WaterDrop,
                                    strings.humidity,
                                    "${current.humidityPercent}%" to null
                                ),
                                Triple(
                                    Icons.Filled.Air,
                                    strings.wind,
                                    WeatherFormatters.formatWindSpeed(
                                        current.windSpeedKmh,
                                        preferences.windSpeedUnit
                                    ) to "$windCompass (${current.windDirectionDeg}°)"
                                ),
                                Triple(
                                    Icons.Filled.Compress,
                                    strings.pressure,
                                    WeatherFormatters.formatPressure(current.pressureHpa) to null
                                ),
                                Triple(
                                    Icons.Filled.Visibility,
                                    strings.visibility,
                                    WeatherFormatters.formatVisibility(current.visibilityMeters) to null
                                ),
                                Triple(
                                    Icons.Filled.Cloud,
                                    strings.cloudCover,
                                    "${current.cloudCoverPercent}%" to null
                                ),
                                Triple(
                                    Icons.Filled.WbSunny,
                                    strings.uvIndex,
                                    String.format("%.1f", current.uvIndex) to uvLabel
                                )
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                metrics.chunked(columns).forEach { rowItems ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        rowItems.forEach { (icon, label, valPair) ->
                                            MetricTile(
                                                icon = icon,
                                                label = label,
                                                value = valPair.first,
                                                subtitle = valPair.second,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                        repeat(columns - rowItems.size) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 4. Sunrise & Sunset Card
                    item {
                        val riseStr = WeatherFormatters.formatIsoTime(
                            current.sunriseIso,
                            preferences.timeFormat
                        )
                        val setStr = WeatherFormatters.formatIsoTime(
                            current.sunsetIso,
                            preferences.timeFormat
                        )
                        val progress = WeatherFormatters.computeDaylightProgress(
                            current.sunriseIso,
                            current.sunsetIso
                        )

                        WeatherSectionCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                            SectionTitleRow(
                                icon = Icons.Filled.WbTwilight,
                                title = strings.daylightProgress
                            )
                            DaylightArcCanvas(progress = progress)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = strings.sunrise,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = riseStr,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = strings.sunset,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = setStr,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // 5. Sri Lanka Rainfall & Monsoon Card
                    item {
                        val rainProb = today?.rainProbabilityMaxPercent
                            ?: next24Hours.maxOfOrNull { it.rainProbabilityPercent } ?: 0
                        val rainSum = today?.rainSumMm ?: next24Hours.sumOf { it.precipitationMm }
                        val intensity = WeatherFormatters.rainIntensityLabel(
                            current.precipitationMm.coerceAtLeast(
                                next24Hours.maxOfOrNull { it.precipitationMm } ?: 0.0
                            ),
                            strings
                        )
                        val rainyPeriods = WeatherFormatters.computeExpectedRainyPeriods(
                            next24Hours,
                            preferences.timeFormat,
                            strings
                        )

                        WeatherSectionCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                            SectionTitleRow(
                                icon = Icons.Outlined.Umbrella,
                                title = strings.monsoonOutlook
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = strings.monsoonTitle(monsoonProfile.season),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = strings.monsoonDescription(monsoonProfile.season),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.88f)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = strings.rainProbability,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "$rainProb%",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = RainCyan
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = strings.expectedRainfall,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = WeatherFormatters.formatRainAmount(rainSum),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = strings.rainIntensity,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = intensity,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "${strings.rainyPeriods}: $rainyPeriods",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // 6. Air Quality Card
                    item {
                        val aqi = bundle.airQuality
                        WeatherSectionCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                            SectionTitleRow(
                                icon = Icons.Outlined.Eco,
                                title = strings.airQuality
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            if (aqi == null || (aqi.europeanAqi == null && aqi.usAqi == null)) {
                                Text(
                                    text = strings.airQualityUnavailable,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                val euVal = aqi.europeanAqi ?: 20
                                val label = WeatherFormatters.aqiCategoryLabel(aqi.europeanAqi, strings)
                                val badgeColor = when {
                                    euVal <= 20 -> SafeEmerald
                                    euVal <= 40 -> ElectricCyan
                                    euVal <= 60 -> SunAmber
                                    else -> MaterialTheme.colorScheme.error
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "${strings.euAqi}: ${aqi.europeanAqi ?: "--"}",
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${strings.usAqi}: ${aqi.usAqi ?: "--"}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = badgeColor.copy(alpha = 0.16f)
                                    ) {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = badgeColor,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                LinearProgressIndicator(
                                    progress = { (euVal / 100f).coerceIn(0.05f, 1f) },
                                    color = badgeColor,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    AqiPollutantPill(strings.pm25, aqi.pm25)
                                    AqiPollutantPill(strings.pm10, aqi.pm10)
                                    AqiPollutantPill(strings.ozone, aqi.ozone)
                                    AqiPollutantPill(strings.no2, aqi.no2)
                                }
                            }
                        }
                    }

                    // 7. Saved Locations Comparison Section
                    item {
                        WeatherSectionCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                            SectionTitleRow(
                                icon = Icons.Filled.CompareArrows,
                                title = strings.compareLocations,
                                trailing = {
                                    Text(
                                        text = strings.selectLocation.split(" ").first(),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .clickable(onClick = onOpenLocationPicker)
                                            .padding(4.dp)
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            if (favoriteLocations.isEmpty()) {
                                Text(
                                    text = strings.addLocationsToCompare,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                Text(
                                    text = strings.comparisonHint,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    items(favoriteLocations, key = { "cmp_${it.id}" }) { loc ->
                                        val cmpBundle = comparisonBundles[loc.id]
                                        ComparisonLocationCard(
                                            location = loc,
                                            bundle = cmpBundle,
                                            isSelected = loc.id == selectedLocation.id,
                                            preferences = preferences,
                                            onClick = { onSelectLocation(loc) }
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
}

@Composable
private fun HeroWeatherHeader(
    location: SriLankaLocation,
    bundle: WeatherBundle,
    preferences: UserPreferences,
    isFavorite: Boolean,
    isRefreshing: Boolean,
    onOpenLocationPicker: () -> Unit,
    onToggleFavorite: () -> Unit,
    onRefresh: () -> Unit
) {
    val strings = LocalAppStrings.current
    val current = bundle.current
    val today = bundle.daily.firstOrNull()
    val gradientColors = heroGradientColors(current.weatherCode, current.isDay)
    val bannerRes = if (isRainyWmo(current.weatherCode)) {
        R.drawable.img_hero_monsoon_rain
    } else {
        R.drawable.img_hero_tropical_sky
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(Brush.verticalGradient(gradientColors))
    ) {
        Image(
            painter = painterResource(id = bannerRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize()
                .alpha(0.24f)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            // Top Action Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = PureWhite.copy(alpha = 0.16f),
                    border = BorderStroke(1.dp, PureWhite.copy(alpha = 0.25f)),
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .clickable(onClick = onOpenLocationPicker)
                        .testTag("location_selector_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = strings.selectLocation,
                            tint = PureWhite,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = location.localizedName(preferences.languageCode),
                            style = MaterialTheme.typography.titleMedium,
                            color = PureWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Filled.ExpandMore,
                            contentDescription = null,
                            tint = PureWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clip(CircleShape)
                            .background(PureWhite.copy(alpha = 0.14f))
                            .testTag("home_favorite_button")
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = strings.favoriteLocations,
                            tint = if (isFavorite) SunAmber else PureWhite
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onRefresh,
                        enabled = !isRefreshing,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clip(CircleShape)
                            .background(PureWhite.copy(alpha = 0.14f))
                            .testTag("home_refresh_button")
                    ) {
                        if (isRefreshing) {
                            CircularProgressIndicator(
                                color = PureWhite,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = strings.refresh,
                                tint = PureWhite
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "${location.districtEn} • ${location.localizedProvince(preferences.languageCode)}",
                style = MaterialTheme.typography.bodyMedium,
                color = PureWhite.copy(alpha = 0.85f)
            )
            Text(
                text = WeatherFormatters.formatCurrentSriLankaDateTime(
                    preferences.timeFormat,
                    preferences.languageCode
                ),
                style = MaterialTheme.typography.bodySmall,
                color = PureWhite.copy(alpha = 0.72f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = WeatherFormatters.formatTemperature(
                            current.temperatureC,
                            preferences.temperatureUnit
                        ),
                        style = MaterialTheme.typography.displayLarge,
                        color = PureWhite
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = strings.weatherDescription(current.weatherCode, current.isDay),
                        style = MaterialTheme.typography.titleLarge,
                        color = PureWhite
                    )
                }

                WeatherConditionGraphic(
                    wmoCode = current.weatherCode,
                    isDay = current.isDay,
                    size = 92.dp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val feelsStr = WeatherFormatters.formatTemperature(
                    current.feelsLikeC,
                    preferences.temperatureUnit
                )
                val highStr = today?.let {
                    WeatherFormatters.formatTemperature(it.tempMaxC, preferences.temperatureUnit)
                } ?: "--"
                val lowStr = today?.let {
                    WeatherFormatters.formatTemperature(it.tempMinC, preferences.temperatureUnit)
                } ?: "--"

                Text(
                    text = "${strings.feelsLike} $feelsStr • ${String.format(strings.highLowFormat, highStr, lowStr)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PureWhite.copy(alpha = 0.9f)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            val updatedTime = WeatherFormatters.formatEpochTime(
                bundle.fetchedAtMillis,
                preferences.timeFormat
            )
            Text(
                text = "${String.format(strings.lastUpdated, updatedTime)} • ${strings.currentTimeZoneNote}",
                style = MaterialTheme.typography.labelSmall,
                color = PureWhite.copy(alpha = 0.68f)
            )
        }
    }
}

@Composable
private fun HourlyChipCard(
    hour: HourlyForecast,
    preferences: UserPreferences
) {
    val isDayHour = hour.timeIso.substringAfter("T", "12:00").take(2).toIntOrNull()?.let {
        it in 6..18
    } ?: true

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .width(86.dp)
                .padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = WeatherFormatters.formatHourLabel(hour.timeIso, preferences.timeFormat),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            WeatherConditionGraphic(
                wmoCode = hour.weatherCode,
                isDay = isDayHour,
                size = 32.dp,
                animate = false
            )
            Text(
                text = WeatherFormatters.formatTemperature(
                    hour.temperatureC,
                    preferences.temperatureUnit,
                    includeUnit = false
                ),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.WaterDrop,
                    contentDescription = null,
                    tint = RainCyan,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = "${hour.rainProbabilityPercent}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = RainCyan
                )
            }
            Text(
                text = WeatherFormatters.formatRainAmount(hour.precipitationMm),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
private fun AqiPollutantPill(label: String, value: Double?) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value?.let { String.format("%.1f", it) } ?: "--",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ComparisonLocationCard(
    location: SriLankaLocation,
    bundle: WeatherBundle?,
    isSelected: Boolean,
    preferences: UserPreferences,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = if (isSelected) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        },
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = Modifier
            .width(156.dp)
            .clickable(onClick = onClick)
            .testTag("comparison_card_${location.id}")
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = location.localizedName(preferences.languageCode),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (bundle != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = WeatherFormatters.formatTemperature(
                            bundle.current.temperatureC,
                            preferences.temperatureUnit
                        ),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    WeatherConditionGraphic(
                        wmoCode = bundle.current.weatherCode,
                        isDay = bundle.current.isDay,
                        size = 34.dp,
                        animate = false
                    )
                }
                val rainProb = bundle.daily.firstOrNull()?.rainProbabilityMaxPercent ?: 0
                Text(
                    text = "${rainProb}% • ${
                        WeatherFormatters.formatWindSpeed(
                            bundle.current.windSpeedKmh,
                            preferences.windSpeedUnit
                        )
                    }",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            } else {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            }
        }
    }
}
