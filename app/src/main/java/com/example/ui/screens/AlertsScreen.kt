package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.remote.OfficialAlertsService
import com.example.localization.LocalAppStrings
import com.example.model.AlertSeverity
import com.example.model.AlertsResult
import com.example.model.SriLankaLocation
import com.example.model.UserPreferences
import com.example.model.WeatherAlert
import com.example.model.WeatherUiState
import com.example.ui.components.SectionTitleRow
import com.example.ui.components.WeatherSectionCard
import com.example.ui.theme.AlertAmber
import com.example.ui.theme.AlertOrange
import com.example.ui.theme.AlertRed
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.SafeEmerald
import com.example.util.WeatherFormatters

@Composable
fun AlertsScreen(
    selectedLocation: SriLankaLocation,
    weatherState: WeatherUiState,
    alertsResult: AlertsResult?,
    isAlertsLoading: Boolean,
    preferences: UserPreferences,
    onRefreshAlerts: () -> Unit
) {
    val strings = LocalAppStrings.current
    val context = LocalContext.current

    fun openOfficialMetPortal(url: String = OfficialAlertsService.MET_GOV_LK_URL) {
        runCatching {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    }

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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = strings.alertsTitle,
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = strings.sourceDeptMet,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = onRefreshAlerts,
                        enabled = !isAlertsLoading,
                        modifier = Modifier.testTag("alerts_refresh_button")
                    ) {
                        if (isAlertsLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Filled.Refresh, contentDescription = strings.refresh)
                        }
                    }
                }
            }

            // Official Verified Alerts Section
            item {
                Text(
                    text = strings.officialAlertsHeader,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            when (alertsResult) {
                null -> {
                    item {
                        WeatherSectionCard {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(strings.loadingWeather)
                            }
                        }
                    }
                }

                is AlertsResult.Available -> {
                    items(alertsResult.alerts, key = { it.id }) { alert ->
                        OfficialAlertCard(
                            alert = alert,
                            onOpenUrl = { url -> openOfficialMetPortal(url) }
                        )
                    }
                }

                is AlertsResult.NoActiveAlerts -> {
                    item {
                        WeatherSectionCard {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = SafeEmerald,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = strings.noOfficialAlertsTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = strings.noOfficialAlertsDesc,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            val checkedAt = WeatherFormatters.formatEpochTime(
                                alertsResult.fetchedAtMillis,
                                preferences.timeFormat
                            )
                            Text(
                                text = "${strings.officialSourceLabel}: ${alertsResult.sourceLabel} • $checkedAt",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = { openOfficialMetPortal() },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("open_meteo_portal_button")
                            ) {
                                Text(strings.openOfficialMetPortal)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                is AlertsResult.Unavailable -> {
                    item {
                        WeatherSectionCard(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.CloudOff,
                                    contentDescription = null,
                                    tint = AlertOrange,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = strings.unavailableAlertsTitle,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = strings.unavailableAlertsDesc,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = onRefreshAlerts,
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("retry_alerts_button")
                                ) {
                                    Text(strings.retry)
                                }
                                OutlinedButton(
                                    onClick = { openOfficialMetPortal(alertsResult.officialPortalUrl) },
                                    shape = RoundedCornerShape(14.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("open_meteo_portal_button")
                                ) {
                                    Text(strings.openOfficialMetPortal)
                                }
                            }
                        }
                    }
                }
            }

            // Forecast Model Observations (Transparently NOT official warnings)
            item {
                val bundle = (weatherState as? WeatherUiState.Success)?.bundle
                val today = bundle?.daily?.firstOrNull()
                val modelNotes = buildList {
                    if (today != null) {
                        if (today.precipitationSumMm >= 25.0) {
                            add("${strings.expectedRainfall}: ${WeatherFormatters.formatRainAmount(today.precipitationSumMm)} (${today.rainProbabilityMaxPercent}%)")
                        }
                        if (today.windSpeedMaxKmh >= 35.0) {
                            add("${strings.wind}: ${WeatherFormatters.formatWindSpeed(today.windSpeedMaxKmh, preferences.windSpeedUnit)}")
                        }
                        if (today.uvIndexMax >= 8.0) {
                            add("${strings.maxUv}: ${String.format("%.1f", today.uvIndexMax)} (${WeatherFormatters.uvCategoryLabel(today.uvIndexMax, strings)})")
                        }
                    }
                }

                WeatherSectionCard {
                    SectionTitleRow(
                        icon = Icons.Filled.Info,
                        title = "${strings.modelObservationsHeader} • ${
                            selectedLocation.localizedName(preferences.languageCode)
                        }"
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = strings.modelObservationsDisclaimer,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    if (modelNotes.isEmpty()) {
                        Text(
                            text = strings.noSignificantModelObs,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    } else {
                        modelNotes.forEach { note ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = AlertAmber.copy(alpha = 0.14f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                            ) {
                                Text(
                                    text = "• $note",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Preparedness Guide
            item {
                WeatherSectionCard {
                    SectionTitleRow(
                        icon = Icons.Filled.Shield,
                        title = strings.preparednessTitle
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "• ${strings.preparednessFlood}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• ${strings.preparednessLightning}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• ${strings.preparednessWind}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun OfficialAlertCard(
    alert: WeatherAlert,
    onOpenUrl: (String) -> Unit
) {
    val strings = LocalAppStrings.current
    val accent = when (alert.severity) {
        AlertSeverity.EXTREME -> AlertRed
        AlertSeverity.SEVERE -> AlertOrange
        AlertSeverity.MODERATE -> AlertAmber
        AlertSeverity.ADVISORY -> ElectricCyan
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.5.dp, accent.copy(alpha = 0.7f)),
        tonalElevation = 3.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = accent.copy(alpha = 0.16f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${strings.severityLabel(alert.severity)} • ${
                                strings.alertCategoryLabel(alert.category)
                            }",
                            style = MaterialTheme.typography.labelMedium,
                            color = accent
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = alert.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            if (alert.summary.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = alert.summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "${strings.officialSourceLabel}: ${alert.source}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${strings.publishedAtLabel}: ${alert.publishedAt}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${strings.affectedAreasLabel}: ${alert.affectedAreas}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (!alert.expiresAt.isNullOrBlank()) {
                Text(
                    text = "${strings.expiresAtLabel}: ${alert.expiresAt}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (!alert.url.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = { onOpenUrl(alert.url) },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(strings.openOfficialMetPortal)
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
