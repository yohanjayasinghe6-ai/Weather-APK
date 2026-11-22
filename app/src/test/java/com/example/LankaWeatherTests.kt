package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextReplacement
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.LankaWeatherDatabase
import com.example.data.local.UserPreferencesRepository
import com.example.data.remote.AirQualityCurrentDto
import com.example.data.remote.AirQualityResponse
import com.example.data.remote.CurrentDto
import com.example.data.remote.DailyDto
import com.example.data.remote.GeocodingResponse
import com.example.data.remote.GeocodingResultDto
import com.example.data.remote.HourlyDto
import com.example.data.remote.OfficialAlertsService
import com.example.data.remote.OpenMeteoAirQualityApi
import com.example.data.remote.OpenMeteoForecastApi
import com.example.data.remote.OpenMeteoForecastResponse
import com.example.data.remote.OpenMeteoGeocodingApi
import com.example.data.remote.RainViewerApi
import com.example.data.remote.RainViewerFrameDto
import com.example.data.remote.RainViewerRadarDto
import com.example.data.remote.RainViewerResponse
import com.example.data.repository.WeatherRepository
import com.example.localization.EnglishStrings
import com.example.localization.SinhalaStrings
import com.example.localization.TamilStrings
import com.example.model.AlertsResult
import com.example.model.HourlyForecast
import com.example.model.MonsoonSeason
import com.example.model.MonsoonZone
import com.example.model.SriLankaLocations
import com.example.model.TemperatureUnit
import com.example.model.TimeFormatPreference
import com.example.model.WeatherUiState
import com.example.model.WindSpeedUnit
import com.example.ui.LankaWeatherApp
import com.example.ui.MainViewModel
import com.example.util.WeatherFormatters
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class LankaWeatherTests {

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var context: Context
    private lateinit var db: LankaWeatherDatabase
    private lateinit var prefsRepo: UserPreferencesRepository

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, LankaWeatherDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        prefsRepo = UserPreferencesRepository(context)
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun `all 25 districts of Sri Lanka are present with valid coordinates and trilingual names`() {
        val districts = SriLankaLocations.allDistricts
        assertEquals(25, districts.size)
        districts.forEach { d ->
            assertTrue("Latitude out of Sri Lanka bounds for ${d.nameEn}", d.latitude in 5.8..9.9)
            assertTrue("Longitude out of Sri Lanka bounds for ${d.nameEn}", d.longitude in 79.5..82.0)
            assertTrue(d.nameEn.isNotBlank())
            assertTrue(d.nameSi.isNotBlank())
            assertTrue(d.nameTa.isNotBlank())
        }
        val expected = listOf(
            "Colombo", "Kandy", "Galle", "Jaffna", "Matara", "Gampaha",
            "Kurunegala", "Anuradhapura", "Trincomalee", "Batticaloa",
            "Badulla", "Nuwara Eliya", "Ratnapura"
        )
        expected.forEach { name ->
            assertTrue("Missing $name", districts.any { it.nameEn == name })
        }
    }

    @Test
    fun `formatters convert units, wind, monsoon seasons, and rainy windows accurately`() {
        assertEquals("30°C", WeatherFormatters.formatTemperature(30.0, TemperatureUnit.CELSIUS))
        assertEquals("86°F", WeatherFormatters.formatTemperature(30.0, TemperatureUnit.FAHRENHEIT))
        assertEquals("18 km/h", WeatherFormatters.formatWindSpeed(18.0, WindSpeedUnit.KMH))
        assertEquals("5.0 m/s", WeatherFormatters.formatWindSpeed(18.0, WindSpeedUnit.MS))

        val swProfile = WeatherFormatters.currentMonsoonProfile(MonsoonZone.SOUTHWEST_WET, monthOneBased = 7)
        assertEquals(MonsoonSeason.SOUTHWEST_MONSOON, swProfile.season)
        assertTrue(swProfile.isSelectedZonePrimaryRainSeason)

        val neProfile = WeatherFormatters.currentMonsoonProfile(MonsoonZone.NORTH_EAST_DRY, monthOneBased = 1)
        assertEquals(MonsoonSeason.NORTHEAST_MONSOON, neProfile.season)
        assertTrue(neProfile.isSelectedZonePrimaryRainSeason)

        val hourly = listOf(
            HourlyForecast("2026-11-21T14:00", 1000L, 28.0, 31.0, 80, 75, 4.2, 4.2, 63, 14.0, 210, 4.0, 8000.0, 90),
            HourlyForecast("2026-11-21T15:00", 4600_000L, 27.0, 30.0, 85, 80, 6.1, 6.1, 63, 16.0, 210, 3.0, 6000.0, 95)
        )
        val rainySummary = WeatherFormatters.computeExpectedRainyPeriods(
            hourly,
            TimeFormatPreference.HOUR_24,
            EnglishStrings
        )
        assertTrue(rainySummary.contains("14:00"))
        assertTrue(rainySummary.contains("15:00"))
    }

    @Test
    fun `trilingual weather descriptions work in English, Sinhala, and Tamil`() {
        assertEquals("Thunderstorm", EnglishStrings.weatherDescription(95, true))
        assertEquals("ගිගුරුම් සහිත වැසි", SinhalaStrings.weatherDescription(95, true))
        assertEquals("இடியுடன் கூடிய மழை", TamilStrings.weatherDescription(95, true))
    }

    @Test
    fun `gdacs parser filters strictly to Sri Lanka weather events without fake warnings`() {
        val service = OfficialAlertsService(OkHttpClient())
        val xml = """
            <rss version="2.0" xmlns:gdacs="http://www.gdacs.org">
              <channel>
                <item>
                  <title>Tropical Cyclone Watch for Sri Lanka</title>
                  <description>Heavy rain and strong winds expected over coastal Sri Lanka.</description>
                  <pubDate>Sat, 21 Nov 2026 06:00:00 GMT</pubDate>
                  <link>https://www.gdacs.org/report.aspx</link>
                  <gdacs:country>Sri Lanka</gdacs:country>
                  <gdacs:alertlevel>Orange</gdacs:alertlevel>
                  <gdacs:eventtype>TC</gdacs:eventtype>
                </item>
                <item>
                  <title>Earthquake in Pacific</title>
                  <description>Unrelated seismic event.</description>
                  <gdacs:country>Japan</gdacs:country>
                  <gdacs:alertlevel>Green</gdacs:alertlevel>
                  <gdacs:eventtype>EQ</gdacs:eventtype>
                </item>
              </channel>
            </rss>
        """.trimIndent()
        val parsed = service.parseGdacsRss(xml)
        assertEquals(1, parsed.size)
        assertEquals("Tropical Cyclone Watch for Sri Lanka", parsed.first().title)
    }

    @Test
    fun `repository caches weather and falls back to cached data on network failure`() = runBlocking {
        var shouldFail = false
        val repo = createFakeRepository(shouldFailProvider = { shouldFail })
        val kandy = SriLankaLocations.findById("kandy")!!

        // First call succeeds and caches in Room
        val liveResult = repo.fetchWeather(kandy, forceRefresh = true)
        assertTrue(liveResult.isSuccess)
        assertFalse(liveResult.getOrThrow().isFromCache)
        assertEquals(24, liveResult.getOrThrow().hourly.size)
        assertEquals(7, liveResult.getOrThrow().daily.size)

        // Second call fails network -> returns cached data marked isFromCache = true
        shouldFail = true
        val offlineResult = repo.fetchWeather(kandy, forceRefresh = true)
        assertTrue(offlineResult.isSuccess)
        assertTrue(offlineResult.getOrThrow().isFromCache)
        assertEquals("Kandy", offlineResult.getOrThrow().locationName)
    }

    @Test
    fun `navigation, district selection, search, language switch, and theme switch work in UI`() {
        val repo = createFakeRepository(shouldFailProvider = { false })
        val vm = MainViewModel(repo, prefsRepo)

        composeRule.setContent {
            LankaWeatherApp(viewModel = vm)
        }
        composeRule.waitForIdle()

        // Wait for async weather loading from IO coroutine
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodesWithTag("location_selector_button").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.waitForIdle()

        // Home screen displays Colombo and current weather
        composeRule.onNodeWithTag("location_selector_button").assertIsDisplayed()
        composeRule.onAllNodesWithText("Colombo", substring = true).onFirst().assertIsDisplayed()

        // Navigate across all 5 tabs
        composeRule.onNodeWithTag("nav_tab_forecast").performClick()
        composeRule.waitForIdle()
        composeRule.onAllNodesWithText("Weekly Outlook").onFirst().assertIsDisplayed()

        composeRule.onNodeWithTag("nav_tab_map").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Sri Lanka Weather Map").assertIsDisplayed()

        composeRule.onNodeWithTag("nav_tab_alerts").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Official Sri Lanka Warnings").assertIsDisplayed()

        composeRule.onNodeWithTag("nav_tab_settings").performClick()
        composeRule.waitForIdle()

        // Switch language to Sinhala and verify translated UI
        composeRule.onNodeWithTag("lang_chip_si").performClick()
        composeRule.waitUntil(timeoutMillis = 3000) {
            composeRule.onAllNodesWithText("සැකසුම්").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onAllNodesWithText("සැකසුම්").onFirst().assertIsDisplayed()

        // Switch theme to Dark (scroll LazyColumn first)
        composeRule.onNodeWithTag("settings_list").performScrollToNode(hasTestTag("theme_DARK"))
        composeRule.onNodeWithTag("theme_DARK").performClick()
        composeRule.waitForIdle()

        // Scroll back up to Language section and switch back to English
        composeRule.onNodeWithTag("settings_list").performScrollToNode(hasTestTag("lang_chip_en"))
        composeRule.onNodeWithTag("lang_chip_en").performClick()
        composeRule.waitUntil(timeoutMillis = 3000) {
            composeRule.onAllNodesWithText("Settings").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onAllNodesWithText("Settings").onFirst().assertIsDisplayed()

        // Navigate Home and pick another district (Jaffna) via search
        composeRule.onNodeWithTag("nav_tab_home").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("location_selector_button").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("location_search_input").performTextReplacement("Jaffna")
        composeRule.waitUntil(timeoutMillis = 3000) {
            composeRule.onAllNodesWithTag("location_row_jaffna").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("location_row_jaffna").performClick()
        composeRule.waitUntil(timeoutMillis = 5000) {
            composeRule.onAllNodes(hasTestTag("location_selector_button") and hasText("Jaffna", substring = true))
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onAllNodesWithText("Jaffna", substring = true).onFirst().assertIsDisplayed()
    }

    @Test
    @Config(qualifiers = "w840dp-h900dp")
    fun `tablet expanded screen size renders NavigationRail cleanly`() {
        val repo = createFakeRepository(shouldFailProvider = { false })
        val vm = MainViewModel(repo, prefsRepo)

        composeRule.setContent {
            LankaWeatherApp(viewModel = vm)
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("nav_tab_home").assertIsDisplayed()
        composeRule.onNodeWithTag("nav_tab_forecast").assertIsDisplayed()
        composeRule.onNodeWithTag("nav_tab_map").assertIsDisplayed()
        composeRule.onNodeWithTag("nav_tab_alerts").assertIsDisplayed()
        composeRule.onNodeWithTag("nav_tab_settings").assertIsDisplayed()
    }

    private fun createFakeRepository(shouldFailProvider: () -> Boolean): WeatherRepository {
        val nowHour = "2026-11-21T10:00"
        val hourlyTimes = (0 until 24).map { h ->
            String.format("2026-11-21T%02d:00", h)
        }
        val dailyDates = (0 until 7).map { d ->
            String.format("2026-11-%02d", 21 + d)
        }

        val fakeForecastApi = object : OpenMeteoForecastApi {
            override suspend fun getForecast(
                latitude: Double,
                longitude: Double,
                timezone: String,
                forecastDays: Int,
                current: String,
                hourly: String,
                daily: String
            ): OpenMeteoForecastResponse {
                if (shouldFailProvider()) throw IOException("Simulated offline error")
                return OpenMeteoForecastResponse(
                    latitude = latitude,
                    longitude = longitude,
                    timezone = timezone,
                    current = CurrentDto(
                        time = nowHour,
                        temperature2m = 29.4,
                        relativeHumidity2m = 78,
                        apparentTemperature = 33.1,
                        isDay = 1,
                        precipitation = 1.2,
                        rain = 1.2,
                        weatherCode = 80,
                        cloudCover = 55,
                        surfacePressure = 1011.0,
                        windSpeed10m = 14.5,
                        windDirection10m = 225
                    ),
                    hourly = HourlyDto(
                        time = hourlyTimes,
                        temperature2m = hourlyTimes.mapIndexed { i, _ -> 26.0 + (i % 6) },
                        relativeHumidity2m = hourlyTimes.map { 78 },
                        apparentTemperature = hourlyTimes.map { 31.0 },
                        precipitationProbability = hourlyTimes.map { 60 },
                        precipitation = hourlyTimes.map { 1.4 },
                        rain = hourlyTimes.map { 1.4 },
                        weatherCode = hourlyTimes.map { 80 },
                        cloudCover = hourlyTimes.map { 60 },
                        visibility = hourlyTimes.map { 9500.0 },
                        windSpeed10m = hourlyTimes.map { 12.0 },
                        windDirection10m = hourlyTimes.map { 220 },
                        uvIndex = hourlyTimes.map { 6.5 }
                    ),
                    daily = DailyDto(
                        time = dailyDates,
                        weatherCode = dailyDates.map { 80 },
                        temperature2mMax = dailyDates.map { 31.5 },
                        temperature2mMin = dailyDates.map { 24.8 },
                        sunrise = dailyDates.map { "${it}T06:02" },
                        sunset = dailyDates.map { "${it}T17:52" },
                        uvIndexMax = dailyDates.map { 8.5 },
                        precipitationSum = dailyDates.map { 14.2 },
                        rainSum = dailyDates.map { 14.2 },
                        precipitationProbabilityMax = dailyDates.map { 75 },
                        windSpeed10mMax = dailyDates.map { 19.0 },
                        windDirection10mDominant = dailyDates.map { 225 }
                    )
                )
            }

            override suspend fun getBatchDistrictSnapshots(
                latitudesCommaSeparated: String,
                longitudesCommaSeparated: String,
                timezone: String,
                forecastDays: Int,
                current: String,
                daily: String
            ): List<OpenMeteoForecastResponse> {
                return SriLankaLocations.allDistricts.map { loc ->
                    OpenMeteoForecastResponse(
                        latitude = loc.latitude,
                        longitude = loc.longitude,
                        current = CurrentDto(
                            time = nowHour,
                            temperature2m = 28.0,
                            relativeHumidity2m = 76,
                            isDay = 1,
                            precipitation = 0.5,
                            weatherCode = 2,
                            windSpeed10m = 12.0
                        ),
                        daily = DailyDto(precipitationProbabilityMax = listOf(45))
                    )
                }
            }
        }

        val fakeAqiApi = object : OpenMeteoAirQualityApi {
            override suspend fun getAirQuality(
                latitude: Double,
                longitude: Double,
                timezone: String,
                current: String
            ): AirQualityResponse = AirQualityResponse(
                latitude = latitude,
                longitude = longitude,
                current = AirQualityCurrentDto(
                    europeanAqi = 18,
                    usAqi = 32,
                    pm10 = 14.0,
                    pm25 = 7.5,
                    ozone = 42.0,
                    nitrogenDioxide = 9.0
                )
            )
        }

        val fakeGeocodingApi = object : OpenMeteoGeocodingApi {
            override suspend fun searchLocations(
                query: String,
                count: Int,
                language: String,
                format: String
            ): GeocodingResponse = GeocodingResponse(
                results = listOf(
                    GeocodingResultDto(
                        id = 123L,
                        name = "Jaffna",
                        latitude = 9.6615,
                        longitude = 80.0255,
                        countryCode = "LK",
                        admin1 = "Northern Province",
                        admin2 = "Jaffna"
                    )
                )
            )
        }

        val fakeRainViewerApi = object : RainViewerApi {
            override suspend fun getWeatherMaps(): RainViewerResponse = RainViewerResponse(
                host = "https://tilecache.rainviewer.com",
                radar = RainViewerRadarDto(
                    past = listOf(RainViewerFrameDto(1700000000L, "/v2/radar/1700000000")),
                    nowcast = emptyList()
                )
            )
        }

        val fakeAlertsService = object : OfficialAlertsService(OkHttpClient()) {
            override suspend fun fetchOfficialAlerts(): AlertsResult =
                AlertsResult.NoActiveAlerts("Test Feed", System.currentTimeMillis())
        }

        val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

        return WeatherRepository(
            weatherDao = db.weatherDao(),
            forecastApi = fakeForecastApi,
            airQualityApi = fakeAqiApi,
            geocodingApi = fakeGeocodingApi,
            rainViewerApi = fakeRainViewerApi,
            alertsService = fakeAlertsService,
            moshi = moshi
        )
    }
}
