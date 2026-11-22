package com.example.data.remote

import com.squareup.moshi.JsonClass
import retrofit2.http.GET

@JsonClass(generateAdapter = true)
data class RainViewerResponse(
    val host: String = "https://tilecache.rainviewer.com",
    val radar: RainViewerRadarDto? = null
)

@JsonClass(generateAdapter = true)
data class RainViewerRadarDto(
    val past: List<RainViewerFrameDto> = emptyList(),
    val nowcast: List<RainViewerFrameDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class RainViewerFrameDto(
    val time: Long = 0L,
    val path: String = ""
)

interface RainViewerApi {
    @GET("public/weather-maps.json")
    suspend fun getWeatherMaps(): RainViewerResponse
}
