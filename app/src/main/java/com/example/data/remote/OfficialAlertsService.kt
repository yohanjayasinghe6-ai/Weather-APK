package com.example.data.remote

import com.example.model.AlertCategory
import com.example.model.AlertSeverity
import com.example.model.AlertsResult
import com.example.model.WeatherAlert
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.StringReader
import java.util.concurrent.TimeUnit

/**
 * Fetches official disaster & meteorological alerts relevant to Sri Lanka from
 * Sri Lanka Dept. of Meteorology (meteo.gov.lk) and GDACS (UN/EC Global Disaster Alert RSS).
 * Strictly reports unavailability rather than generating fake warnings if feeds cannot be retrieved.
 */
open class OfficialAlertsService(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(12, TimeUnit.SECONDS)
        .build()
) {

    companion object {
        const val MET_GOV_LK_URL = "https://www.meteo.gov.lk/"
        const val GDACS_RSS_URL = "https://www.gdacs.org/xml/rss.xml"
    }

    open suspend fun fetchOfficialAlerts(): AlertsResult = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val alerts = mutableListOf<WeatherAlert>()
        var anySourceSucceeded = false
        val successfulSources = mutableListOf<String>()

        // 1. Attempt Sri Lanka Department of Meteorology portal
        runCatching {
            val request = Request.Builder()
                .url(MET_GOV_LK_URL)
                .header("User-Agent", "LankaWeatherAndroid/1.0")
                .build()
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val body = response.body?.string().orEmpty()
                    if (body.isNotBlank()) {
                        anySourceSucceeded = true
                        successfulSources.add("Sri Lanka Dept. of Meteorology")
                        alerts.addAll(parseMeteoGovHtml(body))
                    }
                }
            }
        }

        // 2. Attempt GDACS official UN/EC RSS feed (filtered strictly to Sri Lanka / Bay of Bengal affecting LK)
        runCatching {
            val request = Request.Builder()
                .url(GDACS_RSS_URL)
                .header("User-Agent", "LankaWeatherAndroid/1.0")
                .build()
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val xml = response.body?.string().orEmpty()
                    if (xml.isNotBlank()) {
                        anySourceSucceeded = true
                        successfulSources.add("GDACS Official Bulletin")
                        alerts.addAll(parseGdacsRss(xml))
                    }
                }
            }
        }

        val sourceLabel = successfulSources.joinToString(" • ")
            .ifBlank { "Sri Lanka Dept. of Meteorology / GDACS" }

        when {
            !anySourceSucceeded -> AlertsResult.Unavailable(
                reason = "Unable to connect to official Sri Lanka meteorological alert feeds.",
                officialPortalUrl = MET_GOV_LK_URL,
                fetchedAtMillis = now
            )
            alerts.isEmpty() -> AlertsResult.NoActiveAlerts(
                sourceLabel = sourceLabel,
                fetchedAtMillis = now
            )
            else -> AlertsResult.Available(
                alerts = alerts.distinctBy { it.title },
                sourceLabel = sourceLabel,
                fetchedAtMillis = now
            )
        }
    }

    internal fun parseMeteoGovHtml(html: String): List<WeatherAlert> {
        // Parse explicit warning/advisory blocks only if clearly present on meteo.gov.lk
        val results = mutableListOf<WeatherAlert>()
        val warningRegex = Regex(
            pattern = """<(?:a|h\d|div|p)[^>]*>(.*?(?:Advisory|Warning)\s+for\s+(?:Heavy\s+Rain|Strong\s+Winds|Thunderstorms|Cyclone|Rough\s+Sea).*?)</(?:a|h\d|div|p)>""",
            options = setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
        )
        warningRegex.findAll(html).take(5).forEachIndexed { idx, match ->
            val rawText = match.groupValues[1]
                .replace(Regex("<[^>]+>"), " ")
                .replace(Regex("\\s+"), " ")
                .trim()
            if (rawText.length in 15..280) {
                val category = classifyCategory(rawText)
                val severity = classifySeverity(rawText)
                results.add(
                    WeatherAlert(
                        id = "metlk_$idx",
                        title = rawText,
                        summary = "Official bulletin detected on Sri Lanka Department of Meteorology portal. Open the official portal for full PDF/map details.",
                        severity = severity,
                        category = category,
                        source = "Sri Lanka Department of Meteorology",
                        publishedAt = "Live portal check",
                        affectedAreas = "Sri Lanka (see official bulletin)",
                        expiresAt = null,
                        url = MET_GOV_LK_URL
                    )
                )
            }
        }
        return results
    }

    internal fun parseGdacsRss(xml: String): List<WeatherAlert> {
        val alerts = mutableListOf<WeatherAlert>()
        val factory = XmlPullParserFactory.newInstance()
        val parser = factory.newPullParser()
        parser.setInput(StringReader(xml))

        var insideItem = false
        var title = ""
        var description = ""
        var pubDate = ""
        var link = ""
        var country = ""
        var alertLevel = ""
        var eventType = ""

        var event = parser.eventType
        while (event != XmlPullParser.END_DOCUMENT) {
            when (event) {
                XmlPullParser.START_TAG -> {
                    val tag = parser.name.orEmpty()
                    if (tag.equals("item", ignoreCase = true)) {
                        insideItem = true
                        title = ""
                        description = ""
                        pubDate = ""
                        link = ""
                        country = ""
                        alertLevel = ""
                        eventType = ""
                    } else if (insideItem) {
                        when {
                            tag.equals("title", true) -> title = parser.nextText().trim()
                            tag.equals("description", true) -> description = parser.nextText().trim()
                            tag.equals("pubDate", true) -> pubDate = parser.nextText().trim()
                            tag.equals("link", true) -> link = parser.nextText().trim()
                            tag.equals("country", true) -> country = parser.nextText().trim()
                            tag.equals("alertlevel", true) -> alertLevel = parser.nextText().trim()
                            tag.equals("eventtype", true) -> eventType = parser.nextText().trim()
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (parser.name.equals("item", ignoreCase = true) && insideItem) {
                        val mentionsSriLanka = country.contains("Sri Lanka", ignoreCase = true) ||
                            title.contains("Sri Lanka", ignoreCase = true) ||
                            description.contains("Sri Lanka", ignoreCase = true)
                        val isWeatherEvent = eventType.uppercase() in setOf("TC", "FL", "DR", "") ||
                            title.contains("Cyclone", ignoreCase = true) ||
                            title.contains("Flood", ignoreCase = true) ||
                            title.contains("Storm", ignoreCase = true)
                        if (mentionsSriLanka && isWeatherEvent && title.isNotBlank()) {
                            val sev = when (alertLevel.lowercase()) {
                                "red" -> AlertSeverity.EXTREME
                                "orange" -> AlertSeverity.SEVERE
                                else -> AlertSeverity.MODERATE
                            }
                            alerts.add(
                                WeatherAlert(
                                    id = "gdacs_${title.hashCode()}",
                                    title = title,
                                    summary = description.replace(Regex("<[^>]+>"), " ").trim(),
                                    severity = sev,
                                    category = classifyCategory("$title $description"),
                                    source = "GDACS (UN/EC Official Alert)",
                                    publishedAt = pubDate.ifBlank { "Recent" },
                                    affectedAreas = country.ifBlank { "Sri Lanka" },
                                    expiresAt = null,
                                    url = link.ifBlank { null }
                                )
                            )
                        }
                        insideItem = false
                    }
                }
            }
            event = parser.next()
        }
        return alerts
    }

    private fun classifyCategory(text: String): AlertCategory = when {
        text.contains("cyclone", ignoreCase = true) || text.contains("depression", ignoreCase = true) ->
            AlertCategory.CYCLONE
        text.contains("flood", ignoreCase = true) -> AlertCategory.FLOOD
        text.contains("thunder", ignoreCase = true) || text.contains("lightning", ignoreCase = true) ->
            AlertCategory.THUNDERSTORM
        text.contains("wind", ignoreCase = true) -> AlertCategory.STRONG_WIND
        text.contains("rain", ignoreCase = true) -> AlertCategory.HEAVY_RAIN
        else -> AlertCategory.SEVERE_WEATHER
    }

    private fun classifySeverity(text: String): AlertSeverity = when {
        text.contains("red", ignoreCase = true) || text.contains("extreme", ignoreCase = true) ->
            AlertSeverity.EXTREME
        text.contains("warning", ignoreCase = true) || text.contains("severe", ignoreCase = true) ->
            AlertSeverity.SEVERE
        text.contains("advisory", ignoreCase = true) -> AlertSeverity.ADVISORY
        else -> AlertSeverity.MODERATE
    }
}
