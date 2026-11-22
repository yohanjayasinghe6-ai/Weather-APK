package com.example.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherBundle(
    val locationId: String,
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val fetchedAtMillis: Long,
    val timezone: String,
    val current: CurrentWeather,
    val hourly: List<HourlyForecast>,
    val daily: List<DailyForecast>,
    val airQuality: AirQualityData? = null,
    val isFromCache: Boolean = false
)

@JsonClass(generateAdapter = true)
data class CurrentWeather(
    val temperatureC: Double,
    val feelsLikeC: Double,
    val humidityPercent: Int,
    val isDay: Boolean,
    val precipitationMm: Double,
    val rainMm: Double,
    val weatherCode: Int,
    val cloudCoverPercent: Int,
    val pressureHpa: Double,
    val windSpeedKmh: Double,
    val windDirectionDeg: Int,
    val visibilityMeters: Double,
    val uvIndex: Double,
    val sunriseIso: String,
    val sunsetIso: String,
    val observationTimeIso: String
)

@JsonClass(generateAdapter = true)
data class HourlyForecast(
    val timeIso: String,
    val epochMillis: Long,
    val temperatureC: Double,
    val apparentTempC: Double,
    val humidityPercent: Int,
    val rainProbabilityPercent: Int,
    val precipitationMm: Double,
    val rainMm: Double,
    val weatherCode: Int,
    val windSpeedKmh: Double,
    val windDirectionDeg: Int,
    val uvIndex: Double,
    val visibilityMeters: Double,
    val cloudCoverPercent: Int
)

@JsonClass(generateAdapter = true)
data class DailyForecast(
    val dateIso: String,
    val weatherCode: Int,
    val tempMaxC: Double,
    val tempMinC: Double,
    val sunriseIso: String,
    val sunsetIso: String,
    val uvIndexMax: Double,
    val precipitationSumMm: Double,
    val rainSumMm: Double,
    val rainProbabilityMaxPercent: Int,
    val windSpeedMaxKmh: Double,
    val windDirectionDominantDeg: Int
)

@JsonClass(generateAdapter = true)
data class AirQualityData(
    val europeanAqi: Int?,
    val usAqi: Int?,
    val pm25: Double?,
    val pm10: Double?,
    val ozone: Double?,
    val no2: Double?,
    val fetchedAtMillis: Long
)

data class DistrictWeatherSnapshot(
    val location: SriLankaLocation,
    val temperatureC: Double,
    val weatherCode: Int,
    val precipitationMm: Double,
    val rainProbabilityMax: Int,
    val windSpeedKmh: Double,
    val humidityPercent: Int,
    val isDay: Boolean
)

enum class AlertSeverity {
    EXTREME,
    SEVERE,
    MODERATE,
    ADVISORY
}

enum class AlertCategory {
    HEAVY_RAIN,
    FLOOD,
    STRONG_WIND,
    THUNDERSTORM,
    CYCLONE,
    SEVERE_WEATHER
}

data class WeatherAlert(
    val id: String,
    val title: String,
    val summary: String,
    val severity: AlertSeverity,
    val category: AlertCategory,
    val source: String,
    val publishedAt: String,
    val affectedAreas: String,
    val expiresAt: String?,
    val url: String?
)

sealed interface AlertsResult {
    data class Available(
        val alerts: List<WeatherAlert>,
        val sourceLabel: String,
        val fetchedAtMillis: Long
    ) : AlertsResult

    data class NoActiveAlerts(
        val sourceLabel: String,
        val fetchedAtMillis: Long
    ) : AlertsResult

    data class Unavailable(
        val reason: String,
        val officialPortalUrl: String,
        val fetchedAtMillis: Long
    ) : AlertsResult
}

enum class MonsoonSeason {
    SOUTHWEST_MONSOON,
    NORTHEAST_MONSOON,
    FIRST_INTER_MONSOON,
    SECOND_INTER_MONSOON
}

data class MonsoonSeasonalProfile(
    val season: MonsoonSeason,
    val activeMonthsRange: String,
    val isSelectedZonePrimaryRainSeason: Boolean
)

data class RadarFrame(
    val timeEpochSec: Long,
    val path: String,
    val isNowcast: Boolean
)

data class RadarData(
    val host: String,
    val frames: List<RadarFrame>,
    val fetchedAtMillis: Long
)

enum class TemperatureUnit {
    CELSIUS,
    FAHRENHEIT
}

enum class WindSpeedUnit {
    KMH,
    MS,
    MPH,
    KNOTS
}

enum class TimeFormatPreference {
    HOUR_12,
    HOUR_24
}

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

data class UserPreferences(
    val languageCode: String = "en",
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val windSpeedUnit: WindSpeedUnit = WindSpeedUnit.KMH,
    val timeFormat: TimeFormatPreference = TimeFormatPreference.HOUR_12,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val autoRefreshEnabled: Boolean = true,
    val notificationsEnabled: Boolean = false,
    val severeAlertNotifications: Boolean = true,
    val rainNotifications: Boolean = true,
    val selectedLocationId: String = "colombo",
    val useGpsLocation: Boolean = false
)

sealed interface WeatherUiState {
    data object Loading : WeatherUiState
    data class Success(val bundle: WeatherBundle) : WeatherUiState
    data class Error(val message: String, val cachedBundle: WeatherBundle? = null) : WeatherUiState
}
