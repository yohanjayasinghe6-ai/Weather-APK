package com.example.util

import com.example.localization.AppStrings
import com.example.model.HourlyForecast
import com.example.model.MonsoonSeason
import com.example.model.MonsoonSeasonalProfile
import com.example.model.MonsoonZone
import com.example.model.TemperatureUnit
import com.example.model.TimeFormatPreference
import com.example.model.WindSpeedUnit
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.math.roundToInt

object WeatherFormatters {

    private val colomboTz: TimeZone = TimeZone.getTimeZone("Asia/Colombo")

    fun convertTemperature(tempC: Double, unit: TemperatureUnit): Double = when (unit) {
        TemperatureUnit.CELSIUS -> tempC
        TemperatureUnit.FAHRENHEIT -> (tempC * 9.0 / 5.0) + 32.0
    }

    fun formatTemperature(
        tempC: Double,
        unit: TemperatureUnit,
        includeUnit: Boolean = true
    ): String {
        val value = convertTemperature(tempC, unit).roundToInt()
        return if (includeUnit) {
            val suffix = if (unit == TemperatureUnit.CELSIUS) "°C" else "°F"
            "$value$suffix"
        } else {
            "$value°"
        }
    }

    fun formatWindSpeed(kmh: Double, unit: WindSpeedUnit): String = when (unit) {
        WindSpeedUnit.KMH -> "${kmh.roundToInt()} km/h"
        WindSpeedUnit.MS -> String.format(Locale.US, "%.1f m/s", kmh / 3.6)
        WindSpeedUnit.MPH -> "${(kmh * 0.621371).roundToInt()} mph"
        WindSpeedUnit.KNOTS -> "${(kmh * 0.539957).roundToInt()} kn"
    }

    fun formatRainAmount(mm: Double): String =
        if (mm < 0.05) "0.0 mm" else String.format(Locale.US, "%.1f mm", mm)

    fun formatVisibility(meters: Double): String {
        val km = meters / 1000.0
        return if (km >= 10) "${km.roundToInt()} km" else String.format(Locale.US, "%.1f km", km)
    }

    fun formatPressure(hpa: Double): String = "${hpa.roundToInt()} hPa"

    fun formatIsoTime(isoString: String, formatPref: TimeFormatPreference): String {
        if (isoString.isBlank()) return "--:--"
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US).apply {
            timeZone = colomboTz
        }
        val date = runCatching { parser.parse(isoString.take(16)) }.getOrNull() ?: return isoString
        val pattern = if (formatPref == TimeFormatPreference.HOUR_12) "h:mm a" else "HH:mm"
        return SimpleDateFormat(pattern, Locale.US).apply { timeZone = colomboTz }.format(date)
    }

    fun formatHourLabel(isoString: String, formatPref: TimeFormatPreference): String {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US).apply {
            timeZone = colomboTz
        }
        val date = runCatching { parser.parse(isoString.take(16)) }.getOrNull() ?: return isoString
        val pattern = if (formatPref == TimeFormatPreference.HOUR_12) "ha" else "HH:00"
        return SimpleDateFormat(pattern, Locale.US).apply { timeZone = colomboTz }.format(date)
    }

    fun formatEpochTime(epochMillis: Long, formatPref: TimeFormatPreference): String {
        val pattern = if (formatPref == TimeFormatPreference.HOUR_12) "h:mm a" else "HH:mm"
        return SimpleDateFormat(pattern, Locale.US).apply { timeZone = colomboTz }
            .format(Date(epochMillis))
    }

    fun formatCurrentSriLankaDateTime(
        formatPref: TimeFormatPreference,
        langCode: String
    ): String {
        val locale = Locale.forLanguageTag(langCode)
        val pattern = if (formatPref == TimeFormatPreference.HOUR_12) {
            "EEE, d MMM • h:mm a"
        } else {
            "EEE, d MMM • HH:mm"
        }
        return SimpleDateFormat(pattern, locale).apply { timeZone = colomboTz }.format(Date())
    }

    fun formatDayLabel(
        dateIso: String,
        index: Int,
        strings: AppStrings,
        langCode: String
    ): Pair<String, String> {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = colomboTz }
        val date = runCatching { parser.parse(dateIso) }.getOrNull()
            ?: return dateIso to ""
        val locale = Locale.forLanguageTag(langCode)
        val dayName = when (index) {
            0 -> strings.todayLabel
            1 -> strings.tomorrowLabel
            else -> SimpleDateFormat("EEEE", locale).apply { timeZone = colomboTz }.format(date)
        }
        val dateShort = SimpleDateFormat("d MMM", locale).apply { timeZone = colomboTz }.format(date)
        return dayName to dateShort
    }

    fun rainIntensityLabel(mm: Double, strings: AppStrings): String = when {
        mm < 0.2 -> strings.rainNone
        mm < 2.5 -> strings.rainLight
        mm < 7.6 -> strings.rainModerate
        mm < 25.0 -> strings.rainHeavy
        else -> strings.rainVeryHeavy
    }

    fun uvCategoryLabel(uv: Double, strings: AppStrings): String = when {
        uv < 3.0 -> strings.uvLow
        uv < 6.0 -> strings.uvModerate
        uv < 8.0 -> strings.uvHigh
        uv < 11.0 -> strings.uvVeryHigh
        else -> strings.uvExtreme
    }

    fun aqiCategoryLabel(euAqi: Int?, strings: AppStrings): String = when {
        euAqi == null -> "--"
        euAqi <= 20 -> strings.aqiGood
        euAqi <= 40 -> strings.aqiFair
        euAqi <= 60 -> strings.aqiModerate
        euAqi <= 80 -> strings.aqiPoor
        else -> strings.aqiVeryPoor
    }

    fun currentMonsoonProfile(
        zone: MonsoonZone,
        monthOneBased: Int = Calendar.getInstance(colomboTz).get(Calendar.MONTH) + 1
    ): MonsoonSeasonalProfile {
        val season = when (monthOneBased) {
            5, 6, 7, 8, 9 -> MonsoonSeason.SOUTHWEST_MONSOON
            12, 1, 2 -> MonsoonSeason.NORTHEAST_MONSOON
            3, 4 -> MonsoonSeason.FIRST_INTER_MONSOON
            else -> MonsoonSeason.SECOND_INTER_MONSOON
        }
        val range = when (season) {
            MonsoonSeason.SOUTHWEST_MONSOON -> "May – Sep"
            MonsoonSeason.NORTHEAST_MONSOON -> "Dec – Feb"
            MonsoonSeason.FIRST_INTER_MONSOON -> "Mar – Apr"
            MonsoonSeason.SECOND_INTER_MONSOON -> "Oct – Nov"
        }
        val isPrimary = when (season) {
            MonsoonSeason.SOUTHWEST_MONSOON ->
                zone == MonsoonZone.SOUTHWEST_WET || zone == MonsoonZone.CENTRAL_HIGHLANDS
            MonsoonSeason.NORTHEAST_MONSOON ->
                zone == MonsoonZone.NORTH_EAST_DRY || zone == MonsoonZone.INTERMEDIATE
            MonsoonSeason.FIRST_INTER_MONSOON, MonsoonSeason.SECOND_INTER_MONSOON -> true
        }
        return MonsoonSeasonalProfile(
            season = season,
            activeMonthsRange = range,
            isSelectedZonePrimaryRainSeason = isPrimary
        )
    }

    fun computeExpectedRainyPeriods(
        next24Hours: List<HourlyForecast>,
        timeFormat: TimeFormatPreference,
        strings: AppStrings
    ): String {
        val rainyHours = next24Hours.filter {
            it.rainProbabilityPercent >= 45 || it.precipitationMm >= 0.3
        }
        if (rainyHours.isEmpty()) return strings.noRainyPeriodsExpected

        // Group contiguous hours into readable time ranges
        val windows = mutableListOf<Pair<HourlyForecast, HourlyForecast>>()
        var start = rainyHours.first()
        var prev = start
        for (i in 1 until rainyHours.size) {
            val curr = rainyHours[i]
            if (curr.epochMillis - prev.epochMillis <= 2 * 3600_000L) {
                prev = curr
            } else {
                windows.add(start to prev)
                start = curr
                prev = curr
            }
        }
        windows.add(start to prev)

        return windows.take(3).joinToString(" • ") { (s, e) ->
            val sLabel = formatHourLabel(s.timeIso, timeFormat)
            val eLabel = formatHourLabel(e.timeIso, timeFormat)
            if (sLabel == eLabel) sLabel else "$sLabel–$eLabel"
        }
    }

    fun computeDaylightProgress(sunriseIso: String, sunsetIso: String): Float {
        if (sunriseIso.isBlank() || sunsetIso.isBlank()) return 0.5f
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US).apply {
            timeZone = colomboTz
        }
        val rise = runCatching { parser.parse(sunriseIso.take(16))?.time }.getOrNull() ?: return 0.5f
        val set = runCatching { parser.parse(sunsetIso.take(16))?.time }.getOrNull() ?: return 0.5f
        if (set <= rise) return 0.5f
        val now = System.currentTimeMillis()
        return ((now - rise).toFloat() / (set - rise).toFloat()).coerceIn(0f, 1f)
    }
}
