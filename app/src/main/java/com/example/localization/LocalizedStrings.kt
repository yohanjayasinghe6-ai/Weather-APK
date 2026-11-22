package com.example.localization

import androidx.compose.runtime.staticCompositionLocalOf
import com.example.model.AlertCategory
import com.example.model.AlertSeverity
import com.example.model.MonsoonSeason

data class AppStrings(
    val appName: String,
    val navHome: String,
    val navForecast: String,
    val navMap: String,
    val navAlerts: String,
    val navSettings: String,
    val refresh: String,
    val retry: String,
    val close: String,
    val cancel: String,
    val save: String,
    val feelsLike: String,
    val highLowFormat: String,
    val lastUpdated: String,
    val cachedDataBanner: String,
    val loadingWeather: String,
    val errorLoadingWeather: String,
    val humidity: String,
    val wind: String,
    val pressure: String,
    val visibility: String,
    val cloudCover: String,
    val uvIndex: String,
    val sunrise: String,
    val sunset: String,
    val daylightProgress: String,
    val hourlyForecast: String,
    val next24Hours: String,
    val forecastDisclaimer: String,
    val sevenDayForecast: String,
    val weeklyOutlook: String,
    val rainProbability: String,
    val expectedRainfall: String,
    val rainIntensity: String,
    val rainyPeriods: String,
    val dailyRainfall: String,
    val monsoonOutlook: String,
    val monsoonSouthwest: String,
    val monsoonNortheast: String,
    val monsoonFirstInter: String,
    val monsoonSecondInter: String,
    val monsoonSouthwestDesc: String,
    val monsoonNortheastDesc: String,
    val monsoonFirstInterDesc: String,
    val monsoonSecondInterDesc: String,
    val activeMonsoonSeason: String,
    val regionalImpact: String,
    val airQuality: String,
    val usAqi: String,
    val euAqi: String,
    val pm25: String,
    val pm10: String,
    val ozone: String,
    val no2: String,
    val airQualityUnavailable: String,
    val aqiGood: String,
    val aqiFair: String,
    val aqiModerate: String,
    val aqiPoor: String,
    val aqiVeryPoor: String,
    val uvLow: String,
    val uvModerate: String,
    val uvHigh: String,
    val uvVeryHigh: String,
    val uvExtreme: String,
    val rainNone: String,
    val rainLight: String,
    val rainModerate: String,
    val rainHeavy: String,
    val rainVeryHeavy: String,
    val noRainyPeriodsExpected: String,
    val selectLocation: String,
    val searchCitiesDistricts: String,
    val useCurrentLocation: String,
    val gpsLocating: String,
    val gpsPermissionRequired: String,
    val gpsDisabledMessage: String,
    val favoriteLocations: String,
    val all25Districts: String,
    val townsAndCities: String,
    val searchResults: String,
    val noMatchingLocations: String,
    val compareLocations: String,
    val addLocationsToCompare: String,
    val comparisonHint: String,
    val viewDashboard: String,
    val weatherMapTitle: String,
    val layerRadar: String,
    val layerTemp: String,
    val layerRain: String,
    val playRadar: String,
    val pauseRadar: String,
    val radarUnavailable: String,
    val radarLiveNote: String,
    val districtTapHint: String,
    val alertsTitle: String,
    val officialAlertsHeader: String,
    val officialSourceLabel: String,
    val sourceDeptMet: String,
    val publishedAtLabel: String,
    val affectedAreasLabel: String,
    val expiresAtLabel: String,
    val noOfficialAlertsTitle: String,
    val noOfficialAlertsDesc: String,
    val unavailableAlertsTitle: String,
    val unavailableAlertsDesc: String,
    val openOfficialMetPortal: String,
    val modelObservationsHeader: String,
    val modelObservationsDisclaimer: String,
    val noSignificantModelObs: String,
    val preparednessTitle: String,
    val preparednessFlood: String,
    val preparednessLightning: String,
    val preparednessWind: String,
    val settingsTitle: String,
    val languageLabel: String,
    val temperatureUnitLabel: String,
    val windSpeedUnitLabel: String,
    val timeFormatLabel: String,
    val themeModeLabel: String,
    val themeSystem: String,
    val themeLight: String,
    val themeDark: String,
    val time12Hour: String,
    val time24Hour: String,
    val locationPreferences: String,
    val defaultDistrict: String,
    val autoGpsSwitch: String,
    val refreshAndNotifications: String,
    val autoRefreshLaunch: String,
    val weatherNotifications: String,
    val severeAlertNotif: String,
    val rainNotif: String,
    val sendSampleNotif: String,
    val notificationPermissionNeeded: String,
    val notificationSentToast: String,
    val notificationChannelName: String,
    val notificationSummaryTitle: String,
    val dataSourcesTitle: String,
    val dataSourcesDesc: String,
    val aboutAppTitle: String,
    val aboutAppDesc: String,
    val forecastPredictionBadge: String,
    val dayDetailsTitle: String,
    val dominantWind: String,
    val maxUv: String,
    val totalRain: String,
    val todayLabel: String,
    val tomorrowLabel: String,
    val currentTimeZoneNote: String
) {
    fun weatherDescription(wmoCode: Int, isDay: Boolean): String = when (this) {
        EnglishStrings -> when (wmoCode) {
            0 -> if (isDay) "Clear sky" else "Clear night"
            1 -> "Mainly clear"
            2 -> "Partly cloudy"
            3 -> "Overcast"
            45, 48 -> "Mist and fog"
            51 -> "Light drizzle"
            53 -> "Moderate drizzle"
            55 -> "Dense drizzle"
            56, 57 -> "Cold drizzle"
            61 -> "Light rain"
            63 -> "Moderate rain"
            65 -> "Heavy rain"
            66, 67 -> "Cold heavy rain"
            71, 73, 75, 77 -> "Precipitation"
            80 -> "Slight rain showers"
            81 -> "Moderate rain showers"
            82 -> "Violent rain showers"
            85, 86 -> "Heavy showers"
            95 -> "Thunderstorm"
            96, 99 -> "Severe thunderstorm"
            else -> "Variable conditions"
        }
        SinhalaStrings -> when (wmoCode) {
            0 -> if (isDay) "පැහැදිලි අහස" else "පැහැදිලි රාත්‍රිය"
            1 -> "බොහෝ දුරට පැහැදිලි"
            2 -> "අර්ධ වශයෙන් වලාකුළු සහිත"
            3 -> "වලාකුළු බර අහස"
            45, 48 -> "මීදුම සහිත"
            51 -> "සිහින් පොද වැස්ස"
            53 -> "මධ්‍යම පොද වැස්ස"
            55 -> "තද පොද වැස්ස"
            56, 57 -> "සීතල පොද වැස්ස"
            61 -> "සුළු වැසි"
            63 -> "මධ්‍යම වැසි"
            65 -> "තද වැසි"
            66, 67 -> "සීතල තද වැසි"
            71, 73, 75, 77 -> "වර්ෂාපතනය"
            80 -> "සුළු වැසි වාර"
            81 -> "මධ්‍යම වැසි වාර"
            82 -> "තද වැසි වාර"
            85, 86 -> "දැඩි වැසි වාර"
            95 -> "ගිගුරුම් සහිත වැසි"
            96, 99 -> "තද ගිගුරුම් සහිත වැසි"
            else -> "වෙනස්වන කාලගුණය"
        }
        TamilStrings -> when (wmoCode) {
            0 -> if (isDay) "தெளிவான வானம்" else "தெளிவான இரவு"
            1 -> "பெரும்பாலும் தெளிவு"
            2 -> "பகுதி மேகமூட்டம்"
            3 -> "மேகமூட்டம்"
            45, 48 -> "பனிமூட்டம்"
            51 -> "லேசான தூறல்"
            53 -> "மிதமான தூறல்"
            55 -> "அடர்த்தியான தூறல்"
            56, 57 -> "குளிர் தூறல்"
            61 -> "லேசான மழை"
            63 -> "மிதமான மழை"
            65 -> "கனமழை"
            66, 67 -> "குளிர் கனமழை"
            71, 73, 75, 77 -> "மழைப்பொழிவு"
            80 -> "லேசான மழைத் தூறல்"
            81 -> "மிதமான மழை"
            82 -> "கடுமையான மழை"
            85, 86 -> "பலத்த மழை"
            95 -> "இடியுடன் கூடிய மழை"
            96, 99 -> "கடுமையான இடி மழை"
            else -> "மாறுபடும் வானிலை"
        }
        else -> "Variable conditions"
    }

    fun compassDirection(deg: Int): String {
        val dirsEn = listOf("N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW")
        val dirsSi = listOf("උ", "උඋනැ", "උනැ", "නැඋනැ", "නැ", "නැදනැ", "දනැ", "දදනැ", "ද", "දදබ", "දබ", "බදබ", "බ", "බඋබ", "උබ", "උඋබ")
        val dirsTa = listOf("வ", "வவகி", "வகி", "கிவகி", "கி", "கிதெகி", "தெகி", "தெதெகி", "தெ", "தெதெமே", "தெமே", "மேதெமே", "மே", "மேவமே", "வமே", "வவமே")
        val index = (((deg % 360) + 360) % 360 / 22.5 + 0.5).toInt() % 16
        return when (this) {
            SinhalaStrings -> dirsSi[index]
            TamilStrings -> dirsTa[index]
            else -> dirsEn[index]
        }
    }

    fun monsoonTitle(season: MonsoonSeason): String = when (season) {
        MonsoonSeason.SOUTHWEST_MONSOON -> monsoonSouthwest
        MonsoonSeason.NORTHEAST_MONSOON -> monsoonNortheast
        MonsoonSeason.FIRST_INTER_MONSOON -> monsoonFirstInter
        MonsoonSeason.SECOND_INTER_MONSOON -> monsoonSecondInter
    }

    fun monsoonDescription(season: MonsoonSeason): String = when (season) {
        MonsoonSeason.SOUTHWEST_MONSOON -> monsoonSouthwestDesc
        MonsoonSeason.NORTHEAST_MONSOON -> monsoonNortheastDesc
        MonsoonSeason.FIRST_INTER_MONSOON -> monsoonFirstInterDesc
        MonsoonSeason.SECOND_INTER_MONSOON -> monsoonSecondInterDesc
    }

    fun severityLabel(severity: AlertSeverity): String = when (this) {
        SinhalaStrings -> when (severity) {
            AlertSeverity.EXTREME -> "අතිශය දරුණු"
            AlertSeverity.SEVERE -> "තද අවවාදය"
            AlertSeverity.MODERATE -> "මධ්‍යම"
            AlertSeverity.ADVISORY -> "උපදේශනය"
        }
        TamilStrings -> when (severity) {
            AlertSeverity.EXTREME -> "மிகக் கடுமையான"
            AlertSeverity.SEVERE -> "கடுமையான"
            AlertSeverity.MODERATE -> "மிதமான"
            AlertSeverity.ADVISORY -> "அறிவுறுத்தல்"
        }
        else -> when (severity) {
            AlertSeverity.EXTREME -> "Extreme"
            AlertSeverity.SEVERE -> "Severe"
            AlertSeverity.MODERATE -> "Moderate"
            AlertSeverity.ADVISORY -> "Advisory"
        }
    }

    fun alertCategoryLabel(category: AlertCategory): String = when (this) {
        SinhalaStrings -> when (category) {
            AlertCategory.HEAVY_RAIN -> "තද වැසි"
            AlertCategory.FLOOD -> "ගංවතුර අවදානම"
            AlertCategory.STRONG_WIND -> "තද සුළං"
            AlertCategory.THUNDERSTORM -> "ගිගුරුම් සහිත වැසි"
            AlertCategory.CYCLONE -> "සුළි කුණාටු"
            AlertCategory.SEVERE_WEATHER -> "දරුණු කාලගුණය"
        }
        TamilStrings -> when (category) {
            AlertCategory.HEAVY_RAIN -> "கனமழை"
            AlertCategory.FLOOD -> "வெள்ள எச்சரிக்கை"
            AlertCategory.STRONG_WIND -> "பலத்த காற்று"
            AlertCategory.THUNDERSTORM -> "இடி மின்னல்"
            AlertCategory.CYCLONE -> "புயல்"
            AlertCategory.SEVERE_WEATHER -> "கடுமையான வானிலை"
        }
        else -> when (category) {
            AlertCategory.HEAVY_RAIN -> "Heavy Rainfall"
            AlertCategory.FLOOD -> "Flood Advisory"
            AlertCategory.STRONG_WIND -> "Strong Winds"
            AlertCategory.THUNDERSTORM -> "Thunderstorm"
            AlertCategory.CYCLONE -> "Cyclone Watch"
            AlertCategory.SEVERE_WEATHER -> "Severe Weather"
        }
    }
}

val EnglishStrings = AppStrings(
    appName = "Lanka Weather",
    navHome = "Home",
    navForecast = "Forecast",
    navMap = "Map",
    navAlerts = "Alerts",
    navSettings = "Settings",
    refresh = "Refresh",
    retry = "Retry",
    close = "Close",
    cancel = "Cancel",
    save = "Save",
    feelsLike = "Feels like",
    highLowFormat = "H: %s  L: %s",
    lastUpdated = "Updated %s",
    cachedDataBanner = "Offline / Cached data shown • Last updated %s",
    loadingWeather = "Fetching live Sri Lanka weather…",
    errorLoadingWeather = "Could not retrieve live weather data",
    humidity = "Humidity",
    wind = "Wind",
    pressure = "Pressure",
    visibility = "Visibility",
    cloudCover = "Cloud Cover",
    uvIndex = "UV Index",
    sunrise = "Sunrise",
    sunset = "Sunset",
    daylightProgress = "Daylight",
    hourlyForecast = "Hourly Forecast",
    next24Hours = "Next 24 Hours",
    forecastDisclaimer = "Forecasts are meteorological predictions (Open-Meteo), not guaranteed conditions.",
    sevenDayForecast = "7-Day Forecast",
    weeklyOutlook = "Weekly Outlook",
    rainProbability = "Rain Prob.",
    expectedRainfall = "Expected Rain",
    rainIntensity = "Rain Intensity",
    rainyPeriods = "Expected Rainy Periods",
    dailyRainfall = "Daily Rainfall & Monsoon",
    monsoonOutlook = "Sri Lanka Monsoon Season",
    monsoonSouthwest = "Southwest Monsoon (May – Sep)",
    monsoonNortheast = "Northeast Monsoon (Dec – Feb)",
    monsoonFirstInter = "First Inter-Monsoon (Mar – Apr)",
    monsoonSecondInter = "Second Inter-Monsoon (Oct – Nov)",
    monsoonSouthwestDesc = "Predominant winds from the southwest typically bring increased showers to Western, Sabaragamuwa, Central, and Southern wet-zone districts.",
    monsoonNortheastDesc = "Predominant northeasterly winds typically bring rainfall to Northern, Eastern, and North Central dry-zone districts.",
    monsoonFirstInterDesc = "Afternoon and evening convective thunderstorms commonly develop across southwestern and central regions.",
    monsoonSecondInterDesc = "Widespread evening thunderstorms and tropical depressions may affect multiple provinces island-wide.",
    activeMonsoonSeason = "Seasonal Pattern",
    regionalImpact = "Local Climate Zone",
    airQuality = "Air Quality",
    usAqi = "US AQI",
    euAqi = "EU AQI",
    pm25 = "PM2.5",
    pm10 = "PM10",
    ozone = "O₃",
    no2 = "NO₂",
    airQualityUnavailable = "Air quality metrics currently unavailable",
    aqiGood = "Good",
    aqiFair = "Fair",
    aqiModerate = "Moderate",
    aqiPoor = "Poor",
    aqiVeryPoor = "Very Poor",
    uvLow = "Low",
    uvModerate = "Moderate",
    uvHigh = "High",
    uvVeryHigh = "Very High",
    uvExtreme = "Extreme",
    rainNone = "Dry / Trace",
    rainLight = "Light",
    rainModerate = "Moderate",
    rainHeavy = "Heavy",
    rainVeryHeavy = "Very Heavy",
    noRainyPeriodsExpected = "No significant rainy periods expected in the next 24 hours",
    selectLocation = "Select Sri Lanka Location",
    searchCitiesDistricts = "Search any city, town, or district…",
    useCurrentLocation = "Detect My Location (GPS)",
    gpsLocating = "Detecting location…",
    gpsPermissionRequired = "Location permission is optional. Allow coarse location to detect your nearest Sri Lankan district.",
    gpsDisabledMessage = "Location unavailable. Select any district manually below.",
    favoriteLocations = "Saved Favorites",
    all25Districts = "All 25 Administrative Districts",
    townsAndCities = "Popular Towns & Cities",
    searchResults = "Search Results",
    noMatchingLocations = "No matching Sri Lankan locations found",
    compareLocations = "Compare Districts",
    addLocationsToCompare = "Star locations in the location picker to compare live weather side-by-side.",
    comparisonHint = "Live comparison across saved districts",
    viewDashboard = "Open Dashboard",
    weatherMapTitle = "Sri Lanka Weather Map",
    layerRadar = "Rain Radar",
    layerTemp = "Temperature",
    layerRain = "Rainfall",
    playRadar = "Play Radar",
    pauseRadar = "Pause",
    radarUnavailable = "Live radar tiles unavailable offline",
    radarLiveNote = "Live RainViewer precipitation radar over OpenStreetMap + 25 district stations",
    districtTapHint = "Tap any district marker to inspect live conditions",
    alertsTitle = "Weather Alerts & Advisories",
    officialAlertsHeader = "Official Sri Lanka Warnings",
    officialSourceLabel = "Source",
    sourceDeptMet = "Sri Lanka Dept. of Meteorology / GDACS",
    publishedAtLabel = "Published",
    affectedAreasLabel = "Affected Areas",
    expiresAtLabel = "Valid Until",
    noOfficialAlertsTitle = "No Active Official Warnings Found",
    noOfficialAlertsDesc = "No verified severe weather bulletins affecting Sri Lanka were reported in the retrieved official feed. Always verify critical travel plans with the Department of Meteorology.",
    unavailableAlertsTitle = "Verified Official Alerts Unavailable",
    unavailableAlertsDesc = "Could not retrieve a live official bulletin feed from the Sri Lanka Department of Meteorology right now. No warnings are fabricated.",
    openOfficialMetPortal = "Open meteo.gov.lk",
    modelObservationsHeader = "Forecast Model Observations",
    modelObservationsDisclaimer = "Derived from Open-Meteo numerical predictions for the selected location — NOT an official government warning.",
    noSignificantModelObs = "No elevated rain, wind, or UV thresholds detected in today's numerical forecast.",
    preparednessTitle = "Sri Lanka Weather Preparedness",
    preparednessFlood = "Flood & Landslide Caution: Avoid flooded roads and steep hillside embankments during sustained heavy monsoon rain.",
    preparednessLightning = "Lightning Safety: Seek enclosed shelter during inter-monsoon afternoon thunderstorms; unplug sensitive electronics.",
    preparednessWind = "Coastal & Marine Wind: Small craft and coastal commuters should check official Met Dept. sea-area bulletins before departure.",
    settingsTitle = "Settings",
    languageLabel = "Language",
    temperatureUnitLabel = "Temperature Unit",
    windSpeedUnitLabel = "Wind Speed Unit",
    timeFormatLabel = "Time Format",
    themeModeLabel = "Appearance",
    themeSystem = "System",
    themeLight = "Light",
    themeDark = "Dark",
    time12Hour = "12-hour",
    time24Hour = "24-hour",
    locationPreferences = "Location Preferences",
    defaultDistrict = "Active District",
    autoGpsSwitch = "Prefer GPS Detection on Launch",
    refreshAndNotifications = "Refresh & Notifications",
    autoRefreshLaunch = "Auto-refresh Weather on Launch",
    weatherNotifications = "Weather Notifications",
    severeAlertNotif = "Heavy Rain & Storm Advisories",
    rainNotif = "Daily Rain Outlook",
    sendSampleNotif = "Test Localized Notification",
    notificationPermissionNeeded = "Allow notification permission to receive weather updates",
    notificationSentToast = "Weather notification sent",
    notificationChannelName = "Lanka Weather Advisories",
    notificationSummaryTitle = "%s Weather",
    dataSourcesTitle = "Weather Data Sources",
    dataSourcesDesc = "Live forecasts & air quality: Open-Meteo (ECMWF/ICON). Precipitation radar: RainViewer. Map tiles: OpenStreetMap. Official bulletins: Sri Lanka Department of Meteorology (meteo.gov.lk) & GDACS.",
    aboutAppTitle = "About Lanka Weather",
    aboutAppDesc = "Lanka Weather v1.0 • Built for all 25 districts of Sri Lanka. Forecasts are model predictions; consult the Sri Lanka Department of Meteorology for official warnings.",
    forecastPredictionBadge = "Prediction",
    dayDetailsTitle = "Daily Forecast Details",
    dominantWind = "Dominant Wind",
    maxUv = "Max UV Index",
    totalRain = "24h Rainfall",
    todayLabel = "Today",
    tomorrowLabel = "Tomorrow",
    currentTimeZoneNote = "Sri Lanka Time (IST, UTC+5:30)"
)

val SinhalaStrings = AppStrings(
    appName = "ලංකා කාලගුණය",
    navHome = "මුල් පිටුව",
    navForecast = "පුරෝකථනය",
    navMap = "සිතියම",
    navAlerts = "අවවාද",
    navSettings = "සැකසුම්",
    refresh = "යාවත්කාලීන කරන්න",
    retry = "නැවත උත්සාහ කරන්න",
    close = "වසන්න",
    cancel = "අවලංගු කරන්න",
    save = "සුරකින්න",
    feelsLike = "දැනෙන උෂ්ණත්වය",
    highLowFormat = "උපරිම: %s  අවම: %s",
    lastUpdated = "යාවත්කාලීන වූයේ %s",
    cachedDataBanner = "අන්තර්ජාලය නොමැත / සුරැකි දත්ත • අවසන් යාවත්කාලීනය %s",
    loadingWeather = "ශ්‍රී ලංකා සජීවී කාලගුණ දත්ත ලබා ගනිමින්…",
    errorLoadingWeather = "සජීවී කාලගුණ දත්ත ලබා ගැනීමට නොහැකි විය",
    humidity = "ආර්ද්‍රතාවය",
    wind = "සුළඟ",
    pressure = "වායු පීඩනය",
    visibility = "දෘශ්‍යතාව",
    cloudCover = "වලාකුළු ආවරණය",
    uvIndex = "පාරජම්බුල දර්ශකය (UV)",
    sunrise = "හිරු උදාව",
    sunset = "හිරු බැසීම",
    daylightProgress = "දිවා කාලය",
    hourlyForecast = "පැයෙන් පැය පුරෝකථනය",
    next24Hours = "ඉදිරි පැය 24",
    forecastDisclaimer = "මෙම පුරෝකථන කාලගුණ ආකෘති (Open-Meteo) මත පදනම් වූ අනාවැකි වේ.",
    sevenDayForecast = "දින 7 කාලගුණ පුරෝකථනය",
    weeklyOutlook = "සතියේ දැක්ම",
    rainProbability = "වැසි සම්භාවිතාව",
    expectedRainfall = "අපේක්ෂිත වර්ෂාපතනය",
    rainIntensity = "වැසි තීව්‍රතාව",
    rainyPeriods = "අපේක්ෂිත වැසි කාලසීමා",
    dailyRainfall = "දෛනික වර්ෂාපතනය හා මෝසම",
    monsoonOutlook = "ශ්‍රී ලංකා මෝසම් කාලය",
    monsoonSouthwest = "නිරිතදිග මෝසම (මැයි – සැප්)",
    monsoonNortheast = "ඊසානදිග මෝසම (දෙසැ – පෙබ)",
    monsoonFirstInter = "පළමු අන්තර් මෝසම (මාර් – අප්‍රේ)",
    monsoonSecondInter = "දෙවන අන්තර් මෝසම (ඔක් – නොවැ)",
    monsoonSouthwestDesc = "නිරිතදිග සුළං හේතුවෙන් බස්නාහිර, සබරගමුව, මධ්‍යම සහ දකුණු තෙත් කලාපීය දිස්ත්‍රික්කවලට වැසි ඇතිවිය හැක.",
    monsoonNortheastDesc = "ඊසානදිග සුළං හේතුවෙන් උතුරු, නැගෙනහිර සහ උතුරු මැද වියළි කලාපීය දිස්ත්‍රික්කවලට වැසි ඇතිවිය හැක.",
    monsoonFirstInterDesc = "සවස් කාලයේදී සහ රාත්‍රී කාලයේදී ගිගුරුම් සහිත වැසි නිරිතදිග හා මධ්‍යම ප්‍රදේශවල ඇතිවිය හැක.",
    monsoonSecondInterDesc = "දිවයිනේ බොහෝ පළාත්වල සවස් කාලයේ ගිගුරුම් සහිත වැසි ඇතිවීමේ ප්‍රවණතාවක් පවතී.",
    activeMonsoonSeason = "සෘතුමය රටාව",
    regionalImpact = "ප්‍රාදේශීය කලාපය",
    airQuality = "වායු ගුණාත්මකභාවය",
    usAqi = "US AQI",
    euAqi = "EU AQI",
    pm25 = "PM2.5",
    pm10 = "PM10",
    ozone = "O₃",
    no2 = "NO₂",
    airQualityUnavailable = "වායු ගුණාත්මක දත්ත දැනට නොමැත",
    aqiGood = "හොඳයි",
    aqiFair = "සාධාරණයි",
    aqiModerate = "මධ්‍යම",
    aqiPoor = "දුර්වලයි",
    aqiVeryPoor = "ඉතා දුර්වලයි",
    uvLow = "අඩු",
    uvModerate = "මධ්‍යම",
    uvHigh = "ඉහළ",
    uvVeryHigh = "ඉතා ඉහළ",
    uvExtreme = "අධික",
    rainNone = "වැසි රහිත / සුළු",
    rainLight = "සැහැල්ලු",
    rainModerate = "මධ්‍යම",
    rainHeavy = "තද",
    rainVeryHeavy = "ඉතා තද",
    noRainyPeriodsExpected = "ඉදිරි පැය 24 තුළ සැලකිය යුතු වැසි කාලසීමා අපේක්ෂා නොකෙරේ",
    selectLocation = "ශ්‍රී ලංකාවේ ස්ථානයක් තෝරන්න",
    searchCitiesDistricts = "නගරයක් හෝ දිස්ත්‍රික්කයක් සොයන්න…",
    useCurrentLocation = "මගේ ස්ථානය සොයන්න (GPS)",
    gpsLocating = "ස්ථානය සොයමින්…",
    gpsPermissionRequired = "ස්ථාන අවසරය විකල්ප වේ. ආසන්න දිස්ත්‍රික්කය හඳුනා ගැනීමට අවසර දෙන්න.",
    gpsDisabledMessage = "ස්ථානය ලබාගත නොහැක. පහතින් දිස්ත්‍රික්කයක් තෝරන්න.",
    favoriteLocations = "ප්‍රියතම ස්ථාන",
    all25Districts = "දිස්ත්‍රික්ක 25ම",
    townsAndCities = "ප්‍රධාන නගර",
    searchResults = "සෙවුම් ප්‍රතිඵල",
    noMatchingLocations = "ගැලපෙන ස්ථාන හමු නොවීය",
    compareLocations = "දිස්ත්‍රික්ක සංසන්දනය",
    addLocationsToCompare = "සජීවී කාලගුණය සංසන්දනය කිරීමට ප්‍රියතම ස්ථාන එකතු කරන්න.",
    comparisonHint = "සුරැකි දිස්ත්‍රික්ක සංසන්දනය",
    viewDashboard = "ප්‍රධාන පුවරුව",
    weatherMapTitle = "ශ්‍රී ලංකා කාලගුණ සිතියම",
    layerRadar = "වැසි රේඩාර්",
    layerTemp = "උෂ්ණත්වය",
    layerRain = "වර්ෂාපතනය",
    playRadar = "රේඩාර් ධාවනය",
    pauseRadar = "නවතන්න",
    radarUnavailable = "අන්තර්ජාලය නොමැති විට සජීවී රේඩාර් දත්ත නොමැත",
    radarLiveNote = "RainViewer සජීවී වර්ෂා රේඩාර් සහ දිස්ත්‍රික්ක 25 මධ්‍යස්ථාන",
    districtTapHint = "විස්තර බැලීමට දිස්ත්‍රික්ක සලකුණක් මත තට්ටු කරන්න",
    alertsTitle = "කාලගුණ අවවාද සහ නිවේදන",
    officialAlertsHeader = "නිල ශ්‍රී ලංකා අවවාද",
    officialSourceLabel = "මූලාශ්‍රය",
    sourceDeptMet = "කාලගුණ විද්‍යා දෙපාර්තමේන්තුව / GDACS",
    publishedAtLabel = "නිකුත් කළ වේලාව",
    affectedAreasLabel = "බලපෑමට ලක්වන ප්‍රදේශ",
    expiresAtLabel = "වලංගු කාලය",
    noOfficialAlertsTitle = "ක්‍රියාත්මක නිල අවවාද හමු නොවීය",
    noOfficialAlertsDesc = "ලබාගත් නිල දත්ත අනුව ශ්‍රී ලංකාව සඳහා දරුණු කාලගුණ අවවාද වාර්තා වී නොමැත. කාලගුණ විද්‍යා දෙපාර්තමේන්තුවෙන් තහවුරු කර ගන්න.",
    unavailableAlertsTitle = "තහවුරු කළ නිල අවවාද ලබාගත නොහැක",
    unavailableAlertsDesc = "කාලගුණ විද්‍යා දෙපාර්තමේන්තුවේ සජීවී නිවේදන පද්ධතිය වෙත දැනට සම්බන්ධ විය නොහැක. ව්‍යාජ අවවාද ප්‍රදර්ශනය නොකෙරේ.",
    openOfficialMetPortal = "meteo.gov.lk විවෘත කරන්න",
    modelObservationsHeader = "පුරෝකථන ආකෘති නිරීක්ෂණ",
    modelObservationsDisclaimer = "Open-Meteo සංඛ්‍යාත්මක පුරෝකථනයෙන් ලබාගත් දත්ත — මෙය නිල රජයේ අවවාදයක් නොවේ.",
    noSignificantModelObs = "අද පුරෝකථනයේ ඉහළ වැසි, සුළං හෝ UV මට්ටම් නිරීක්ෂණය වී නොමැත.",
    preparednessTitle = "කාලගුණ ආරක්ෂක උපදෙස්",
    preparednessFlood = "ගංවතුර සහ නායයෑම් අවදානම: තද මෝසම් වැසි කාලයේදී ජලයෙන් යටවූ මාර්ග සහ බෑවුම් සහිත ප්‍රදේශවලින් වළකින්න.",
    preparednessLightning = "අකුණු අනතුරු: සවස් කාලයේ ගිගුරුම් සහිත වැසි ඇති විට ආරක්ෂිත ගොඩනැගිල්ලක් තුළ රැඳී සිටින්න.",
    preparednessWind = "වෙරළ සහ මුහුදු සුළං: ධීවර සහ වෙරළ කටයුතු සඳහා නිල මුහුදු කාලගුණ නිවේදන පරීක්ෂා කරන්න.",
    settingsTitle = "සැකසුම්",
    languageLabel = "භාෂාව",
    temperatureUnitLabel = "උෂ්ණත්ව ඒකකය",
    windSpeedUnitLabel = "සුළං වේග ඒකකය",
    timeFormatLabel = "වේලා ආකෘතිය",
    themeModeLabel = "තේමාව",
    themeSystem = "පද්ධතිය",
    themeLight = "ආලෝක",
    themeDark = "අඳුරු",
    time12Hour = "පැය 12",
    time24Hour = "පැය 24",
    locationPreferences = "ස්ථාන සැකසුම්",
    defaultDistrict = "තෝරාගත් දිස්ත්‍රික්කය",
    autoGpsSwitch = "ආරම්භයේදී GPS භාවිතා කරන්න",
    refreshAndNotifications = "යාවත්කාලීන සහ දැනුම්දීම්",
    autoRefreshLaunch = "ආරම්භයේදී ස්වයංක්‍රීයව යාවත්කාලීන කරන්න",
    weatherNotifications = "කාලගුණ දැනුම්දීම්",
    severeAlertNotif = "තද වැසි සහ කුණාටු දැනුම්දීම්",
    rainNotif = "දෛනික වැසි දැක්ම",
    sendSampleNotif = "දැනුම්දීමක් පරීක්ෂා කරන්න",
    notificationPermissionNeeded = "දැනුම්දීම් ලබා ගැනීමට අවසර දෙන්න",
    notificationSentToast = "කාලගුණ දැනුම්දීම යවන ලදී",
    notificationChannelName = "ලංකා කාලගුණ දැනුම්දීම්",
    notificationSummaryTitle = "%s කාලගුණය",
    dataSourcesTitle = "දත්ත මූලාශ්‍ර",
    dataSourcesDesc = "සජීවී පුරෝකථන සහ වායු ගුණාත්මකභාවය: Open-Meteo. රේඩාර්: RainViewer. සිතියම්: OpenStreetMap. නිල නිවේදන: ශ්‍රී ලංකා කාලගුණ විද්‍යා දෙපාර්තමේන්තුව.",
    aboutAppTitle = "යෙදුම පිළිබඳව",
    aboutAppDesc = "ලංකා කාලගුණය v1.0 • ශ්‍රී ලංකාවේ දිස්ත්‍රික්ක 25 සඳහා නිර්මාණය කරන ලදී.",
    forecastPredictionBadge = "අනාවැකිය",
    dayDetailsTitle = "දෛනික පුරෝකථන විස්තර",
    dominantWind = "ප්‍රධාන සුළං දිශාව",
    maxUv = "උපරිම UV",
    totalRain = "පැය 24 වර්ෂාපතනය",
    todayLabel = "අද",
    tomorrowLabel = "හෙට",
    currentTimeZoneNote = "ශ්‍රී ලංකා වේලාව (IST, UTC+5:30)"
)

val TamilStrings = AppStrings(
    appName = "லங்கா வானிலை",
    navHome = "முகப்பு",
    navForecast = "முன்னறிவிப்பு",
    navMap = "வரைபடம்",
    navAlerts = "எச்சரிக்கை",
    navSettings = "அமைப்புகள்",
    refresh = "புதுப்பி",
    retry = "மீண்டும் முயற்சி",
    close = "மூடு",
    cancel = "ரத்து",
    save = "சேமி",
    feelsLike = "உணரும் வெப்பம்",
    highLowFormat = "உச்சம்: %s  குறைவு: %s",
    lastUpdated = "புதுப்பிப்பு %s",
    cachedDataBanner = "இணையம் இல்லை / சேமித்த தரவு • கடைசி புதுப்பிப்பு %s",
    loadingWeather = "இலங்கை நேரடி வானிலை பெறப்படுகிறது…",
    errorLoadingWeather = "நேரடி வானிலை தரவை பெற முடியவில்லை",
    humidity = "ஈரப்பதம்",
    wind = "காற்று",
    pressure = "காற்றழுத்தம்",
    visibility = "பார்வைத்தூரம்",
    cloudCover = "மேக மூட்டம்",
    uvIndex = "புற ஊதா குறியீடு (UV)",
    sunrise = "சூரிய உதயம்",
    sunset = "சூரிய அஸ்தமனம்",
    daylightProgress = "பகல் நேரம்",
    hourlyForecast = "மணிநேர முன்னறிவிப்பு",
    next24Hours = "அடுத்த 24 மணி நேரம்",
    forecastDisclaimer = "இந்த முன்னறிவிப்புகள் வானிலை மாதிரி (Open-Meteo) கணிப்புகள் ஆகும்.",
    sevenDayForecast = "7-நாள் வானிலை முன்னறிவிப்பு",
    weeklyOutlook = "வாராந்திர நிலவரம்",
    rainProbability = "மழை வாய்ப்பு",
    expectedRainfall = "எதிர்பார்க்கப்படும் மழை",
    rainIntensity = "மழையின் தீவிரம்",
    rainyPeriods = "எதிர்பார்க்கப்படும் மழை நேரங்கள்",
    dailyRainfall = "தினசரி மழை & பருவமழை",
    monsoonOutlook = "இலங்கை பருவமழை காலம்",
    monsoonSouthwest = "தென்மேற்கு பருவமழை (மே – செப்)",
    monsoonNortheast = "வடகிழக்கு பருவமழை (டிச – பிப்)",
    monsoonFirstInter = "முதல் இடைப்பருவம் (மார் – ஏப்)",
    monsoonSecondInter = "இரண்டாம் இடைப்பருவம் (அக் – நவ)",
    monsoonSouthwestDesc = "தென்மேற்கு காற்றால் மேல், சபரகமுவ, மத்திய மற்றும் தென் மாகாணங்களில் மழை பெய்யக்கூடும்.",
    monsoonNortheastDesc = "வடகிழக்கு காற்றால் வடக்கு, கிழக்கு மற்றும் வடமத்திய மாவட்டங்களில் மழை பெய்யக்கூடும்.",
    monsoonFirstInterDesc = "பிற்பகல் மற்றும் மாலை வேளைகளில் இடியுடன் கூடிய மழை பெய்ய வாய்ப்புள்ளது.",
    monsoonSecondInterDesc = "நாடு முழுவதும் மாலை நேரங்களில் இடியுடன் கூடிய மழை பெய்யக்கூடும்.",
    activeMonsoonSeason = "பருவநிலை முறை",
    regionalImpact = "உள்ளூர் காலநிலை மண்டலம்",
    airQuality = "காற்றின் தரம்",
    usAqi = "US AQI",
    euAqi = "EU AQI",
    pm25 = "PM2.5",
    pm10 = "PM10",
    ozone = "O₃",
    no2 = "NO₂",
    airQualityUnavailable = "காற்றின் தர தகவல் தற்போது இல்லை",
    aqiGood = "நன்று",
    aqiFair = "நியாயமானது",
    aqiModerate = "மிதமானது",
    aqiPoor = "மோசமானது",
    aqiVeryPoor = "மிக மோசம்",
    uvLow = "குறைவு",
    uvModerate = "மிதமானது",
    uvHigh = "அதிகம்",
    uvVeryHigh = "மிக அதிகம்",
    uvExtreme = "தீவிரம்",
    rainNone = "வறண்ட / மிகக் குறைவு",
    rainLight = "லேசான",
    rainModerate = "மிதமான",
    rainHeavy = "கனமழை",
    rainVeryHeavy = "மிக கனமழை",
    noRainyPeriodsExpected = "அடுத்த 24 மணி நேரத்தில் குறிப்பிடத்தக்க மழை எதிர்பார்க்கப்படவில்லை",
    selectLocation = "இலங்கை இடத்தை தேர்ந்தெடுக்கவும்",
    searchCitiesDistricts = "நகரம் அல்லது மாவட்டத்தைத் தேடுங்கள்…",
    useCurrentLocation = "என் இருப்பிடம் (GPS)",
    gpsLocating = "இருப்பிடம் கண்டறியப்படுகிறது…",
    gpsPermissionRequired = "இருப்பிட அனுமதி விருப்பமானது. அருகிலுள்ள மாவட்டத்தை அறிய அனுமதிக்கவும்.",
    gpsDisabledMessage = "இருப்பிடம் கிடைக்கவில்லை. கீழே மாவட்டத்தைத் தேர்ந்தெடுக்கவும்.",
    favoriteLocations = "பிடித்த இடங்கள்",
    all25Districts = "அனைத்து 25 மாவட்டங்கள்",
    townsAndCities = "முக்கிய நகரங்கள்",
    searchResults = "தேடல் முடிவுகள்",
    noMatchingLocations = "பொருந்தும் இடங்கள் இல்லை",
    compareLocations = "மாவட்டங்களை ஒப்பிடுக",
    addLocationsToCompare = "நேரடி வானிலையை ஒப்பிட பிடித்த இடங்களைச் சேர்க்கவும்.",
    comparisonHint = "சேமித்த மாவட்டங்களின் நேரடி ஒப்பீடு",
    viewDashboard = "முகப்பை திற",
    weatherMapTitle = "இலங்கை வானிலை வரைபடம்",
    layerRadar = "மழை ரேடார்",
    layerTemp = "வெப்பநிலை",
    layerRain = "மழைப்பொழிவு",
    playRadar = "ரேடார் இயக்கு",
    pauseRadar = "நிறுத்து",
    radarUnavailable = "ஆஃப்லைனில் நேரடி ரேடார் இல்லை",
    radarLiveNote = "RainViewer நேரடி மழை ரேடார் மற்றும் 25 மாவட்ட நிலையங்கள்",
    districtTapHint = "விவரங்களைக் காண மாவட்ட குறியீட்டைத் தட்டவும்",
    alertsTitle = "வானிலை எச்சரிக்கைகள்",
    officialAlertsHeader = "அதிகாரப்பூர்வ இலங்கை எச்சரிக்கைகள்",
    officialSourceLabel = "மூலம்",
    sourceDeptMet = "இலங்கை வளிமண்டலவியல் திணைக்களம் / GDACS",
    publishedAtLabel = "வெளியீடு",
    affectedAreasLabel = "பாதிக்கப்படும் பகுதிகள்",
    expiresAtLabel = "செல்லுபடியாகும் நேரம்",
    noOfficialAlertsTitle = "செயலில் உள்ள அதிகாரப்பூர்வ எச்சரிக்கை இல்லை",
    noOfficialAlertsDesc = "பெறப்பட்ட அதிகாரப்பூர்வ தரவில் இலங்கைக்கு கடுமையான வானிலை எச்சரிக்கைகள் எதுவும் இல்லை. வளிமண்டலவியல் திணைக்களத்துடன் உறுதிப்படுத்தவும்.",
    unavailableAlertsTitle = "அதிகாரப்பூர்வ எச்சரிக்கைகள் கிடைக்கவில்லை",
    unavailableAlertsDesc = "இலங்கை வளிமண்டலவியல் திணைக்களத்தின் நேரடி அறிவிப்புகளை இப்போது பெற முடியவில்லை. போலி எச்சரிக்கைகள் உருவாக்கப்படாது.",
    openOfficialMetPortal = "meteo.gov.lk திறக்கவும்",
    modelObservationsHeader = "முன்னறிவிப்பு மாதிரி அவதானிப்புகள்",
    modelObservationsDisclaimer = "Open-Meteo கணிப்பிலிருந்து பெறப்பட்டது — இது அதிகாரப்பூர்வ அரசாங்க எச்சரிக்கை அல்ல.",
    noSignificantModelObs = "இன்றைய முன்னறிவிப்பில் அதிக மழை, காற்று அல்லது UV அளவுகள் இல்லை.",
    preparednessTitle = "வானிலை பாதுகாப்பு வழிகாட்டி",
    preparednessFlood = "வெள்ளம் & மண்சரிவு: கனமழையின் போது வெள்ளம் சூழ்ந்த சாலைகள் மற்றும் மலைச் சரிவுகளைத் தவிர்க்கவும்.",
    preparednessLightning = "மின்னல் பாதுகாப்பு: மாலை நேர இடி மழையின் போது பாதுகாப்பான கட்டிடங்களுக்குள் இருக்கவும்.",
    preparednessWind = "கடலோரக் காற்று: மீனவர்கள் மற்றும் பயணிகள் கடல் வானிலை அறிவிப்புகளைப் பார்க்கவும்.",
    settingsTitle = "அமைப்புகள்",
    languageLabel = "மொழி",
    temperatureUnitLabel = "வெப்பநிலை அலகு",
    windSpeedUnitLabel = "காற்று வேக அலகு",
    timeFormatLabel = "நேர வடிவம்",
    themeModeLabel = "தோற்றம்",
    themeSystem = "கணினி",
    themeLight = "வெளிச்சம்",
    themeDark = "இருள்",
    time12Hour = "12-மணி",
    time24Hour = "24-மணி",
    locationPreferences = "இருப்பிட அமைப்புகள்",
    defaultDistrict = "தேர்ந்தெடுத்த மாவட்டம்",
    autoGpsSwitch = "தொடக்கத்தில் GPS பயன்படுத்து",
    refreshAndNotifications = "புதுப்பிப்பு & அறிவிப்புகள்",
    autoRefreshLaunch = "தொடக்கத்தில் தானாக புதுப்பி",
    weatherNotifications = "வானிலை அறிவிப்புகள்",
    severeAlertNotif = "கனமழை & புயல் அறிவிப்புகள்",
    rainNotif = "தினசரி மழை முன்னோட்டம்",
    sendSampleNotif = "மாதிரி அறிவிப்பை அனுப்பு",
    notificationPermissionNeeded = "வானிலை அறிவிப்புகளைப் பெற அனுமதி அளிக்கவும்",
    notificationSentToast = "வானிலை அறிவிப்பு அனுப்பப்பட்டது",
    notificationChannelName = "லங்கா வானிலை அறிவிப்புகள்",
    notificationSummaryTitle = "%s வானிலை",
    dataSourcesTitle = "வானிலை தரவு மூலங்கள்",
    dataSourcesDesc = "நேரடி முன்னறிவிப்பு & காற்றின் தரம்: Open-Meteo. மழை ரேடார்: RainViewer. வரைபடம்: OpenStreetMap. அதிகாரப்பூர்வ அறிவிப்பு: இலங்கை வளிமண்டலவியல் திணைக்களம்.",
    aboutAppTitle = "லங்கா வானிலை பற்றி",
    aboutAppDesc = "லங்கா வானிலை v1.0 • இலங்கையின் 25 மாவட்டங்களுக்காக உருவாக்கப்பட்டது.",
    forecastPredictionBadge = "கணிப்பு",
    dayDetailsTitle = "தினசரி முன்னறிவிப்பு விவரங்கள்",
    dominantWind = "முதன்மை காற்று திசை",
    maxUv = "அதிகபட்ச UV",
    totalRain = "24மணி மழை",
    todayLabel = "இன்று",
    tomorrowLabel = "நாளை",
    currentTimeZoneNote = "இலங்கை நேரம் (IST, UTC+5:30)"
)

fun stringsForLanguage(code: String): AppStrings = when (code) {
    "si" -> SinhalaStrings
    "ta" -> TamilStrings
    else -> EnglishStrings
}

val LocalAppStrings = staticCompositionLocalOf { EnglishStrings }
