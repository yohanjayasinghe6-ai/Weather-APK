package com.example.ui.screens

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.localization.AppLanguage
import com.example.localization.LocalAppStrings
import com.example.model.SriLankaLocation
import com.example.model.TemperatureUnit
import com.example.model.ThemeMode
import com.example.model.TimeFormatPreference
import com.example.model.UserPreferences
import com.example.model.WindSpeedUnit
import com.example.ui.components.SectionTitleRow
import com.example.ui.components.WeatherSectionCard
import com.example.util.WeatherNotificationHelper

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    preferences: UserPreferences,
    selectedLocation: SriLankaLocation,
    onLanguageSelected: (String) -> Unit,
    onTempUnitSelected: (TemperatureUnit) -> Unit,
    onWindUnitSelected: (WindSpeedUnit) -> Unit,
    onTimeFormatSelected: (TimeFormatPreference) -> Unit,
    onThemeModeSelected: (ThemeMode) -> Unit,
    onAutoRefreshChanged: (Boolean) -> Unit,
    onUseGpsChanged: (Boolean) -> Unit,
    onNotificationsChanged: (Boolean) -> Unit,
    onSevereAlertNotifChanged: (Boolean) -> Unit,
    onRainNotifChanged: (Boolean) -> Unit,
    onOpenLocationPicker: () -> Unit,
    onSendSampleNotification: () -> Boolean
) {
    val strings = LocalAppStrings.current
    val context = LocalContext.current

    val notifPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            onNotificationsChanged(true)
        } else {
            onNotificationsChanged(false)
            Toast.makeText(context, strings.notificationPermissionNeeded, Toast.LENGTH_SHORT).show()
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
                .widthIn(max = 760.dp)
                .testTag("settings_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = strings.settingsTitle,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // 1. Language Selection (English / Sinhala / Tamil)
            item {
                WeatherSectionCard {
                    SectionTitleRow(
                        icon = Icons.Filled.Language,
                        title = strings.languageLabel
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppLanguage.entries.forEach { lang ->
                            FilterChip(
                                selected = preferences.languageCode == lang.code,
                                onClick = { onLanguageSelected(lang.code) },
                                label = { Text(lang.nativeLabel) },
                                modifier = Modifier.testTag("lang_chip_${lang.code}")
                            )
                        }
                    }
                }
            }

            // 2. Units & Time Format
            item {
                WeatherSectionCard {
                    SectionTitleRow(
                        icon = Icons.Filled.Speed,
                        title = "${strings.temperatureUnitLabel} • ${strings.windSpeedUnitLabel}"
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = strings.temperatureUnitLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = preferences.temperatureUnit == TemperatureUnit.CELSIUS,
                            onClick = { onTempUnitSelected(TemperatureUnit.CELSIUS) },
                            label = { Text("Celsius (°C)") },
                            modifier = Modifier.testTag("unit_celsius")
                        )
                        FilterChip(
                            selected = preferences.temperatureUnit == TemperatureUnit.FAHRENHEIT,
                            onClick = { onTempUnitSelected(TemperatureUnit.FAHRENHEIT) },
                            label = { Text("Fahrenheit (°F)") },
                            modifier = Modifier.testTag("unit_fahrenheit")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = strings.windSpeedUnitLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            WindSpeedUnit.KMH to "km/h",
                            WindSpeedUnit.MS to "m/s",
                            WindSpeedUnit.MPH to "mph",
                            WindSpeedUnit.KNOTS to "knots"
                        ).forEach { (unit, label) ->
                            FilterChip(
                                selected = preferences.windSpeedUnit == unit,
                                onClick = { onWindUnitSelected(unit) },
                                label = { Text(label) },
                                modifier = Modifier.testTag("wind_unit_${unit.name}")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = strings.timeFormatLabel,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = preferences.timeFormat == TimeFormatPreference.HOUR_12,
                            onClick = { onTimeFormatSelected(TimeFormatPreference.HOUR_12) },
                            label = { Text(strings.time12Hour) },
                            modifier = Modifier.testTag("time_12h")
                        )
                        FilterChip(
                            selected = preferences.timeFormat == TimeFormatPreference.HOUR_24,
                            onClick = { onTimeFormatSelected(TimeFormatPreference.HOUR_24) },
                            label = { Text(strings.time24Hour) },
                            modifier = Modifier.testTag("time_24h")
                        )
                    }
                }
            }

            // 3. Appearance (System / Light / Dark)
            item {
                WeatherSectionCard {
                    SectionTitleRow(
                        icon = Icons.Filled.Palette,
                        title = strings.themeModeLabel
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            ThemeMode.SYSTEM to strings.themeSystem,
                            ThemeMode.LIGHT to strings.themeLight,
                            ThemeMode.DARK to strings.themeDark
                        ).forEach { (mode, label) ->
                            FilterChip(
                                selected = preferences.themeMode == mode,
                                onClick = { onThemeModeSelected(mode) },
                                label = { Text(label) },
                                modifier = Modifier.testTag("theme_${mode.name}")
                            )
                        }
                    }
                }
            }

            // 4. Location Preferences
            item {
                WeatherSectionCard {
                    SectionTitleRow(
                        icon = Icons.Filled.LocationOn,
                        title = strings.locationPreferences
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onOpenLocationPicker)
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = strings.defaultDistrict,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = selectedLocation.localizedName(preferences.languageCode),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Icon(Icons.Filled.ChevronRight, contentDescription = null)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                    SettingsSwitchRow(
                        title = strings.autoGpsSwitch,
                        checked = preferences.useGpsLocation,
                        onCheckedChange = onUseGpsChanged,
                        testTag = "switch_use_gps"
                    )
                }
            }

            // 5. Refresh & Notifications
            item {
                WeatherSectionCard {
                    SectionTitleRow(
                        icon = Icons.Filled.Notifications,
                        title = strings.refreshAndNotifications
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SettingsSwitchRow(
                        title = strings.autoRefreshLaunch,
                        checked = preferences.autoRefreshEnabled,
                        onCheckedChange = onAutoRefreshChanged,
                        testTag = "switch_auto_refresh"
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    SettingsSwitchRow(
                        title = strings.weatherNotifications,
                        checked = preferences.notificationsEnabled,
                        onCheckedChange = { wantEnable ->
                            if (wantEnable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                !WeatherNotificationHelper.hasNotificationPermission(context)
                            ) {
                                notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                onNotificationsChanged(wantEnable)
                            }
                        },
                        testTag = "switch_notifications"
                    )
                    if (preferences.notificationsEnabled) {
                        SettingsSwitchRow(
                            title = strings.severeAlertNotif,
                            checked = preferences.severeAlertNotifications,
                            onCheckedChange = onSevereAlertNotifChanged,
                            testTag = "switch_severe_notif"
                        )
                        SettingsSwitchRow(
                            title = strings.rainNotif,
                            checked = preferences.rainNotifications,
                            onCheckedChange = onRainNotifChanged,
                            testTag = "switch_rain_notif"
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                    !WeatherNotificationHelper.hasNotificationPermission(context)
                                ) {
                                    notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    val sent = onSendSampleNotification()
                                    if (sent) {
                                        Toast.makeText(
                                            context,
                                            strings.notificationSentToast,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("test_notification_button")
                        ) {
                            Text(strings.sendSampleNotif)
                        }
                    }
                }
            }

            // 6. Weather Data Sources & About
            item {
                WeatherSectionCard {
                    SectionTitleRow(
                        icon = Icons.Filled.CloudQueue,
                        title = strings.dataSourcesTitle
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = strings.dataSourcesDesc,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    SectionTitleRow(
                        icon = Icons.Filled.Info,
                        title = strings.aboutAppTitle
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = strings.aboutAppDesc,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.testTag(testTag)
        )
    }
}
