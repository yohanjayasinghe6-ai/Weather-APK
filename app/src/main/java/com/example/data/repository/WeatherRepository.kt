package com.example.data.repository

import com.example.data.local.CachedWeatherEntity
import com.example.data.local.SavedLocationEntity
import com.example.data.local.WeatherDao
import com.example.data.remote.AirQualityResponse
import com.example.data.remote.OfficialAlertsService
import com.example.data.remote.OpenMeteoAirQualityApi
import com.example.data.remote.OpenMeteoForecastApi
import com.example.data.remote.OpenMeteoForecastResponse
import com.example.data.remote.OpenMeteoGeocodingApi
import com.example.data.remote.RainViewerApi
import com.example.model.AirQualityData
import com.example.model.AlertsResult
import com.example.model.CurrentWeather
import com.example.model.DailyForecast
import com.example.model.DistrictWeatherSnapshot
import com.example.model.HourlyForecast
import com.example.model.MonsoonZone
import com.example.model.RadarData
import com.example.model.RadarFrame
import com.example.model.SriLankaLocation
import com.example.model.SriLankaLocations
import com.example.model.WeatherBundle
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class WeatherRepository(
    private val weatherDao: WeatherDao,
    private val forecastApi: OpenMeteoForecastApi,
    private val airQualityApi: OpenMeteoAirQualityApi,
    private val geocodingApi: OpenMeteoGeocodingApi,
    private val rainViewerApi: RainViewerApi,
    private val alertsService: OfficialAlertsService,
    private val moshi: Moshi
) {

    private val bundleAdapter = moshi.adapter(WeatherBundle::class.java)

    val favoriteLocations: Flow<List<SriLankaLocation>> =
        weatherDao.observeFavoriteLocations().map { list ->
            list.map { it.toDomain() }
        }

    suspend fun ensureDefaultFavorites() = withContext(Dispatchers.IO) {
        if (weatherDao.countFavorites() == 0) {
            val defaults = listOf("colombo", "kandy", "galle", "jaffna")
            defaults.forEachIndexed { idx, id ->
                SriLankaLocations.findById(id)?.let { loc ->
                    weatherDao.upsertSavedLocation(
                        SavedLocationEntity.fromDomain(loc, isFavorite = true, sortOrder = idx)
                    )
                }
            }
        }
    }

    suspend fun toggleFavorite(location: SriLankaLocation): Boolean = withContext(Dispatchers.IO) {
        val existing = weatherDao.getSavedLocation(location.id)
        if (existing != null && existing.isFavorite) {
            weatherDao.deleteSavedLocation(location.id)
            false
        } else {
            val count = weatherDao.countFavorites()
            weatherDao.upsertSavedLocation(
                SavedLocationEntity.fromDomain(location, isFavorite = true, sortOrder = count)
            )
            true
        }
    }

    suspend fun saveCustomLocation(location: SriLankaLocation) = withContext(Dispatchers.IO) {
        val count = weatherDao.countFavorites()
        weatherDao.upsertSavedLocation(
            SavedLocationEntity.fromDomain(location, isFavorite = true, sortOrder = count)
        )
    }

    suspend fun getSavedLocationById(id: String): SriLankaLocation? = withContext(Dispatchers.IO) {
        SriLankaLocations.findById(id) ?: weatherDao.getSavedLocation(id)?.toDomain()
    }

    suspend fun getCachedWeather(locationId: String): WeatherBundle? = withContext(Dispatchers.IO) {
        val cached = weatherDao.getCachedWeather(locationId) ?: return@withContext null
        runCatching {
            bundleAdapter.fromJson(cached.payloadJson)?.copy(isFromCache = true)
        }.getOrNull()
    }

    suspend fun fetchWeather(
        location: SriLankaLocation,
        forceRefresh: Boolean = false
    ): Result<WeatherBundle> = withContext(Dispatchers.IO) {
        val cached = getCachedWeather(location.id)
        val cacheMaxAgeMs = 15 * 60 * 1000L // 15 minutes
        if (!forceRefresh && cached != null &&
            (System.currentTimeMillis() - cached.fetchedAtMillis) < cacheMaxAgeMs
        ) {
            return@withContext Result.success(cached.copy(isFromCache = false))
        }

        try {
            val bundle = coroutineScope {
                val forecastDeferred = async {
                    forecastApi.getForecast(
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                }
                val aqiDeferred = async {
                    runCatching {
                        airQualityApi.getAirQuality(
                            latitude = location.latitude,
                            longitude = location.longitude
                        )
                    }.getOrNull()
                }
                val forecast = forecastDeferred.await()
                val aqi = aqiDeferred.await()
                mapToWeatherBundle(location, forecast, aqi)
            }

            bundleAdapter.toJson(bundle)?.let { json ->
                weatherDao.upsertCachedWeather(
                    CachedWeatherEntity(
                        locationId = location.id,
                        payloadJson = json,
                        fetchedAtMillis = bundle.fetchedAtMillis
                    )
                )
            }
            Result.success(bundle)
        } catch (e: Exception) {
            if (cached != null) {
                Result.success(cached.copy(isFromCache = true))
            } else {
                Result.failure(e)
            }
        }
    }

    suspend fun fetchComparisonWeather(
        locations: List<SriLankaLocation>
    ): Map<String, WeatherBundle> = withContext(Dispatchers.IO) {
        val result = mutableMapOf<String, WeatherBundle>()
        coroutineScope {
            val jobs = locations.take(6).map { loc ->
                loc.id to async { fetchWeather(loc, forceRefresh = false).getOrNull() }
            }
            jobs.forEach { (id, deferred) ->
                deferred.await()?.let { result[id] = it }
            }
        }
        result
    }

    suspend fun fetchAll25DistrictSnapshots(): Result<List<DistrictWeatherSnapshot>> =
        withContext(Dispatchers.IO) {
            try {
                val districts = SriLankaLocations.allDistricts
                val lats = districts.joinToString(",") { it.latitude.toString() }
                val lons = districts.joinToString(",") { it.longitude.toString() }
                val responses = forecastApi.getBatchDistrictSnapshots(lats, lons)
                val snapshots = districts.mapIndexedNotNull { idx, loc ->
                    val dto = responses.getOrNull(idx) ?: return@mapIndexedNotNull null
                    val current = dto.current ?: return@mapIndexedNotNull null
                    val rainProb = dto.daily?.precipitationProbabilityMax?.firstOrNull() ?: 0
                    DistrictWeatherSnapshot(
                        location = loc,
                        temperatureC = current.temperature2m,
                        weatherCode = current.weatherCode,
                        precipitationMm = current.precipitation,
                        rainProbabilityMax = rainProb,
                        windSpeedKmh = current.windSpeed10m,
                        humidityPercent = current.relativeHumidity2m,
                        isDay = current.isDay == 1
                    )
                }
                Result.success(snapshots)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun searchSriLankaLocations(query: String): List<SriLankaLocation> =
        withContext(Dispatchers.IO) {
            val trimmed = query.trim()
            if (trimmed.isBlank()) return@withContext SriLankaLocations.allDistricts

            val localMatches = SriLankaLocations.allPresetLocations.filter { loc ->
                loc.nameEn.contains(trimmed, ignoreCase = true) ||
                    loc.nameSi.contains(trimmed, ignoreCase = true) ||
                    loc.nameTa.contains(trimmed, ignoreCase = true) ||
                    loc.districtEn.contains(trimmed, ignoreCase = true) ||
                    loc.provinceEn.contains(trimmed, ignoreCase = true)
            }

            if (trimmed.length < 2) return@withContext localMatches

            val remoteMatches = runCatching {
                geocodingApi.searchLocations(trimmed).results
                    .orEmpty()
                    .filter { it.countryCode.equals("LK", ignoreCase = true) }
                    .map { dto ->
                        val nearest = SriLankaLocations.findNearest(dto.latitude, dto.longitude)
                        val cleanId = "lk_${dto.id}"
                        SriLankaLocation(
                            id = cleanId,
                            nameEn = dto.name,
                            nameSi = dto.name,
                            nameTa = dto.name,
                            districtEn = dto.admin2 ?: nearest.districtEn,
                            provinceEn = dto.admin1 ?: nearest.provinceEn,
                            provinceSi = nearest.provinceSi,
                            provinceTa = nearest.provinceTa,
                            latitude = dto.latitude,
                            longitude = dto.longitude,
                            isDistrictCapital = false,
                            monsoonZone = nearest.monsoonZone
                        )
                    }
            }.getOrDefault(emptyList())

            (localMatches + remoteMatches).distinctBy {
                it.nameEn.lowercase(Locale.ROOT)
            }
        }

    suspend fun fetchOfficialAlerts(): AlertsResult = alertsService.fetchOfficialAlerts()

    suspend fun fetchRadarData(): Result<RadarData> = withContext(Dispatchers.IO) {
        runCatching {
            val response = rainViewerApi.getWeatherMaps()
            val past = response.radar?.past.orEmpty().map {
                RadarFrame(timeEpochSec = it.time, path = it.path, isNowcast = false)
            }
            val nowcast = response.radar?.nowcast.orEmpty().map {
                RadarFrame(timeEpochSec = it.time, path = it.path, isNowcast = true)
            }
            RadarData(
                host = response.host,
                frames = past + nowcast,
                fetchedAtMillis = System.currentTimeMillis()
            )
        }
    }

    private fun mapToWeatherBundle(
        location: SriLankaLocation,
        forecast: OpenMeteoForecastResponse,
        aqi: AirQualityResponse?
    ): WeatherBundle {
        val now = System.currentTimeMillis()
        val currentDto = forecast.current
        val hourlyDto = forecast.hourly
        val dailyDto = forecast.daily

        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("Asia/Colombo")
        }

        val hourlyList = mutableListOf<HourlyForecast>()
        val times = hourlyDto?.time.orEmpty()
        for (i in times.indices) {
            val timeStr = times[i]
            val epoch = runCatching { parser.parse(timeStr)?.time ?: 0L }.getOrDefault(0L)
            hourlyList.add(
                HourlyForecast(
                    timeIso = timeStr,
                    epochMillis = epoch,
                    temperatureC = hourlyDto?.temperature2m?.getOrNull(i) ?: 0.0,
                    apparentTempC = hourlyDto?.apparentTemperature?.getOrNull(i) ?: 0.0,
                    humidityPercent = hourlyDto?.relativeHumidity2m?.getOrNull(i) ?: 0,
                    rainProbabilityPercent = hourlyDto?.precipitationProbability?.getOrNull(i) ?: 0,
                    precipitationMm = hourlyDto?.precipitation?.getOrNull(i) ?: 0.0,
                    rainMm = hourlyDto?.rain?.getOrNull(i) ?: 0.0,
                    weatherCode = hourlyDto?.weatherCode?.getOrNull(i) ?: 0,
                    windSpeedKmh = hourlyDto?.windSpeed10m?.getOrNull(i) ?: 0.0,
                    windDirectionDeg = hourlyDto?.windDirection10m?.getOrNull(i) ?: 0,
                    uvIndex = hourlyDto?.uvIndex?.getOrNull(i) ?: 0.0,
                    visibilityMeters = hourlyDto?.visibility?.getOrNull(i) ?: 10000.0,
                    cloudCoverPercent = hourlyDto?.cloudCover?.getOrNull(i) ?: 0
                )
            )
        }

        val dailyList = mutableListOf<DailyForecast>()
        val dates = dailyDto?.time.orEmpty()
        for (i in dates.indices) {
            dailyList.add(
                DailyForecast(
                    dateIso = dates[i],
                    weatherCode = dailyDto?.weatherCode?.getOrNull(i) ?: 0,
                    tempMaxC = dailyDto?.temperature2mMax?.getOrNull(i) ?: 0.0,
                    tempMinC = dailyDto?.temperature2mMin?.getOrNull(i) ?: 0.0,
                    sunriseIso = dailyDto?.sunrise?.getOrNull(i).orEmpty(),
                    sunsetIso = dailyDto?.sunset?.getOrNull(i).orEmpty(),
                    uvIndexMax = dailyDto?.uvIndexMax?.getOrNull(i) ?: 0.0,
                    precipitationSumMm = dailyDto?.precipitationSum?.getOrNull(i) ?: 0.0,
                    rainSumMm = dailyDto?.rainSum?.getOrNull(i) ?: 0.0,
                    rainProbabilityMaxPercent = dailyDto?.precipitationProbabilityMax?.getOrNull(i) ?: 0,
                    windSpeedMaxKmh = dailyDto?.windSpeed10mMax?.getOrNull(i) ?: 0.0,
                    windDirectionDominantDeg = dailyDto?.windDirection10mDominant?.getOrNull(i) ?: 0
                )
            )
        }

        // Match current hour index in hourly data for UV index and Visibility
        val currentHourPrefix = currentDto?.time?.take(13).orEmpty()
        val matchedHour = hourlyList.firstOrNull { it.timeIso.startsWith(currentHourPrefix) }
            ?: hourlyList.firstOrNull { it.epochMillis >= now - 3600_000L }
            ?: hourlyList.firstOrNull()

        val today = dailyList.firstOrNull()

        val currentWeather = CurrentWeather(
            temperatureC = currentDto?.temperature2m ?: matchedHour?.temperatureC ?: 0.0,
            feelsLikeC = currentDto?.apparentTemperature ?: matchedHour?.apparentTempC ?: 0.0,
            humidityPercent = currentDto?.relativeHumidity2m ?: matchedHour?.humidityPercent ?: 0,
            isDay = (currentDto?.isDay ?: 1) == 1,
            precipitationMm = currentDto?.precipitation ?: matchedHour?.precipitationMm ?: 0.0,
            rainMm = currentDto?.rain ?: matchedHour?.rainMm ?: 0.0,
            weatherCode = currentDto?.weatherCode ?: matchedHour?.weatherCode ?: 0,
            cloudCoverPercent = currentDto?.cloudCover ?: matchedHour?.cloudCoverPercent ?: 0,
            pressureHpa = currentDto?.surfacePressure ?: 1012.0,
            windSpeedKmh = currentDto?.windSpeed10m ?: matchedHour?.windSpeedKmh ?: 0.0,
            windDirectionDeg = currentDto?.windDirection10m ?: matchedHour?.windDirectionDeg ?: 0,
            visibilityMeters = matchedHour?.visibilityMeters ?: 10000.0,
            uvIndex = matchedHour?.uvIndex ?: 0.0,
            sunriseIso = today?.sunriseIso.orEmpty(),
            sunsetIso = today?.sunsetIso.orEmpty(),
            observationTimeIso = currentDto?.time.orEmpty()
        )

        val aqiData = aqi?.current?.let {
            AirQualityData(
                europeanAqi = it.europeanAqi,
                usAqi = it.usAqi,
                pm25 = it.pm25,
                pm10 = it.pm10,
                ozone = it.ozone,
                no2 = it.nitrogenDioxide,
                fetchedAtMillis = now
            )
        }

        return WeatherBundle(
            locationId = location.id,
            locationName = location.nameEn,
            latitude = location.latitude,
            longitude = location.longitude,
            fetchedAtMillis = now,
            timezone = forecast.timezone,
            current = currentWeather,
            hourly = hourlyList,
            daily = dailyList,
            airQuality = aqiData,
            isFromCache = false
        )
    }

    companion object {
        fun create(weatherDao: WeatherDao): WeatherRepository {
            val moshi = Moshi.Builder()
                .addLast(KotlinJsonAdapterFactory())
                .build()

            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val client = OkHttpClient.Builder()
                .connectTimeout(12, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build()

            val forecastApi = Retrofit.Builder()
                .baseUrl("https://api.open-meteo.com/")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(OpenMeteoForecastApi::class.java)

            val airQualityApi = Retrofit.Builder()
                .baseUrl("https://air-quality-api.open-meteo.com/")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(OpenMeteoAirQualityApi::class.java)

            val geocodingApi = Retrofit.Builder()
                .baseUrl("https://geocoding-api.open-meteo.com/")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(OpenMeteoGeocodingApi::class.java)

            val rainViewerApi = Retrofit.Builder()
                .baseUrl("https://api.rainviewer.com/")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(RainViewerApi::class.java)

            return WeatherRepository(
                weatherDao = weatherDao,
                forecastApi = forecastApi,
                airQualityApi = airQualityApi,
                geocodingApi = geocodingApi,
                rainViewerApi = rainViewerApi,
                alertsService = OfficialAlertsService(client),
                moshi = moshi
            )
        }
    }
}
