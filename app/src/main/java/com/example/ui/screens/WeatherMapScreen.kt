package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.localization.LocalAppStrings
import com.example.model.DistrictWeatherSnapshot
import com.example.model.RadarData
import com.example.model.SriLankaLocation
import com.example.model.SriLankaLocations
import com.example.model.UserPreferences
import com.example.ui.components.WeatherConditionGraphic
import com.example.ui.components.WeatherSectionCard
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.OceanNavy
import com.example.ui.theme.PureWhite
import com.example.ui.theme.RainCyan
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SunAmber
import com.example.util.WeatherFormatters
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.tan

private enum class MapLayerMode {
    RADAR,
    TEMPERATURE,
    RAINFALL
}

// Web Mercator projection helpers for zoom level 7 covering Sri Lanka
private const val MAP_ZOOM = 7
private const val TILE_X_START = 92
private const val TILE_X_COUNT = 2 // tiles 92, 93
private const val TILE_Y_START = 60
private const val TILE_Y_COUNT = 3 // tiles 60, 61, 62

private fun lonToTileFractionX(lon: Double, zoom: Int): Double {
    return ((lon + 180.0) / 360.0) * 2.0.pow(zoom)
}

private fun latToTileFractionY(lat: Double, zoom: Int): Double {
    val rad = Math.toRadians(lat)
    return (1.0 - ln(tan(rad) + 1.0 / cos(rad)) / PI) / 2.0 * 2.0.pow(zoom)
}

@Composable
fun WeatherMapScreen(
    selectedLocation: SriLankaLocation,
    districtSnapshots: List<DistrictWeatherSnapshot>,
    isMapLoading: Boolean,
    radarData: RadarData?,
    preferences: UserPreferences,
    onRefreshMap: () -> Unit,
    onSelectLocationAndOpenHome: (SriLankaLocation) -> Unit
) {
    val strings = LocalAppStrings.current
    var layerMode by remember { mutableStateOf(MapLayerMode.RADAR) }
    var focusedDistrictId by remember(selectedLocation.id) { mutableStateOf(selectedLocation.id) }
    var isRadarPlaying by remember { mutableStateOf(false) }
    var radarFrameIdx by remember { mutableIntStateOf(0) }

    val frames = radarData?.frames.orEmpty()
    LaunchedEffect(frames.size) {
        if (frames.isNotEmpty()) {
            // Default to latest past radar frame
            radarFrameIdx = (frames.indexOfLast { !it.isNowcast }.takeIf { it >= 0 } ?: 0)
        }
    }

    LaunchedEffect(isRadarPlaying, frames.size) {
        if (isRadarPlaying && frames.isNotEmpty()) {
            while (true) {
                delay(750L)
                radarFrameIdx = (radarFrameIdx + 1) % frames.size
            }
        }
    }

    val activeFrame = frames.getOrNull(radarFrameIdx)
    val snapshotMap = remember(districtSnapshots) {
        districtSnapshots.associateBy { it.location.id }
    }
    val focusedLocation = remember(focusedDistrictId) {
        SriLankaLocations.findById(focusedDistrictId) ?: selectedLocation
    }
    val focusedSnapshot = snapshotMap[focusedLocation.id]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 760.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = strings.weatherMapTitle,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = strings.districtTapHint,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = onRefreshMap,
                        enabled = !isMapLoading,
                        modifier = Modifier.testTag("map_refresh_button")
                    ) {
                        if (isMapLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Filled.Refresh, contentDescription = strings.refresh)
                        }
                    }
                }
            }

            // Layer chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = layerMode == MapLayerMode.RADAR,
                        onClick = { layerMode = MapLayerMode.RADAR },
                        label = { Text(strings.layerRadar) },
                        modifier = Modifier.testTag("layer_chip_radar")
                    )
                    FilterChip(
                        selected = layerMode == MapLayerMode.TEMPERATURE,
                        onClick = { layerMode = MapLayerMode.TEMPERATURE },
                        label = { Text(strings.layerTemp) },
                        modifier = Modifier.testTag("layer_chip_temp")
                    )
                    FilterChip(
                        selected = layerMode == MapLayerMode.RAINFALL,
                        onClick = { layerMode = MapLayerMode.RAINFALL },
                        label = { Text(strings.layerRain) },
                        modifier = Modifier.testTag("layer_chip_rain")
                    )
                }
            }

            // Interactive Sri Lanka Map Container
            item {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    color = OceanNavy,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        BoxWithConstraints(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(TILE_X_COUNT.toFloat() / TILE_Y_COUNT.toFloat())
                                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(DeepNavy, OceanNavy, Color(0xFF0C3A6E))
                                    )
                                )
                        ) {
                            val mapWidthPx = with(LocalDensity.current) { maxWidth.toPx() }
                            val mapHeightPx = with(LocalDensity.current) { maxHeight.toPx() }
                            val context = LocalContext.current

                            // Fallback Stylized Sri Lanka Vector Outline (visible offline or while tiles load)
                            SriLankaVectorSilhouette(modifier = Modifier.fillMaxSize())

                            // OpenStreetMap Base Tile Grid (2x3 at zoom 7)
                            Column(modifier = Modifier.fillMaxSize()) {
                                for (ty in TILE_Y_START until TILE_Y_START + TILE_Y_COUNT) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .weight(1f)
                                    ) {
                                        for (tx in TILE_X_START until TILE_X_START + TILE_X_COUNT) {
                                            AsyncImage(
                                                model = ImageRequest.Builder(context)
                                                    .data("https://tile.openstreetmap.org/$MAP_ZOOM/$tx/$ty.png")
                                                    .addHeader("User-Agent", "LankaWeatherAndroid/1.0")
                                                    .crossfade(true)
                                                    .build(),
                                                contentDescription = null,
                                                contentScale = ContentScale.FillBounds,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .fillMaxSize()
                                                    .alpha(0.75f)
                                            )
                                        }
                                    }
                                }
                            }

                            // RainViewer Live Precipitation Radar Overlay
                            if (layerMode == MapLayerMode.RADAR && radarData != null && activeFrame != null) {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    for (ty in TILE_Y_START until TILE_Y_START + TILE_Y_COUNT) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f)
                                        ) {
                                            for (tx in TILE_X_START until TILE_X_START + TILE_X_COUNT) {
                                                AsyncImage(
                                                    model = ImageRequest.Builder(context)
                                                        .data("${radarData.host}${activeFrame.path}/256/$MAP_ZOOM/$tx/$ty/2/1_1.png")
                                                        .crossfade(false)
                                                        .build(),
                                                    contentDescription = null,
                                                    contentScale = ContentScale.FillBounds,
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .fillMaxSize()
                                                        .alpha(0.78f)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // Plot all 25 District Markers accurately by Web Mercator coordinates
                            SriLankaLocations.allDistricts.forEach { district ->
                                val fx = (lonToTileFractionX(district.longitude, MAP_ZOOM) - TILE_X_START) / TILE_X_COUNT
                                val fy = (latToTileFractionY(district.latitude, MAP_ZOOM) - TILE_Y_START) / TILE_Y_COUNT
                                if (fx in 0.0..1.0 && fy in 0.0..1.0) {
                                    val snap = snapshotMap[district.id]
                                    val isFocused = district.id == focusedDistrictId
                                    val markerLabel = when {
                                        snap == null -> district.nameEn.take(3)
                                        layerMode == MapLayerMode.RAINFALL -> "${snap.rainProbabilityMax}%"
                                        else -> WeatherFormatters.formatTemperature(
                                            snap.temperatureC,
                                            preferences.temperatureUnit,
                                            includeUnit = false
                                        )
                                    }
                                    val badgeBg = when {
                                        isFocused -> SunAmber
                                        layerMode == MapLayerMode.RAINFALL && (snap?.rainProbabilityMax ?: 0) >= 50 ->
                                            RainCyan
                                        else -> DeepNavy.copy(alpha = 0.88f)
                                    }
                                    val badgeFg = if (isFocused) DeepNavy else PureWhite

                                    Surface(
                                        shape = CircleShape,
                                        color = badgeBg,
                                        border = BorderStroke(
                                            width = if (isFocused) 2.dp else 1.dp,
                                            color = PureWhite.copy(alpha = 0.85f)
                                        ),
                                        shadowElevation = if (isFocused) 6.dp else 2.dp,
                                        modifier = Modifier
                                            .offset {
                                                IntOffset(
                                                    x = (fx * mapWidthPx).roundToInt() - 22,
                                                    y = (fy * mapHeightPx).roundToInt() - 16
                                                )
                                            }
                                            .clickable { focusedDistrictId = district.id }
                                            .testTag("map_marker_${district.id}")
                                    ) {
                                        Text(
                                            text = markerLabel,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = badgeFg,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Radar Playback Bar
                        if (layerMode == MapLayerMode.RADAR) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(DeepNavy)
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                if (activeFrame != null) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = CircleShape,
                                            color = ElectricCyan,
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clickable { isRadarPlaying = !isRadarPlaying }
                                                .testTag("radar_play_pause")
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = if (isRadarPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                                    contentDescription = if (isRadarPlaying) strings.pauseRadar else strings.playRadar,
                                                    tint = DeepNavy,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = if (isRadarPlaying) strings.pauseRadar else strings.playRadar,
                                                style = MaterialTheme.typography.labelLarge,
                                                color = PureWhite
                                            )
                                            Text(
                                                text = WeatherFormatters.formatEpochTime(
                                                    activeFrame.timeEpochSec * 1000L,
                                                    preferences.timeFormat
                                                ),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = ElectricCyan
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${radarFrameIdx + 1}/${frames.size}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = PureWhite.copy(alpha = 0.75f)
                                    )
                                } else {
                                    Text(
                                        text = strings.radarUnavailable,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = PureWhite.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Focused District Summary Card
            item {
                WeatherSectionCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = focusedLocation.localizedName(preferences.languageCode),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = focusedLocation.localizedProvince(preferences.languageCode),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (focusedSnapshot != null) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = strings.weatherDescription(
                                        focusedSnapshot.weatherCode,
                                        focusedSnapshot.isDay
                                    ),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        if (focusedSnapshot != null) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                WeatherConditionGraphic(
                                    wmoCode = focusedSnapshot.weatherCode,
                                    isDay = focusedSnapshot.isDay,
                                    size = 44.dp,
                                    animate = false
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = WeatherFormatters.formatTemperature(
                                        focusedSnapshot.temperatureC,
                                        preferences.temperatureUnit
                                    ),
                                    style = MaterialTheme.typography.displayMedium
                                )
                            }
                        }
                    }
                    if (focusedSnapshot != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${strings.humidity}: ${focusedSnapshot.humidityPercent}%",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${strings.rainProbability}: ${focusedSnapshot.rainProbabilityMax}%",
                                style = MaterialTheme.typography.bodySmall,
                                color = RainCyan
                            )
                            Text(
                                text = "${strings.wind}: ${
                                    WeatherFormatters.formatWindSpeed(
                                        focusedSnapshot.windSpeedKmh,
                                        preferences.windSpeedUnit
                                    )
                                }",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { onSelectLocationAndOpenHome(focusedLocation) },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("map_view_dashboard_button")
                    ) {
                        Text(strings.viewDashboard)
                    }
                }
            }

            item {
                Text(
                    text = "${strings.radarLiveNote} • © OpenStreetMap contributors",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SriLankaVectorSilhouette(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        fun toScreen(lat: Double, lon: Double): Offset {
            val fx = ((lonToTileFractionX(lon, MAP_ZOOM) - TILE_X_START) / TILE_X_COUNT).toFloat()
            val fy = ((latToTileFractionY(lat, MAP_ZOOM) - TILE_Y_START) / TILE_Y_COUNT).toFloat()
            return Offset(fx * w, fy * h)
        }

        // Approximate Sri Lanka island polygon for instant vector backdrop
        val coastline = listOf(
            9.82 to 80.22, 9.65 to 79.95, 9.35 to 80.02, 8.98 to 79.90,
            8.45 to 79.84, 7.95 to 79.81, 7.35 to 79.83, 6.92 to 79.84,
            6.40 to 79.98, 6.03 to 80.22, 5.92 to 80.56, 6.13 to 81.12,
            6.55 to 81.68, 7.20 to 81.86, 7.73 to 81.70, 8.58 to 81.23,
            9.28 to 80.82, 9.66 to 80.42
        )
        val path = Path()
        coastline.forEachIndexed { idx, (lat, lon) ->
            val p = toScreen(lat, lon)
            if (idx == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y)
        }
        path.close()
        drawPath(path = path, color = Color(0xFF144272))
        drawPath(path = path, color = SkyBlue.copy(alpha = 0.55f), style = Stroke(width = 2.dp.toPx()))
    }
}
