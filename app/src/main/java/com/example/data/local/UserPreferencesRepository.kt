package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.TemperatureUnit
import com.example.model.ThemeMode
import com.example.model.TimeFormatPreference
import com.example.model.UserPreferences
import com.example.model.WindSpeedUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "lanka_weather_prefs"
)

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val LANGUAGE = stringPreferencesKey("language_code")
        val TEMP_UNIT = stringPreferencesKey("temp_unit")
        val WIND_UNIT = stringPreferencesKey("wind_unit")
        val TIME_FORMAT = stringPreferencesKey("time_format")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val AUTO_REFRESH = booleanPreferencesKey("auto_refresh")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val SEVERE_NOTIF = booleanPreferencesKey("severe_notif")
        val RAIN_NOTIF = booleanPreferencesKey("rain_notif")
        val SELECTED_LOCATION_ID = stringPreferencesKey("selected_location_id")
        val USE_GPS = booleanPreferencesKey("use_gps")
    }

    val preferences: Flow<UserPreferences> = context.userPreferencesDataStore.data.map { prefs ->
        UserPreferences(
            languageCode = prefs[Keys.LANGUAGE] ?: "en",
            temperatureUnit = enumOrDefault(prefs[Keys.TEMP_UNIT], TemperatureUnit.CELSIUS),
            windSpeedUnit = enumOrDefault(prefs[Keys.WIND_UNIT], WindSpeedUnit.KMH),
            timeFormat = enumOrDefault(prefs[Keys.TIME_FORMAT], TimeFormatPreference.HOUR_12),
            themeMode = enumOrDefault(prefs[Keys.THEME_MODE], ThemeMode.SYSTEM),
            autoRefreshEnabled = prefs[Keys.AUTO_REFRESH] ?: true,
            notificationsEnabled = prefs[Keys.NOTIFICATIONS_ENABLED] ?: false,
            severeAlertNotifications = prefs[Keys.SEVERE_NOTIF] ?: true,
            rainNotifications = prefs[Keys.RAIN_NOTIF] ?: true,
            selectedLocationId = prefs[Keys.SELECTED_LOCATION_ID] ?: "colombo",
            useGpsLocation = prefs[Keys.USE_GPS] ?: false
        )
    }

    suspend fun setLanguage(code: String) {
        context.userPreferencesDataStore.edit { it[Keys.LANGUAGE] = code }
    }

    suspend fun setTemperatureUnit(unit: TemperatureUnit) {
        context.userPreferencesDataStore.edit { it[Keys.TEMP_UNIT] = unit.name }
    }

    suspend fun setWindSpeedUnit(unit: WindSpeedUnit) {
        context.userPreferencesDataStore.edit { it[Keys.WIND_UNIT] = unit.name }
    }

    suspend fun setTimeFormat(format: TimeFormatPreference) {
        context.userPreferencesDataStore.edit { it[Keys.TIME_FORMAT] = format.name }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.userPreferencesDataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    suspend fun setAutoRefresh(enabled: Boolean) {
        context.userPreferencesDataStore.edit { it[Keys.AUTO_REFRESH] = enabled }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.userPreferencesDataStore.edit { it[Keys.NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun setSevereAlertNotifications(enabled: Boolean) {
        context.userPreferencesDataStore.edit { it[Keys.SEVERE_NOTIF] = enabled }
    }

    suspend fun setRainNotifications(enabled: Boolean) {
        context.userPreferencesDataStore.edit { it[Keys.RAIN_NOTIF] = enabled }
    }

    suspend fun setSelectedLocationId(locationId: String) {
        context.userPreferencesDataStore.edit { it[Keys.SELECTED_LOCATION_ID] = locationId }
    }

    suspend fun setUseGpsLocation(enabled: Boolean) {
        context.userPreferencesDataStore.edit { it[Keys.USE_GPS] = enabled }
    }

    private inline fun <reified T : Enum<T>> enumOrDefault(raw: String?, default: T): T {
        if (raw == null) return default
        return runCatching { enumValueOf<T>(raw) }.getOrDefault(default)
    }
}
