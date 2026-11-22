package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Query

@JsonClass(generateAdapter = true)
data class OpenMeteoForecastResponse(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timezone: String = "Asia/Colombo",
    val current: CurrentDto? = null,
    val hourly: HourlyDto? = null,
    val daily: DailyDto? = null
)

@JsonClass(generateAdapter = true)
data class CurrentDto(
    val time: String = "",
    @Json(name = "temperature_2m") val temperature2m: Double = 0.0,
    @Json(name = "relative_humidity_2m") val relativeHumidity2m: Int = 0,
    @Json(name = "apparent_temperature") val apparentTemperature: Double = 0.0,
    @Json(name = "is_day") val isDay: Int = 1,
    val precipitation: Double = 0.0,
    val rain: Double = 0.0,
    @Json(name = "weather_code") val weatherCode: Int = 0,
    @Json(name = "cloud_cover") val cloudCover: Int = 0,
    @Json(name = "surface_pressure") val surfacePressure: Double = 1013.0,
    @Json(name = "wind_speed_10m") val windSpeed10m: Double = 0.0,
    @Json(name = "wind_direction_10m") val windDirection10m: Int = 0
)

@JsonClass(generateAdapter = true)
data class HourlyDto(
    val time: List<String> = emptyList(),
    @Json(name = "temperature_2m") val temperature2m: List<Double?> = emptyList(),
    @Json(name = "relative_humidity_2m") val relativeHumidity2m: List<Int?> = emptyList(),
    @Json(name = "apparent_temperature") val apparentTemperature: List<Double?> = emptyList(),
    @Json(name = "precipitation_probability") val precipitationProbability: List<Int?> = emptyList(),
    val precipitation: List<Double?> = emptyList(),
    val rain: List<Double?> = emptyList(),
    @Json(name = "weather_code") val weatherCode: List<Int?> = emptyList(),
    @Json(name = "cloud_cover") val cloudCover: List<Int?> = emptyList(),
    val visibility: List<Double?> = emptyList(),
    @Json(name = "wind_speed_10m") val windSpeed10m: List<Double?> = emptyList(),
    @Json(name = "wind_direction_10m") val windDirection10m: List<Int?> = emptyList(),
    @Json(name = "uv_index") val uvIndex: List<Double?> = emptyList()
)

@JsonClass(generateAdapter = true)
data class DailyDto(
    val time: List<String> = emptyList(),
    @Json(name = "weather_code") val weatherCode: List<Int?> = emptyList(),
    @Json(name = "temperature_2m_max") val temperature2mMax: List<Double?> = emptyList(),
    @Json(name = "temperature_2m_min") val temperature2mMin: List<Double?> = emptyList(),
    val sunrise: List<String?> = emptyList(),
    val sunset: List<String?> = emptyList(),
    @Json(name = "uv_index_max") val uvIndexMax: List<Double?> = emptyList(),
    @Json(name = "precipitation_sum") val precipitationSum: List<Double?> = emptyList(),
    @Json(name = "rain_sum") val rainSum: List<Double?> = emptyList(),
    @Json(name = "precipitation_probability_max") val precipitationProbabilityMax: List<Int?> = emptyList(),
    @Json(name = "wind_speed_10m_max") val windSpeed10mMax: List<Double?> = emptyList(),
    @Json(name = "wind_direction_10m_dominant") val windDirection10mDominant: List<Int?> = emptyList()
)

@JsonClass(generateAdapter = true)
data class AirQualityResponse(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val current: AirQualityCurrentDto? = null
)

@JsonClass(generateAdapter = true)
data class AirQualityCurrentDto(
    @Json(name = "european_aqi") val europeanAqi: Int? = null,
    @Json(name = "us_aqi") val usAqi: Int? = null,
    val pm10: Double? = null,
    @Json(name = "pm2_5") val pm25: Double? = null,
    val ozone: Double? = null,
    @Json(name = "nitrogen_dioxide") val nitrogenDioxide: Double? = null
)

@JsonClass(generateAdapter = true)
data class GeocodingResponse(
    val results: List<GeocodingResultDto>? = null
)

@JsonClass(generateAdapter = true)
data class GeocodingResultDto(
    val id: Long = 0L,
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    @Json(name = "country_code") val countryCode: String? = null,
    val admin1: String? = null,
    val admin2: String? = null
)

interface OpenMeteoForecastApi {
    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("timezone") timezone: String = "Asia/Colombo",
        @Query("forecast_days") forecastDays: Int = 7,
        @Query("current") current: String =
            "temperature_2m,relative_humidity_2m,apparent_temperature,is_day,precipitation,rain,weather_code,cloud_cover,surface_pressure,wind_speed_10m,wind_direction_10m",
        @Query("hourly") hourly: String =
            "temperature_2m,relative_humidity_2m,apparent_temperature,precipitation_probability,precipitation,rain,weather_code,cloud_cover,visibility,wind_speed_10m,wind_direction_10m,uv_index",
        @Query("daily") daily: String =
            "weather_code,temperature_2m_max,temperature_2m_min,sunrise,sunset,uv_index_max,precipitation_sum,rain_sum,precipitation_probability_max,wind_speed_10m_max,wind_direction_10m_dominant"
    ): OpenMeteoForecastResponse

    @GET("v1/forecast")
    suspend fun getBatchDistrictSnapshots(
        @Query("latitude") latitudesCommaSeparated: String,
        @Query("longitude") longitudesCommaSeparated: String,
        @Query("timezone") timezone: String = "Asia/Colombo",
        @Query("forecast_days") forecastDays: Int = 1,
        @Query("current") current: String =
            "temperature_2m,relative_humidity_2m,is_day,precipitation,weather_code,wind_speed_10m",
        @Query("daily") daily: String = "precipitation_probability_max"
    ): List<OpenMeteoForecastResponse>
}

interface OpenMeteoAirQualityApi {
    @GET("v1/air-quality")
    suspend fun getAirQuality(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("timezone") timezone: String = "Asia/Colombo",
        @Query("current") current: String =
            "european_aqi,us_aqi,pm10,pm2_5,ozone,nitrogen_dioxide"
    ): AirQualityResponse
}

interface OpenMeteoGeocodingApi {
    @GET("v1/search")
    suspend fun searchLocations(
        @Query("name") query: String,
        @Query("count") count: Int = 15,
        @Query("language") language: String = "en",
        @Query("format") format: String = "json"
    ): GeocodingResponse
}
