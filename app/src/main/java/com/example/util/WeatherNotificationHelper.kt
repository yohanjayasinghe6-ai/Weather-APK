package com.example.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R
import com.example.localization.AppStrings
import com.example.model.SriLankaLocation
import com.example.model.TemperatureUnit
import com.example.model.WeatherBundle

object WeatherNotificationHelper {

    private const val CHANNEL_ID = "lanka_weather_advisories"
    private const val SUMMARY_NOTIF_ID = 1001

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun sendWeatherSummaryNotification(
        context: Context,
        location: SriLankaLocation,
        bundle: WeatherBundle,
        tempUnit: TemperatureUnit,
        langCode: String,
        strings: AppStrings
    ): Boolean {
        if (!hasNotificationPermission(context)) return false

        ensureChannel(context, strings)

        val locName = location.localizedName(langCode)
        val tempStr = WeatherFormatters.formatTemperature(bundle.current.temperatureC, tempUnit)
        val desc = strings.weatherDescription(bundle.current.weatherCode, bundle.current.isDay)
        val rainProb = bundle.daily.firstOrNull()?.rainProbabilityMaxPercent ?: 0
        val rainSum = WeatherFormatters.formatRainAmount(bundle.daily.firstOrNull()?.rainSumMm ?: 0.0)

        val title = String.format(strings.notificationSummaryTitle, locName)
        val body = "$tempStr • $desc • ${strings.rainProbability}: $rainProb% ($rainSum)"

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        return runCatching {
            NotificationManagerCompat.from(context).notify(SUMMARY_NOTIF_ID, notification)
            true
        }.getOrDefault(false)
    }

    private fun ensureChannel(context: Context, strings: AppStrings) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                ?: return
            val channel = NotificationChannel(
                CHANNEL_ID,
                strings.notificationChannelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }
    }
}
